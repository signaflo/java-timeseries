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

import data.DoubleFunctions;

import timeseries.TimePeriod;
import timeseries.TimeSeries;

/**
 * A time series model that assumes no trend or seasonal factors are present, and that puts as much weight
 * on early values of the series as it does on recent values.
 * 
 * @author Jacob Rachiele
 *
 */
public final class MeanModel implements Model {

  private final TimeSeries timeSeries;
  private final TimeSeries fittedSeries;
  private final double mean;

  public MeanModel(final TimeSeries observed) {
    this.timeSeries = observed;
    this.mean = this.timeSeries.mean();
    this.fittedSeries = new TimeSeries(observed.timePeriod(), observed.observationTimes().get(0),
         DoubleFunctions.fill(observed.n(), this.mean));
  }

  @Override
  public Forecast forecast(final int steps, final double alpha) {
    return new MeanForecast(this, steps, alpha);
  }

  @Override
  public TimeSeries pointForecast(final int steps) {
    int n = timeSeries.n();
    TimePeriod timePeriod = timeSeries.timePeriod();

    final double[] forecasted = DoubleFunctions.fill(steps, this.mean);
    final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
        .plus(timePeriod.periodLength() * timePeriod.timeUnit().unitLength(), timePeriod.timeUnit().temporalUnit());
    return new TimeSeries(timePeriod, startTime, forecasted);
  }
  
  @Override
  public TimeSeries timeSeries() {
    return this.timeSeries;
  }
  
  @Override
  public TimeSeries fittedSeries() {
    return this.fittedSeries;
  }

  @Override
  public TimeSeries residuals() {
    return this.timeSeries.minus(this.fittedSeries);
  }

  @Override
  public void plotResiduals() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(residuals().series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
          .title("Mean Model Residuals").build();
      XYSeries residualSeries = chart.addSeries("Model Residuals", xAxis, seriesList);
      residualSeries.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
      residualSeries.setMarker(new Circle()).setMarkerColor(Color.RED);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Mean Model Residuals");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }

  /**
   * Plot just the model fitted values.
   */
  public void plotFittedValues() {
    this.fittedSeries.plot("Mean Model Fitted Values");
  }

  @Override
  public void plotFit() {

    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(timeSeries.series());
      List<Double> fittedList = com.google.common.primitives.Doubles.asList(fittedSeries.series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
          .title("Mean Model Fitted vs Actual").build();
      XYSeries fitSeries = chart.addSeries("Fitted Values", xAxis, fittedList);
      XYSeries observedSeries = chart.addSeries("Actual Values", xAxis, seriesList);
      
      chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
      
      observedSeries.setLineWidth(0.75f);
      observedSeries.setMarker(new None()).setLineColor(Color.RED);
      fitSeries.setLineWidth(0.75f);
      fitSeries.setMarker(new None()).setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Mean Model Fit");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }

  @Override
  public String toString() {
    return "timeSeries: " + timeSeries + "\nfittedSeries: " + fittedSeries + "\nmean: " + mean;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fittedSeries == null) ? 0 : fittedSeries.hashCode());
    long temp;
    temp = Double.doubleToLongBits(mean);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((timeSeries == null) ? 0 : timeSeries.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MeanModel other = (MeanModel) obj;
    if (fittedSeries == null) {
      if (other.fittedSeries != null) {
        return false;
      }
    } else if (!fittedSeries.equals(other.fittedSeries)) {
      return false;
    }
    if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean)) {
      return false;
    }
    if (timeSeries == null) {
      if (other.timeSeries != null) {
        return false;
      }
    } else if (!timeSeries.equals(other.timeSeries)) {
      return false;
    }
    return true;
  }

}
