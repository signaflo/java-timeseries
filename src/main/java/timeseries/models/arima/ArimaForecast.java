/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */
package timeseries.models.arima;

import com.google.common.primitives.Doubles;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.Circle;
import stats.distributions.Normal;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.operators.LagPolynomial;

import javax.swing.*;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.sqrt;

/**
 * A forecast for an ARIMA model. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class ArimaForecast implements Forecast {

    private final Arima model;
    private final TimeSeries forecast;
    private final TimeSeries upperValues;
    private final TimeSeries lowerValues;
    private final double alpha;
    private final double criticalValue;
    private final TimeSeries fcstErrors;

    private ArimaForecast(final Arima model, final int steps, final double alpha) {
        this.model = model;
        this.forecast = model.pointForecast(steps);
        this.alpha = alpha;
        this.criticalValue = new Normal().quantile(1 - alpha / 2);
        this.fcstErrors = getFcstErrors(this.criticalValue);
        this.upperValues = computeUpperPredictionBounds(steps, alpha);
        this.lowerValues = computeLowerPredictionBounds(steps, alpha);
    }

    /**
     * Create a new forecast for the given number of steps ahead using the given ARIMA model and the given
     * &alpha; significance level.
     *
     * @param model a fitted ARIMA model.
     * @param steps the number of forecast steps.
     * @param alpha the significance level for the prediction intervals.
     * @return a new forecast for the given number of steps ahead using the given ARIMA model and the given
     * &alpha; significance level.
     */
    public static ArimaForecast forecast(final Arima model, final int steps, final double alpha) {
        return new ArimaForecast(model, steps, alpha);
    }

    /**
     * Create a new forecast for the given number of steps ahead using the given ARIMA model with a default
     * &alpha; significance level of 0.05.
     *
     * @param model a fitted ARIMA model.
     * @param steps the number of time periods ahead to forecast.
     * @return a new forecast for the given number of steps ahead using the given ARIMA model with a default
     * &alpha; significance level of 0.05.
     */
    public static ArimaForecast forecast(final Arima model, final int steps) {
        return new ArimaForecast(model, steps, 0.05);
    }

    /**
     * Create a new 12 step ahead forecast from the given ARIMA model with a default
     * &alpha; significance level of 0.05.
     *
     * @param model a fitted ARIMA model.
     * @return a new 12 step ahead forecast from the given ARIMA model with a default
     * &alpha; significance level of 0.05.
     */
    public static ArimaForecast forecast(final Arima model) {
        return new ArimaForecast(model, 12, 0.05);
    }

    @Override
    public TimeSeries forecast() {
        return this.forecast;
    }

    @Override
    public TimeSeries upperPredictionValues() {
        return this.upperValues;
    }

    @Override
    public TimeSeries lowerPredictionValues() {
        return this.lowerValues;
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(final int steps, final double alpha) {
        final double criticalValue = new Normal().quantile(1 - alpha / 2);
        double[] upperPredictionValues = new double[steps];
        double[] errors = getStdErrors(criticalValue);
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) + errors[t];
        }
        return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(final int steps, final double alpha) {
        final double criticalValue = new Normal().quantile(alpha / 2);
        double[] lowerPredictionValues = new double[steps];
        double[] errors = getStdErrors(criticalValue);
        for (int t = 0; t < steps; t++) {
            lowerPredictionValues[t] = forecast.at(t) + errors[t];
        }
        return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), lowerPredictionValues);
    }

    private double[] getPsiCoefficients() {
        final int steps = this.forecast.size();
        LagPolynomial arPoly = LagPolynomial.autoRegressive(model.arSarCoefficients());
        LagPolynomial diffPoly = LagPolynomial.differences(model.order().d);
        LagPolynomial seasDiffPoly = LagPolynomial.seasonalDifferences(model.seasonalFrequency(), model.order().D);
        double[] phi = diffPoly.times(seasDiffPoly).times(arPoly).inverseParams();
        double[] theta = model.maSmaCoefficients();
        final double[] psi = new double[steps];
        psi[0] = 1.0;
        System.arraycopy(theta, 0, psi, 1, Math.min(steps - 1, theta.length));
        for (int j = 1; j < psi.length; j++) {
            for (int i = 0; i < Math.min(j, phi.length); i++) {
                psi[j] += psi[j - i - 1] * phi[i];
            }
        }
        return psi;
    }

    private TimeSeries getFcstErrors(final double criticalValue) {
        double[] errors = getStdErrors(criticalValue);
        return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), errors);
    }

    private double[] getStdErrors(final double criticalValue) {
        double[] psiCoeffs = getPsiCoefficients();
        double[] stdErrors = new double[this.forecast.size()];
        double sigma = sqrt(model.sigma2());
        double sd;
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
    public void plot() {
        new Thread(() -> {
            final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
            final List<Date> xAxisObs = new ArrayList<>(model.timeSeries().size());
            for (OffsetDateTime dateTime : model.timeSeries().observationTimes()) {
                xAxisObs.add(Date.from(dateTime.toInstant()));
            }
            for (OffsetDateTime dateTime : forecast.observationTimes()) {
                xAxis.add(Date.from(dateTime.toInstant()));
            }

            List<Double> errorList = Doubles.asList(fcstErrors.asArray());
            List<Double> seriesList = Doubles.asList(model.timeSeries().asArray());
            List<Double> forecastList = Doubles.asList(forecast.asArray());
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
    public void plotForecast() {
        new Thread(() -> {
            final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
            for (OffsetDateTime dateTime : forecast.observationTimes()) {
                xAxis.add(Date.from(dateTime.toInstant()));
            }

            List<Double> errorList = Doubles.asList(fcstErrors.asArray());
            List<Double> forecastList = Doubles.asList(forecast.asArray());
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

    @Override
    public String toString() {
        final String newLine = System.lineSeparator();
        StringBuilder builder = new StringBuilder(newLine);
        builder.append(String.format("%-18.18s", "| Date "))
               .append("  ")
               .append(String.format("%-13.13s", "| Forecast "))
               .append("  ")
               .append(String.format("%-13.13s", "| Lower " + String.format("%.1f", (1 - alpha) * 100) + "%"))
               .append("  ")
               .append(String.format("%-13.13s", "| Upper " + String.format("%.1f", (1 - alpha) * 100) + "%"))
               .append(" |")
               .append(newLine)
               .append(String.format("%-70.70s", " -------------------------------------------------------------- "))
               .append(newLine);
        for (int i = 0; i < this.forecast.size(); i++) {
            builder.append(String.format("%-18.18s", "| " + forecast.observationTimes().get(i).toLocalDateTime()))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " +  Double.toString(forecast.at(i))))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " + Double.toString(this.lowerValues.at(i))))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " + Double.toString(this.upperValues.at(i))))
                   .append(" |")
                   .append(newLine);
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArimaForecast that = (ArimaForecast) o;

        if (Double.compare(that.alpha, alpha) != 0) return false;
        if (Double.compare(that.criticalValue, criticalValue) != 0) return false;
        if (!model.equals(that.model)) return false;
        if (!forecast.equals(that.forecast)) return false;
        if (!upperValues.equals(that.upperValues)) return false;
        if (!lowerValues.equals(that.lowerValues)) return false;
        return fcstErrors.equals(that.fcstErrors);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = model.hashCode();
        result = 31 * result + forecast.hashCode();
        result = 31 * result + upperValues.hashCode();
        result = 31 * result + lowerValues.hashCode();
        temp = Double.doubleToLongBits(alpha);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(criticalValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + fcstErrors.hashCode();
        return result;
    }
}
