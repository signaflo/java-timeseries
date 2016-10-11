/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package timeseries.models.arima;

import static java.lang.Math.sqrt;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.Circle;

import com.google.common.primitives.Doubles;

import stats.distributions.Normal;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.operators.LagPolynomial;

/**
 * A forecast for an ARIMA model.
 * @author Jacob Rachiele
 *
 */
public final class ArimaForecast implements Forecast {
  
  private final Arima model;
  private final TimeSeries forecast;
  private final TimeSeries upperValues;
  private final TimeSeries lowerValues;
  private final double alpha;
  private final double criticalValue;
  private final TimeSeries fcstErrors;  
  
  /**
   * Create a new forecast for the given number of steps from the provided ARIMA model and with the given
   * &alpha; significance level.
   * @param model a fitted ARIMA model.
   * @param steps the number of forecast steps.
   * @param alpha the significance level for the prediction intervals.
   * @return a new forecast for the given number of steps from the provided ARIMA model and with the given
   * &alpha; significance level.
   */
  public static final ArimaForecast forecast(final Arima model, final int steps, final double alpha) {
    return new ArimaForecast(model, steps, alpha);
  }
  
  /**
   * Create a new forecast for the given number of steps from the provided ARIMA model with a default
   * &alpha; significance level of 0.05.
   * @param model a fitted ARIMA model.
   * @param steps the number of forecast steps.
   * @return a new forecast for the given number of steps from the provided ARIMA model with a default
   * &alpha; significance level of 0.05.
   */
  public static final ArimaForecast forecast(final Arima model, final int steps) {
    return new ArimaForecast(model, steps, 0.05);
  }
  
  /**
   * Create a new 12 step ahead forecast from the provided ARIMA model with a default
   * &alpha; significance level of 0.05.
   * @param model a fitted ARIMA model.
   * @return a new 12 step ahead forecast from the provided ARIMA model with a default
   * &alpha; significance level of 0.05.
   */
  public static final ArimaForecast forecast(final Arima model) {
    return new ArimaForecast(model, 12, 0.05);
  }
  
  /**
   * Create a new forecast for the given number of steps from the provided ARIMA model with a default
   * &alpha; significance level of 0.05.
   * @param model a fitted ARIMA model.
   * @param steps the number of forecast steps.
   */
  private ArimaForecast(final Arima model, final int steps) {
    this(model, steps, 0.05);
  }

  /**
   * Create a new forecast for the given number of steps from the provided ARIMA model and with the given
   * &alpha; significance level.
   * @param model a fitted ARIMA model.
   * @param steps the number of forecast steps.
   * @param alpha the significance level for the prediction intervals.
   */
  private ArimaForecast(final Arima model, final int steps, final double alpha) {
    this.model = model;
    this.forecast = model.pointForecast(steps);
    this.alpha = alpha;
    this.criticalValue = new Normal().quantile(1 - alpha / 2);
    this.fcstErrors = getFcstErrors(this.criticalValue);
    this.upperValues = computeUpperPredictionValues(steps, alpha);
    this.lowerValues = computeLowerPredictionValues(steps, alpha);
  }
  
  @Override
  public final TimeSeries forecast() {
    return this.forecast;
  }

  @Override
  public final TimeSeries upperPredictionValues() {
    return this.upperValues;
  }
  
  @Override
  public final TimeSeries lowerPredictionValues() {
    return this.lowerValues;
  }
  
  @Override
  public final TimeSeries computeUpperPredictionValues(final int steps, final double alpha) {
    final double criticalValue = new Normal().quantile(1 - alpha / 2);
    double[] upperPredictionValues = new double[steps];
    double[] errors = getStdErrors(criticalValue);
    for (int t = 0; t < steps; t++) {
      upperPredictionValues[t] = forecast.at(t) + errors[t];
    }
    return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0),
        upperPredictionValues);
  }

  @Override
  public final TimeSeries computeLowerPredictionValues(final int steps, final double alpha) {
    final double criticalValue = new Normal().quantile(alpha / 2);
    double[] lowerPredictionValues = new double[steps];
    double[] errors = getStdErrors(criticalValue);
    for (int t = 0; t < steps; t++) {
      lowerPredictionValues[t] = forecast.at(t) + errors[t];
    }
    return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0),
        lowerPredictionValues);
  }
  
  private final double[] getPsiCoefficients() {
    final int steps = this.forecast.n();
    LagPolynomial arPoly = LagPolynomial.autoRegressive(model.arSarCoefficients());
    LagPolynomial diffPoly = LagPolynomial.differences(model.order().d);
    LagPolynomial seasDiffPoly = LagPolynomial.seasonalDifferences(model.seasonalFrequency(), model.order().D);
    double[] phi = diffPoly.times(seasDiffPoly).times(arPoly).inverseParams();
    double[] theta = model.maSmaCoefficients();
    final double[] psi = new double[steps];
    psi[0] = 1.0;
    for (int i = 0; i < Math.min(steps - 1, theta.length); i++) {
      psi[i + 1] = theta[i];
    }
    for (int j = 1; j < psi.length; j++) {
      for (int i = 0; i < Math.min(j, phi.length); i++) {
        psi[j] += psi[j - i - 1] * phi[i];
      }
    }
    return psi;
  }
  
  private final TimeSeries getFcstErrors(final double criticalValue) {
    double[] errors = getStdErrors(criticalValue);
    return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), errors);
  }
  
  private final double[] getStdErrors(final double criticalValue) {
    double[] psiCoeffs = getPsiCoefficients();
    double[] stdErrors = new double[this.forecast.n()];
    double sigma = sqrt(model.sigma2());
    double sd = 0.0;
    double psiWeightSum = 0.0;
    for (int i = 0; i < stdErrors.length; i++) {
      psiWeightSum += psiCoeffs[i] * psiCoeffs[i];
      sd = sigma * sqrt(psiWeightSum);
      stdErrors[i] = criticalValue * sd;
    }
    return stdErrors;
  }
  
  //********** Plots **********//
  @Override
  public final void plot() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
      final List<Date> xAxisObs = new ArrayList<>(model.timeSeries().n());
      for (OffsetDateTime dateTime : model.timeSeries().observationTimes()) {
        xAxisObs.add(Date.from(dateTime.toInstant()));
      }
      for (OffsetDateTime dateTime : forecast.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }

      List<Double> errorList = Doubles.asList(fcstErrors.series());
      List<Double> seriesList = Doubles.asList(model.timeSeries().series());
      List<Double> forecastList = Doubles.asList(forecast.series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(800).width(1200)
          .title("ARIMA Forecast").build();

      XYSeries observationSeries = chart.addSeries("Past", xAxisObs, seriesList);
      XYSeries forecastSeries = chart.addSeries("Future", xAxis, forecastList, errorList);

      observationSeries.setMarker(new Circle());
      observationSeries.setMarkerColor(Color.DARK_GRAY);
      forecastSeries.setMarker(new Circle());
      forecastSeries.setMarkerColor(Color.BLUE);

      observationSeries.setLineWidth(1.0f);
      forecastSeries.setLineWidth(1.0f);

      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED);
      observationSeries.setLineColor(Color.DARK_GRAY);
      forecastSeries.setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("ARIMA Forecast");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }

  @Override
  public final void plotForecast() {   
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
      for (OffsetDateTime dateTime : forecast.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }

      List<Double> errorList = Doubles.asList(fcstErrors.series());
      List<Double> forecastList = Doubles.asList(forecast.series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
          .title("ARIMA Forecast").build();

      chart.setXAxisTitle("Time");
      chart.setYAxisTitle("Forecast Values");
      chart.getStyler().setAxisTitleFont(new Font("Arial", Font.PLAIN, 14));
      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED)
      .setChartFontColor(new Color(112, 112, 112));     
      
      XYSeries forecastSeries = chart.addSeries("Forecast", xAxis, forecastList, errorList);
      forecastSeries.setMarker(new Circle());
      forecastSeries.setMarkerColor(Color.BLUE);
      forecastSeries.setLineWidth(1.0f);
      forecastSeries.setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("ARIMA Forecast");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }
  //********** Plots **********//

  //********** Boiler Plate **********//
  @Override
  public String toString() {
    NumberFormat numFormatter = new DecimalFormat("#0.0000");
    StringBuilder builder = new StringBuilder();
    builder.append(String.format("%-20s", "| Date ")).append(String.format("%-12s", "| Forecast "))
            .append(String.format("%-12s", " | Lower " + String.format("%.1f", (1 - alpha) * 100) + "%"))
            .append(String.format("%-12s", "  | Upper " + String.format("%.1f", (1 - alpha) * 100) + "%  |\n"));
    builder.append("| ------------------------------------------------------------ |\n");
    for (int i = 0; i < this.forecast.n(); i++) {
      builder.append("| ").append(forecast.observationTimes().get(i).toLocalDateTime()).append("  | ")
      .append(numFormatter.format(forecast.at(i))).append("  | ").append(numFormatter.format(this.lowerValues.at(i)))
      .append("    | ").append(numFormatter.format(this.upperValues.at(i))).append("    |\n");
    }
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(criticalValue);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((fcstErrors == null)? 0 : fcstErrors.hashCode());
    result = prime * result + ((forecast == null)? 0 : forecast.hashCode());
    result = prime * result + ((lowerValues == null)? 0 : lowerValues.hashCode());
    result = prime * result + ((model == null)? 0 : model.hashCode());
    result = prime * result + ((upperValues == null)? 0 : upperValues.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ArimaForecast other = (ArimaForecast) obj;
    if (Double.doubleToLongBits(criticalValue) != Double.doubleToLongBits(other.criticalValue)) return false;
    if (fcstErrors == null) {
      if (other.fcstErrors != null) return false;
    } else if (!fcstErrors.equals(other.fcstErrors)) return false;
    if (forecast == null) {
      if (other.forecast != null) return false;
    } else if (!forecast.equals(other.forecast)) return false;
    if (lowerValues == null) {
      if (other.lowerValues != null) return false;
    } else if (!lowerValues.equals(other.lowerValues)) return false;
    if (model == null) {
      if (other.model != null) return false;
    } else if (!model.equals(other.model)) return false;
    if (upperValues == null) {
      if (other.upperValues != null) return false;
    } else if (!upperValues.equals(other.upperValues)) return false;
    return true;
  }
  //********** Boiler Plate **********//

}
