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

import math.stats.distributions.Normal;
import timeseries.TimeSeries;
import timeseries.models.Forecast;

/**
 * A pointForecast for an ARIMA model. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class ArimaForecast implements Forecast {

    private final TimeSeries pointForecast;
    private final TimeSeries lowerValues;
    private final TimeSeries upperValues;
    private final double alpha;

    ArimaForecast(TimeSeries pointForecast, TimeSeries lowerValues, TimeSeries upperValues, double alpha) {
        this.pointForecast = pointForecast;
        this.lowerValues = lowerValues;
        this.upperValues = upperValues;
        this.alpha = alpha;
    }

    @Override
    public TimeSeries pointForecast() {
        return this.pointForecast;
    }

    @Override
    public TimeSeries upperPredictionInterval() {
        return this.upperValues;
    }

    @Override
    public TimeSeries lowerPredictionInterval() {
        return this.lowerValues;
    }


    //********** Plots **********//
//    private TimeSeries getFcstErrors(TimeSeries forecast, final double criticalValue) {
//        double[] errors = getStdErrors(forecast, criticalValue);
//        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), errors);
//    }
//
//    @Override
//    public void plot() {
//        new Thread(() -> {
//            final List<Date> xAxis = new ArrayList<>(pointForecast.observationTimes().size());
//            final List<Date> xAxisObs = new ArrayList<>(model.timeSeries().size());
//            for (OffsetDateTime dateTime : model.timeSeries().observationTimes()) {
//                xAxisObs.add(Date.from(dateTime.toInstant()));
//            }
//            for (OffsetDateTime dateTime : pointForecast.observationTimes()) {
//                xAxis.add(Date.from(dateTime.toInstant()));
//            }
//
//            List<Double> errorList = Doubles.asList(fcstErrors.asArray());
//            List<Double> seriesList = Doubles.asList(model.timeSeries().asArray());
//            List<Double> forecastList = Doubles.asList(pointForecast.asArray());
//            final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(800).width(1200)
//                                                      .title("ARIMA Forecast").build();
//
//            XYSeries observationSeries = chart.addSeries("Past", xAxisObs, seriesList);
//            XYSeries forecastSeries = chart.addSeries("Future", xAxis, forecastList, errorList);
//
//            observationSeries.setMarker(new Circle());
//            observationSeries.setMarkerColor(Color.DARK_GRAY);
//            forecastSeries.setMarker(new Circle());
//            forecastSeries.setMarkerColor(Color.BLUE);
//
//            observationSeries.setLineWidth(1.0f);
//            forecastSeries.setLineWidth(1.0f);
//
//            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED);
//            observationSeries.setLineColor(Color.DARK_GRAY);
//            forecastSeries.setLineColor(Color.BLUE);
//
//            JPanel panel = new XChartPanel<>(chart);
//            JFrame frame = new JFrame("ARIMA Forecast");
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.add(panel);
//            frame.pack();
//            frame.setVisible(true);
//        }).start();
//    }
//
//    @Override
//    public void plotForecast() {
//        new Thread(() -> {
//            final List<Date> xAxis = new ArrayList<>(pointForecast.observationTimes().size());
//            for (OffsetDateTime dateTime : pointForecast.observationTimes()) {
//                xAxis.add(Date.from(dateTime.toInstant()));
//            }
//
//            List<Double> errorList = Doubles.asList(fcstErrors.asArray());
//            List<Double> forecastList = Doubles.asList(pointForecast.asArray());
//            final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
//                                                      .title("ARIMA Forecast").build();
//
//            chart.setXAxisTitle("Time");
//            chart.setYAxisTitle("Forecast Values");
//            chart.getStyler().setAxisTitleFont(new Font("Arial", Font.PLAIN, 14));
//            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED)
//                 .setChartFontColor(new Color(112, 112, 112));
//
//            XYSeries forecastSeries = chart.addSeries("Forecast", xAxis, forecastList, errorList);
//            forecastSeries.setMarker(new Circle());
//            forecastSeries.setMarkerColor(Color.BLUE);
//            forecastSeries.setLineWidth(1.0f);
//            forecastSeries.setLineColor(Color.BLUE);
//
//            JPanel panel = new XChartPanel<>(chart);
//            JFrame frame = new JFrame("ARIMA Forecast");
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.add(panel);
//            frame.pack();
//            frame.setVisible(true);
//        }).start();
//    }
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
        for (int i = 0; i < this.pointForecast.size(); i++) {
            builder.append(String.format("%-18.18s", "| " + pointForecast.observationTimes().get(i).toLocalDateTime()))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " +  Double.toString(pointForecast.at(i))))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " + Double.toString(this.lowerValues.at(i))))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " + Double.toString(this.upperValues.at(i))))
                   .append(" |")
                   .append(newLine);
        }
        return builder.toString();
    }
}
