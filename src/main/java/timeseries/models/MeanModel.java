/*
 * Copyright (c) 2016 Jacob Rachiele
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
import timeseries.TimeScale;
import timeseries.TimeSeries;

/**
 * This time series model assumes that the series contains no underlying trend or seasonality, and that early values of
 * the series provide as much information as recent values.
 * 
 * @author Jacob Rachiele
 *
 */
public final class MeanModel implements Model {

  private final TimeSeries timeSeries;
  private final TimeSeries fittedSeries;
  private final double mean;

  public MeanModel(final TimeSeries observed) {
    this.timeSeries = observed.copy();
    this.mean = this.timeSeries.mean();
    this.fittedSeries = new TimeSeries(observed.timeScale(), observed.observationTimes().get(0),
        observed.timeScaleLength(), DoubleFunctions.fill(observed.n(), this.mean));
  }
  
  /* (non-Javadoc)
   * @see timeseries.models.Model#newForecast(int, double)
   */
  @Override
  public final Forecast forecast(final int steps, final double alpha) {
    return new MeanForecast(this, steps, alpha);
  }

  @Override
  public final TimeSeries pointForecast(final int steps) {
    int n = timeSeries.n();
    long periodLength = timeSeries.timeScaleLength();
    TimeScale timeScale = timeSeries.timeScale();

    final double[] forecasted = DoubleFunctions.fill(steps, this.mean);
    final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
        .plus(periodLength * timeScale.periodLength(), timeScale.timeUnit());
    return new TimeSeries(timeScale, startTime, periodLength, forecasted);
  }
  
  @Override
  public final TimeSeries timeSeries() {
    return this.timeSeries;
  }
  
  @Override
  public final TimeSeries fittedSeries() {
    return this.fittedSeries;
  }

  @Override
  public final TimeSeries residuals() {
    return this.timeSeries.minus(this.fittedSeries);
  }

  @Override
  public final void plotResiduals() {
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

  public final void plotFitted() {
    this.fittedSeries.plot("Mean Model Fitted Values");
  }

  @Override
  public final void plotFit() {

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
    StringBuilder builder = new StringBuilder();
    builder.append("timeSeries: ").append(timeSeries).append("\nfittedSeries: ").append(fittedSeries).append("\nmean: ")
        .append(mean);
    return builder.toString();
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
