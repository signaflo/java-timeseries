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
package timeseries.models;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import timeseries.TimeSeries;

/**
 * A mean model forecast.
 *
 * @author Jacob Rachiele
 */
@EqualsAndHashCode @ToString
final class MeanForecast implements Forecast {

    private final TimeSeries forecast;
    private final TimeSeries upperValues;
    private final TimeSeries lowerValues;

    MeanForecast(TimeSeries pointForecast, TimeSeries lowerValues, TimeSeries upperValues) {
        this.forecast = pointForecast;
        this.lowerValues = lowerValues;
        this.upperValues = upperValues;
    }

    @Override
    public TimeSeries pointForecast() {
        return this.forecast;
    }

    @Override
    public TimeSeries upperPredictionInterval() {
        return this.upperValues;
    }

    @Override
    public TimeSeries lowerPredictionInterval() {
        return this.lowerValues;
    }


//    @Override
//    public void plotForecast() {
//        new Thread(() -> {
//            final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
//            for (OffsetDateTime dateTime : forecast.observationTimes()) {
//                xAxis.add(Date.from(dateTime.toInstant()));
//            }
//
//            List<Double> errorList = Doubles.asList(fcstErrors.asArray());
//            List<Double> forecastList = Doubles.asList(forecast.asArray());
//            final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
//                                                      .title("Mean Forecast").build();
//
//            chart.setXAxisTitle("Time");
//            chart.setYAxisTitle("Forecast Values");
//            chart.getStyler().setAxisTitleFont(new Font("Arial", Font.PLAIN, 14)).setMarkerSize(5);
//            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED)
//                 .setChartFontColor(new Color(112, 112, 112));
//
//            XYSeries forecastSeries = chart.addSeries("Forecast", xAxis, forecastList, errorList);
//            forecastSeries.setMarker(new Circle()).setMarkerColor(Color.BLACK).setLineWidth(1.5f)
//                          .setLineColor(Color.BLUE);
//
//            JPanel panel = new XChartPanel<>(chart);
//            JFrame frame = new JFrame("Mean Forecast");
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.add(panel);
//            frame.pack();
//            frame.setVisible(true);
//        }).start();
//    }
//
//    @Override
//    public void plot() {
//        new Thread(() -> {
//            final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
//            final List<Date> xAxisObs = new ArrayList<>(model.timeSeries().size());
//            for (OffsetDateTime dateTime : model.timeSeries().observationTimes()) {
//                xAxisObs.add(Date.from(dateTime.toInstant()));
//            }
//            for (OffsetDateTime dateTime : forecast.observationTimes()) {
//                xAxis.add(Date.from(dateTime.toInstant()));
//            }
//
//            List<Double> errorList = Doubles.asList(fcstErrors.asArray());
//            List<Double> seriesList = Doubles.asList(model.timeSeries().asArray());
//            List<Double> forecastList = Doubles.asList(forecast.asArray());
//            final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(800).width(1200)
//                                                      .title("Mean Forecast Past and Future").build();
//
//            XYSeries observationSeries = chart.addSeries("Past", xAxisObs, seriesList);
//            XYSeries forecastSeries = chart.addSeries("Future", xAxis, forecastList, errorList);
//
//            observationSeries.setMarker(new None());
//            forecastSeries.setMarker(new None());
//
//            observationSeries.setLineWidth(0.75f);
//            forecastSeries.setLineWidth(1.5f);
//
//            chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED);
//            observationSeries.setLineColor(Color.BLACK);
//            forecastSeries.setLineColor(Color.BLUE);
//
//            JPanel panel = new XChartPanel<>(chart);
//            JFrame frame = new JFrame("Mean Forecast Past and Future");
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.add(panel);
//            frame.pack();
//            frame.setVisible(true);
//        }).start();
//    }

}
