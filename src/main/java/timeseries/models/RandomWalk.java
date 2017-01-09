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
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.None;

import stats.distributions.Distribution;
import stats.distributions.Normal;
import timeseries.TimePeriod;
import timeseries.TimeSeries;

/**
 * A model for a random walk process. Some important characteristics of the random walk are that the
 * process variance increases as a function of time (non-stationarity) and that the optimal forecast
 * at any point in the future is equal to the last observed value.
 *
 * @author Jacob Rachiele
 *
 */
public final class RandomWalk implements Model {

  private final TimeSeries timeSeries;
  private final TimeSeries fittedSeries;
  private final TimeSeries residuals;

  /**
   * Create a new random walk model from the given time series of observations.
   *
   * @param observed the observed series.
   */
  public RandomWalk(final TimeSeries observed) {
    this.timeSeries = observed;
    this.fittedSeries = fitSeries();
    this.residuals = calculateResiduals();
  }

  /**
   * Simulate a random walk assuming that the errors, or random shocks, follow the given Distribution.
   * 
   * @param dist The probability distribution that observations are drawn from.
   * @param n The number of observations to simulate.
   * @return the simulated series.
   */
  public static TimeSeries simulate(final Distribution dist, final int n) {
    if (n < 1) {
      throw new IllegalArgumentException("the number of observations to simulate must be a positive integer.");
    }
    final double[] series = new double[n];
    series[0] = dist.rand();
    for (int t = 1; t < n; t++) {
      series[t] = series[t - 1] + dist.rand();
    }
    return new TimeSeries(series);
  }

  /**
   * Simulate a random walk assuming errors follow a Normal (Gaussian) Distribution with the given mean and standard
   * deviation.
   * 
   * @param mean the mean of the Normal distribution the observations are drawn from.
   * @param sigma the standard deviation of the Normal distribution the observations are drawn from.
   * @param n the number of observations to simulate.
   * @return the simulated series.
   */
  public static TimeSeries simulate(final double mean, final double sigma, final int n) {
    final Distribution dist = new Normal(mean, sigma);
    return simulate(dist, n);
  }

  /**
   * Simulate a random walk assuming errors follow a Normal (Gaussian) Distribution with zero mean and with the provided
   * standard deviation.
   * 
   * @param sigma the standard deviation of the Normal distribution the observations are drawn from.
   * @param n the number of observations to simulate.
   * @return the simulated series.
   */
  public static TimeSeries simulate(final double sigma, final int n) {
    final Distribution dist = new Normal(0, sigma);
    return simulate(dist, n);
  }

  /**
   * Simulate a random walk assuming errors follow a standard Normal (Gaussian) Distribution.
   * 
   * @param n the number of observations to simulate.
   * @return the simulated series.
   */
  public static TimeSeries simulate(final int n) {
    final Distribution dist = new Normal(0, 1);
    return simulate(dist, n);
  }

  @Override
  public TimeSeries pointForecast(final int steps) {
    int n = timeSeries.n();
    TimePeriod timePeriod = timeSeries.timePeriod();
    final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
            .plus(timePeriod.periodLength() * timePeriod.timeUnit().unitLength(), timePeriod.timeUnit().temporalUnit());
    double[] forecast = new double[steps];
    for (int t = 0; t < steps; t++) {
      forecast[t] = timeSeries.at(n - 1);
    }
    return new TimeSeries(timePeriod, startTime, forecast);
  }

  @Override
  public Forecast forecast(final int steps, final double alpha) {
    return new RandomWalkForecast(this, steps, alpha);
  }

  /*
   * (non-Javadoc)
   * 
   * @see timeseries.models.Model#timeSeries()
   */
  @Override
  public TimeSeries timeSeries() {
    return this.timeSeries;
  }

  /*
   * (non-Javadoc)
   * 
   * @see timeseries.models.Model#fittedSeries()
   */
  @Override
  public TimeSeries fittedSeries() {
    return this.fittedSeries;
  }

  @Override
  public TimeSeries residuals() {
    return this.residuals;
  }

  /**
   * {@inheritDoc}
   * 
   * <p>
   * <b>Implementation Note:</b>
   * </p>
   * <p>
   * In the case of the random walk the plot of the fitted values is a one-period
   * horizontal shift of the observations plot.
   * </p>
   */
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
          .title("Random Walk Fitted vs Actual").build();
      XYSeries fitSeries = chart.addSeries("Fitted Values", xAxis, fittedList);
      XYSeries observedSeries = chart.addSeries("Actual Values", xAxis, seriesList);
      XYStyler styler = chart.getStyler();
      styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
      observedSeries.setLineWidth(0.75f);
      observedSeries.setMarker(new None()).setLineColor(Color.RED);
      fitSeries.setLineWidth(0.75f);
      fitSeries.setMarker(new None()).setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Random Walk Fit");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();

  }

  @Override
  public void plotResiduals() {

    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(residuals.series());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
          .title("Random Walk Residuals").build();
      XYSeries residualSeries = chart.addSeries("Model Residuals", xAxis, seriesList);
      residualSeries.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
      residualSeries.setMarker(new Circle()).setMarkerColor(Color.RED);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Random Walk Residuals");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();

  }

  private TimeSeries fitSeries() {
    final double[] fitted = new double[timeSeries.n()];
    fitted[0] = timeSeries.at(0);
    for (int t = 1; t < timeSeries.n(); t++) {
      fitted[t] = timeSeries.at(t - 1);
    }
    return new TimeSeries(timeSeries.timePeriod(), timeSeries.observationTimes().get(0),
        fitted);
  }

  private TimeSeries calculateResiduals() {
    final double[] residuals = new double[timeSeries.n()];
    for (int t = 1; t < timeSeries.n(); t++) {
      residuals[t] = timeSeries.at(t) - fittedSeries.at(t);
    }
    return new TimeSeries(timeSeries.timePeriod(), timeSeries.observationTimes().get(0),
        residuals);
  }

  @Override
  public String toString() {
    return "timeSeries: " + timeSeries + "\nfittedSeries: " + fittedSeries + "\nresiduals: " + residuals;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fittedSeries == null) ? 0 : fittedSeries.hashCode());
    result = prime * result + ((residuals == null) ? 0 : residuals.hashCode());
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
    RandomWalk other = (RandomWalk) obj;
    if (fittedSeries == null) {
      if (other.fittedSeries != null) {
        return false;
      }
    } else if (!fittedSeries.equals(other.fittedSeries)) {
      return false;
    }
    if (residuals == null) {
      if (other.residuals != null) {
        return false;
      }
    } else if (!residuals.equals(other.residuals)) {
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
