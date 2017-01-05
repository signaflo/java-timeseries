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
import org.knowm.xchart.style.markers.None;

import com.google.common.primitives.Doubles;

import stats.distributions.StudentsT;
import timeseries.TimeSeries;

/**
 * A mean model forecast.
 *
 * @author Jacob Rachiele
 *
 */
public final class MeanForecast implements Forecast {
  
  private final Model model;
  private final TimeSeries forecast;
  private final TimeSeries upperValues;
  private final TimeSeries lowerValues;
  private final double criticalValue;
  private final double fcstStdError;
  private final TimeSeries fcstErrors;
  
  public MeanForecast(final Model model, final int steps, final double alpha) {
    this.model = model;
    this.forecast = model.pointForecast(steps);
    this.criticalValue = new StudentsT(model.timeSeries().n() - 1).quantile(1 - alpha/2);
    this.fcstStdError = model.timeSeries().stdDeviation() * Math.sqrt(1 + (1/model.timeSeries().n()));
    this.fcstErrors = getFcstErrors();
    this.upperValues = computeUpperPredictionValues(steps, alpha);
    this.lowerValues = computeLowerPredictionValues(steps, alpha);
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
  public TimeSeries computeUpperPredictionValues(final int steps, final double alpha) {
    double fcstStdError = model.timeSeries().stdDeviation() * Math.sqrt(1 + (1/model.timeSeries().n()));
    double[] upperPredictionValues = new double[steps];
    double criticalValue = new StudentsT(model.timeSeries().n() - 1).quantile(1 - alpha/2);
    for (int t = 0; t < steps; t++) {
      upperPredictionValues[t] = forecast.at(t) + criticalValue * fcstStdError;
    }
    return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
  }
  
  @Override
  public TimeSeries computeLowerPredictionValues(final int steps, final double alpha) {
    double fcstStdError = model.timeSeries().stdDeviation() * Math.sqrt(1 + (1/model.timeSeries().n()));
    double[] upperPredictionValues = new double[steps];
    double criticalValue = new StudentsT(model.timeSeries().n() - 1).quantile(1 - alpha/2);
    for (int t = 0; t < steps; t++) {
      upperPredictionValues[t] = forecast.at(t) - criticalValue * fcstStdError;
    }
    return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
  }
  
  @Override
  public void plotForecast() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(forecast.observationTimes().size());
      for (OffsetDateTime dateTime : forecast.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      
      List<Double> errorList = Doubles.asList(fcstErrors.series());
      List<Double> forecastList = Doubles.asList(forecast.series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
          .title("Mean Forecast").build();
      
      chart.setXAxisTitle("Time");
      chart.setYAxisTitle("Forecast Values");
      chart.getStyler().setAxisTitleFont(new Font("Arial", Font.PLAIN, 14)).setMarkerSize(5);
      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED)
      .setChartFontColor(new Color(112, 112, 112));
     
      XYSeries forecastSeries = chart.addSeries("Forecast", xAxis, forecastList, errorList);
      forecastSeries.setMarker(new Circle())
      .setMarkerColor(Color.BLACK)
      .setLineWidth(1.5f)
      .setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Mean Forecast");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }
  
  @Override
  public void plot() {
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
          .title("Mean Forecast Past and Future").build();

      XYSeries observationSeries = chart.addSeries("Past", xAxisObs, seriesList);
      XYSeries forecastSeries = chart.addSeries("Future", xAxis, forecastList, errorList);

      observationSeries.setMarker(new None());
      forecastSeries.setMarker(new None());

      observationSeries.setLineWidth(0.75f);
      forecastSeries.setLineWidth(1.5f);

      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line).setErrorBarsColor(Color.RED);
      observationSeries.setLineColor(Color.BLACK);
      forecastSeries.setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Mean Forecast Past and Future");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }

  private TimeSeries getFcstErrors() {
    double[] errors = new double[forecast.n()];
    for (int t = 0; t < errors.length; t++) {
      errors[t] = criticalValue * fcstStdError;
    }
    return new TimeSeries(forecast.timePeriod(), forecast.observationTimes().get(0), errors);
  }
  
}
