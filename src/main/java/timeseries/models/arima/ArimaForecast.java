package timeseries.models.arima;

import static java.lang.Math.sqrt;
import java.awt.Color;
import java.awt.Font;
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

public final class ArimaForecast implements Forecast {
  
  private final Arima model;
  private final TimeSeries forecast;
  private final TimeSeries upperValues;
  private final TimeSeries lowerValues;
  private final double criticalValue;
  private final TimeSeries fcstErrors;

  public ArimaForecast(final Arima model, final int steps, final double alpha) {
    this.model = model;
    this.forecast = model.pointForecast(steps);
    this.criticalValue = new Normal().quantile(1 - alpha / 2);
    this.fcstErrors = getFcstErrors(this.criticalValue);
    this.upperValues = computeUpperPredictionValues(steps, alpha);
    this.lowerValues = computeLowerPredictionValues(steps, alpha);
  }
  
  public ArimaForecast(final Arima model, final int steps) {
    this(model, steps, 0.05);
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
    LagPolynomial arPoly = LagPolynomial.autoRegressive(model.arSarCoefficients());
    LagPolynomial diffPoly = LagPolynomial.differences(model.order().d);
    LagPolynomial seasDiffPoly = LagPolynomial.seasonalDifferences(model.seasonalFrequency(), model.order().D);
    double[] phi = diffPoly.times(seasDiffPoly).times(arPoly).inverseParams();
    double[] theta = model.maSmaCoefficients();
    final double[] psi = new double[this.forecast.n()];
    psi[0] = 1.0;
    for (int i = 0; i < theta.length; i++) {
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
      forecastSeries.setMarkerColor(Color.RED);

      observationSeries.setLineWidth(1.0f);
      forecastSeries.setLineWidth(1.0f);

      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.DARK_GRAY);
      observationSeries.setLineColor(Color.DARK_GRAY);
      forecastSeries.setLineColor(Color.RED);

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
      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.DARK_GRAY)
      .setChartFontColor(new Color(112, 112, 112));     
      
      XYSeries forecastSeries = chart.addSeries("Forecast", xAxis, forecastList, errorList);
      forecastSeries.setMarker(new Circle());
      forecastSeries.setMarkerColor(Color.GRAY);
      forecastSeries.setLineWidth(1.0f);
      forecastSeries.setLineColor(Color.DARK_GRAY);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("ARIMA Forecast");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }
//********** Plots **********//

}
