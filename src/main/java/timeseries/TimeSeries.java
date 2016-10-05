/*
 * Copyright (c) 2016 Jacob Rachiele
 */

package timeseries;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.None;
import org.knowm.xchart.style.markers.SeriesMarkers;

import data.DataSet;
import data.DoubleFunctions;

/**
 * An immutable sequence of observations taken at regular time intervals.
 * 
 * @author Jacob Rachiele
 *
 */
public final class TimeSeries extends DataSet {

  private final TimePeriod timePeriod;
  private final int n;
  private final double mean;
  private final double[] series;
  private final List<OffsetDateTime> observationTimes;
  private final Map<OffsetDateTime, Integer> dateTimeIndex;

  /**
   * Construct a new TimeSeries from the given data without regard to the times observations are made. Use this
   * constructor if the dates and/or times associated with the observations do not matter.
   * 
   * @param series the time series of observations.
   */
  public TimeSeries(final double... series) {
    this(OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0)), series);
  }

  /**
   * Construct a new TimeSeries using the given arguments.
   * 
   * @param timeUnit The unit of time in which observations are made.
   * @param startTime The time at which the first observation was made. May be an approximation.
   * @param series The data underlying the time series.
   */
  public TimeSeries(final TimeUnit timeUnit, final OffsetDateTime startTime, final double... series) {
    this(new TimePeriod(timeUnit, 1), startTime, series);
  }

  /**
   * Construct a new TimeSeries using the given arguments.
   * 
   * @param timePeriod The period of time in which observations are made.
   * @param startTime The time at which the first observation was made. The string must represent either a valid
   *        {@link OffsetDateTime} or a valid {@link LocalDateTime}. If a LocalDateTime, then the default UTC/Greenwich
   *        offset, i.e., an offset of 0, will be used.
   * @param series The data underlying the time series.
   */
  public TimeSeries(final TimePeriod timePeriod, final String startTime, final double... series) {
    super(series);
    this.series = series.clone();
    this.n = series.length;
    this.mean = super.mean();
    this.timePeriod = timePeriod;
    Map<OffsetDateTime, Integer> dateTimeIndex = new HashMap<>(series.length);
    List<OffsetDateTime> dateTimes = new ArrayList<>(series.length);
    OffsetDateTime dateTime;
    try {
      dateTime = OffsetDateTime.parse(startTime);
      dateTimes.add(dateTime);
      dateTimeIndex.put(dateTime, 0);
    } catch (DateTimeParseException e) {
      dateTime = OffsetDateTime.of(LocalDateTime.parse(startTime), ZoneOffset.ofHours(0));
      dateTimes.add(dateTime);
      dateTimeIndex.put(dateTime, 0);
    }

    for (int i = 1; i < series.length; i++) {
      dateTime = dateTimes.get(i - 1).plus(totalPeriodLength(timePeriod), timePeriod.timeUnit().temporalUnit());
      dateTimes.add(dateTime);
      dateTimeIndex.put(dateTime, i);
    }
    this.observationTimes = Collections.unmodifiableList(dateTimes);
    this.dateTimeIndex = Collections.unmodifiableMap(dateTimeIndex);
  }

  private final long totalPeriodLength(final TimePeriod timePeriod) {
    return timePeriod.timeUnit().periodLength() * timePeriod.unitLength();
  }

  /**
   * Construct a new TimeSeries using the given arguments.
   * 
   * @param timeUnit The unit of time in which observations are made.
   * @param startTime The time at which the first observation was made. The string must represent either a valid
   *        {@link OffsetDateTime} or a valid {@link LocalDateTime}. If a LocalDateTime, then the default UTC/Greenwich
   *        offset, i.e., an offset of 0, will be used.
   * @param series The data underlying the time series.
   */
  public TimeSeries(final TimeUnit timeUnit, final String startTime, final double... series) {
    this(new TimePeriod(timeUnit, 1), startTime, series);
  }

  /**
   * Construct a new TimeSeries using the given arguments.
   * 
   * @param timePeriod The time period in which observations are made.
   * @param startTime The time at which the first observation was made. Usually a rough approximation.
   * @param series The data underlying the time series.
   */
  public TimeSeries(final TimePeriod timePeriod, final OffsetDateTime startTime, final double... series) {
    super(series);
    this.series = series.clone();
    this.n = series.length;
    this.mean = super.mean();
    this.timePeriod = timePeriod;
    List<OffsetDateTime> dateTimes = new ArrayList<>(series.length);
    Map<OffsetDateTime, Integer> dateTimeIndex = new HashMap<>(series.length);
    dateTimes.add(startTime);
    dateTimeIndex.put(startTime, 0);
    OffsetDateTime dateTime;
    for (int i = 1; i < series.length; i++) {
      dateTime = dateTimes.get(i - 1).plus(timePeriod.unitLength() * timePeriod.timeUnit().periodLength(),
              timePeriod.timeUnit().temporalUnit());
      dateTimes.add(dateTime);
      dateTimeIndex.put(dateTime, i);
    }
    this.observationTimes = Collections.unmodifiableList(dateTimes);
    this.dateTimeIndex = Collections.unmodifiableMap(dateTimeIndex);
  }

  /**
   * Construct a new TimeSeries from the given data with the supplied start time.
   * 
   * @param startTime the time of the first observation.
   * @param series the observations.
   */
  TimeSeries(final OffsetDateTime startTime, final double... series) {
    this(TimeUnit.MONTH, startTime, series);
  }

  public TimeSeries(final TimePeriod timePeriod, final List<OffsetDateTime> observationTimes, final double... series) {
    super(series);
    this.series = series.clone();
    this.n = series.length;
    this.mean = super.mean();
    this.timePeriod = timePeriod;
    this.observationTimes = Collections.unmodifiableList(observationTimes);
    Map<OffsetDateTime, Integer> dateTimeIndex = new HashMap<>(series.length);
    int i = 0;
    for (OffsetDateTime dt : observationTimes) {
      dateTimeIndex.put(dt, i++);
    }
    this.dateTimeIndex = Collections.unmodifiableMap(dateTimeIndex);
  }

  /**
   * Aggregate the observations in this series to the yearly level.
   * 
   * @return a new TimeSeries with the observations in this series aggregated to the yearly level.
   */
  public final TimeSeries aggregateToYears() {
    return aggregate(TimePeriod.oneYear());
  }

  /**
   * Aggregate the observations in this series to the given time period.
   * 
   * @param timeUnit The time period to aggregate up to.
   * @return a new TimeSeries with the observations in this series aggregated to the given time period.
   */
  public final TimeSeries aggregate(final TimeUnit timeUnit) {
    return aggregate(new TimePeriod(timeUnit, 1));
  }

  /**
   * Aggregate the TimeSeries up to the given time period with the specified period length. For example, to aggregate
   * monthly data to bi-yearly data, one could give a time argument of {@link TimeUnit#YEAR} and a periodLength argument
   * of 2.
   * 
   * @param timePeriod the unit of time that this series should be aggregated up to.
   * @return A new TimeSeries aggregated up to the given unit of time.
   */
  public final TimeSeries aggregate(final TimePeriod timePeriod) {
    final int period = (int) (this.timePeriod.frequencyPer(timePeriod));
    if (period == 0) {
      throw new IllegalArgumentException(
          "The given time period was of a smaller magnitude than the original time period."
              + " To aggregate a series, the time period argument must be of a larger magnitude than the original.");
    }
    final List<OffsetDateTime> obsTimes = new ArrayList<>();
    double[] aggregated = new double[series.length / period];
    double sum = 0.0;
    for (int i = 0; i < aggregated.length; i++) {
      sum = 0.0;
      for (int j = 0; j < period; j++) {
        sum += series[j + period * i];
      }
      aggregated[i] = sum;
      obsTimes.add(this.observationTimes.get(i * period));
    }
    return new TimeSeries(timePeriod, obsTimes, aggregated);
  }

  /**
   * Return the value of the time series at the given index.
   * 
   * @param index the index of the value to return.
   * @return the value of the time series at the given index.
   */
  public final double at(final int index) {
    return this.series[index];
  }
  
  /**
   * Return the value of the time series at the given date and time.
   * 
   * @param dateTime the date and time of the value to return.
   * @return the value of the time series at the given date and time.
   */
  public final double at(final OffsetDateTime dateTime) {
    return this.series[dateTimeIndex.get(dateTime)];
  }

  /**
   * The correlation of this series with itself at lag k.
   * 
   * @param k the lag to compute the autocorrelation at.
   * @return the correlation of this series with itself at lag k.
   */
  public final double autoCorrelationAtLag(final int k) {
    final double variance = autoCovarianceAtLag(0);
    return autoCovarianceAtLag(k) / variance;
  }

  /**
   * Every correlation coefficient of this series with itself up to the given lag.
   * 
   * @param k the maximum lag to compute the autocorrelation at.
   * @return every correlation coefficient of this series with itself up to the given lag.
   */
  public final double[] autoCorrelationUpToLag(final int k) {
    final double[] autoCorrelation = new double[Math.min(k + 1, n)];
    for (int i = 0; i < Math.min(k + 1, n); i++) {
      autoCorrelation[i] = autoCorrelationAtLag(i);
    }
    return autoCorrelation;
  }

  /**
   * The covariance of this series with itself at lag k.
   * 
   * @param k the lag to compute the autocovariance at.
   * @return the covariance of this series with itself at lag k.
   */
  public final double autoCovarianceAtLag(final int k) {
    double sumOfProductOfDeviations = 0.0;
    for (int t = 0; t < n - k; t++) {
      sumOfProductOfDeviations += (series[t] - mean) * (series[t + k] - mean);
    }
    return sumOfProductOfDeviations / n;
  }

  /**
   * Every covariance measure of this series with itself up to the given lag.
   * 
   * @param k the maximum lag to compute the autocovariance at.
   * @return every covariance measure of this series with itself up to the given lag.
   */
  public final double[] autoCovarianceUpToLag(final int k) {
    final double[] acv = new double[Math.min(k + 1, n)];
    for (int i = 0; i < Math.min(k + 1, n); i++) {
      acv[i] = autoCovarianceAtLag(i);
    }
    return acv;
  }

  /**
   * Perform the inverse of the Box-Cox transformation on this series and return the result in a new TimeSeries.
   * 
   * @param boxCoxLambda the Box-Cox transformation parameter to use for the inversion.
   * @return a new TimeSeries with the inverse Box-Cox transformation applied.
   */
  public final TimeSeries backTransform(final double boxCoxLambda) {
    if (boxCoxLambda > 2 || boxCoxLambda < -1) {
      throw new IllegalArgumentException("The BoxCox parameter must lie between"
          + " -1 and 2, but the provided parameter was equal to " + boxCoxLambda);
    }
    final double[] invBoxCoxed = DoubleFunctions.inverseBoxCox(this.series, boxCoxLambda);
    return new TimeSeries(this.timePeriod, this.observationTimes, invBoxCoxed);
  }

  /**
   * Return a moving average of order m if m is odd and of order 2 &times; m if m is even.
   * 
   * @param m the order of the moving average.
   * @return a centered moving average of order m.
   */
  public final TimeSeries centeredMovingAverage(final int m) {
    if (m % 2 == 1)
      return movingAverage(m);
    TimeSeries firstAverage = movingAverage(m);
    final int k = m / 2;
    final List<OffsetDateTime> times = this.observationTimes.subList(k, n - k);
    double[] secondAverage = firstAverage.movingAverage(2).series;
    return new TimeSeries(this.timePeriod, times, secondAverage);
  }

  public final TimeSeries demean() {
    final double[] demeaned = new double[this.series.length];
    for (int t = 0; t < demeaned.length; t++) {
      demeaned[t] = this.series[t] - this.mean;
    }
    return new TimeSeries(this.timePeriod, this.observationTimes, demeaned);
  }

  /**
   * Difference this series the given number of times at the given lag.
   * 
   * @param lag the lag at which to take differences.
   * @param times the number of times to difference the series at the given lag.
   * @return a new TimeSeries differenced the given number of times at the given lag.
   */
  public final TimeSeries difference(final int lag, final int times) {
    if (times > 0) {
      TimeSeries diffed = difference(lag);
      for (int i = 1; i < times; i++) {
        diffed = diffed.difference(lag);
      }
      return diffed;
    }
    return this;
  }

  /**
   * Difference this time series at the given lag and return the result in a new TimeSeries.
   * 
   * @param lag the lag at which to take differences.
   * @return a new TimeSeries differenced at the given lag.
   */
  public final TimeSeries difference(final int lag) {
    double[] diffed = differenceArray(lag);
    final List<OffsetDateTime> obsTimes = this.observationTimes.subList(lag, n);
    return new TimeSeries(this.timePeriod, obsTimes, diffed);
  }

  /**
   * Difference this time series once at lag 1 and return the result in a new TimeSeries.
   * 
   * @return a new TimeSeries differenced once at lag.
   */
  public final TimeSeries difference() {
    return difference(1);
  }

  /**
   * Compute a moving average of order m.
   * 
   * @param m the order of the moving average.
   * @return a new TimeSeries with the smoothed observations.
   */
  public final TimeSeries movingAverage(final int m) {
    final int c = m % 2;
    final int k = (m - c) / 2;
    final double[] average;
    average = new double[this.n - m + 1];
    double sum;
    for (int t = 0; t < average.length; t++) {
      sum = 0;
      for (int j = -k; j < k + c; j++) {
        sum += series[t + k + j];
      }
      average[t] = sum / m;
    }
    final List<OffsetDateTime> times = this.observationTimes.subList(k + c - 1, n - k);
    return new TimeSeries(this.timePeriod, times, average);
  }

  /**
   * Return the list of observation times for this series.
   * 
   * @return the list of observation times for this series.
   */
  public final List<OffsetDateTime> observationTimes() {
    return this.observationTimes;
  }
  
  public final Map<OffsetDateTime, Integer> dateTimeIndex() {
    return this.dateTimeIndex;
  }

  /**
   * Print a descriptive summary of this time series.
   */
  public final void print() {
    System.out.println(this.toString());
  }

  /**
   * The time series of observations.
   * 
   * @return the time series of observations.
   */
  public final double[] series() {
    return this.series.clone();
  }

  /**
   * Return a slice of this time series from start (inclusive) to end (exclusive).
   * 
   * @param start the beginning index of the slice. The value at the index is included in the returned TimeSeries.
   * @param end the ending index of the slice. The value at the index is <i>not</i> included in the returned TimeSeries.
   * @return a slice of this time series from start (inclusive) to end (exclusive).
   */
  public final TimeSeries slice(final int start, final int end) {
    final double[] sliced = new double[end - start + 1];
    System.arraycopy(series, start, sliced, 0, end - start + 1);
    final List<OffsetDateTime> obsTimes = this.observationTimes.subList(start, end + 1);
    return new TimeSeries(this.timePeriod, obsTimes, sliced);
  }

  /**
   * Return a slice of this time series using R/Julia style indexing.
   * 
   * @param start the beginning time index of the slice. The value at the time index is included in the returned
   *        TimeSeries.
   * @param end the ending time index of the slice. The value at the time index is <i>not</i> included in the returned
   *        TimeSeries.
   * @return a slice of this time series from start (inclusive) to end (exclusive) using R/Julia style indexing.
   */
  public final TimeSeries timeSlice(final int start, final int end) {
    final double[] sliced = new double[end - start + 1];
    System.arraycopy(series, start - 1, sliced, 0, end - start + 1);
    final List<OffsetDateTime> obsTimes = this.observationTimes.subList(start - 1, end);
    return new TimeSeries(this.timePeriod, obsTimes, sliced);
  }

  /**
   * Transform the series using a Box-Cox transformation with the given parameter value.
   * 
   * <p>
   * Setting boxCoxLambda equal to 0 corresponds to the natural logarithm while values other than 0 correspond to power
   * transforms.
   * </p>
   * <p>
   * See the definition given
   * <a target="_blank" href="https://en.wikipedia.org/wiki/Power_transform#Box.E2.80.93Cox_transformation"> here.</a>
   * </p>
   * 
   * @param boxCoxLambda the parameter to use for the transformation.
   * @return a new TimeSeries transformed using the given Box-Cox parameter.
   * @throws IllegalArgumentException if boxCoxLambda is not strictly between -1 and 2.
   */
  public final TimeSeries transform(final double boxCoxLambda) {
    if (boxCoxLambda > 2 || boxCoxLambda < -1) {
      throw new IllegalArgumentException("The BoxCox parameter must lie between"
          + " -1 and 2, but the provided parameter was equal to " + boxCoxLambda);
    }
    final double[] boxCoxed = DoubleFunctions.boxCox(this.series, boxCoxLambda);
    return new TimeSeries(this.timePeriod, this.observationTimes, boxCoxed);
  }

  private final double[] differenceArray(final int lag) {
    double[] differenced = new double[series.length - lag];
    for (int i = 0; i < differenced.length; i++) {
      differenced[i] = series[i + lag] - series[i];
    }
    return differenced;
  }

  /**
   * The time at which the first observation was made.
   * 
   * @return the time at which the first observation was made.
   */
  public final OffsetDateTime startTime() {
    return this.observationTimes.get(0);
  }

  // ********** Plots ********** //

  /**
   * Display a line plot connecting the observation times to the measurements.
   */
  @Override
  public final void plot() {

    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(this.observationTimes.size());
      for (OffsetDateTime dateTime : this.observationTimes) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      final List<Double> seriesList = com.google.common.primitives.Doubles.asList(this.series);
      for (int t = 0; t < seriesList.size(); t++) {
        if (seriesList.get(t).isInfinite()) {
          seriesList.set(t, Double.NaN);
        }
      }
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.XChart).height(480).width(960).title("").build();
      final XYSeries xySeries = chart.addSeries("Series Values", xAxis, seriesList)
          .setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
      xySeries.setLineWidth(0.75f);
      xySeries.setMarker(new None()).setLineColor(Color.BLUE);
      final JPanel panel = new XChartPanel<>(chart);
      final JFrame frame = new JFrame("");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();

  }

  public final void plot(final String plotName) {

    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(this.observationTimes.size());
      for (OffsetDateTime dateTime : this.observationTimes) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      final List<Double> seriesList = com.google.common.primitives.Doubles.asList(this.series);
      for (int t = 0; t < seriesList.size(); t++) {
        if (seriesList.get(t).isInfinite()) {
          seriesList.set(t, Double.NaN);
        }
      }
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800).title(plotName)
          .build();
      final XYSeries xySeries = chart.addSeries("Series Values", xAxis, seriesList)
          .setXYSeriesRenderStyle(XYSeriesRenderStyle.Line);
      xySeries.setLineWidth(0.75f);
      xySeries.setMarker(new None()).setLineColor(Color.BLUE);
      final JPanel panel = new XChartPanel<>(chart);
      final JFrame frame = new JFrame(plotName);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();

  }

  /**
   * Display a plot of the sample autocorrelations up to the given lag.
   * 
   * @param k the maximum lag to include in the acf plot.
   */
  public final void plotAcf(final int k) {

    final double[] acf = autoCorrelationUpToLag(k);
    final double[] lags = new double[k + 1];
    for (int i = 1; i < lags.length; i++) {
      lags[i] = i;
    }
    final double upper = (-1 / series.length) + (2 / Math.sqrt(series.length));
    final double lower = (-1 / series.length) - (2 / Math.sqrt(series.length));
    final double[] upperLine = new double[lags.length];
    final double[] lowerLine = new double[lags.length];
    for (int i = 0; i < lags.length; i++) {
      upperLine[i] = upper;
    }
    for (int i = 0; i < lags.length; i++) {
      lowerLine[i] = lower;
    }

    new Thread(() -> {
      XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(800).width(1200)
          .title("Autocorrelations By Lag").build();
      XYSeries series = chart.addSeries("Autocorrelation", lags, acf);
      XYSeries series2 = chart.addSeries("Upper Bound", lags, upperLine);
      XYSeries series3 = chart.addSeries("Lower Bound", lags, lowerLine);
      chart.getStyler().setChartFontColor(Color.BLACK)
          .setSeriesColors(new Color[] { Color.BLACK, Color.BLUE, Color.BLUE });

      series.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
      series2.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line).setMarker(SeriesMarkers.NONE)
          .setLineStyle(SeriesLines.DASH_DASH);
      series3.setXYSeriesRenderStyle(XYSeriesRenderStyle.Line).setMarker(SeriesMarkers.NONE)
          .setLineStyle(SeriesLines.DASH_DASH);
      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("Autocorrelation by Lag");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).run();

  }

  // ********** Plots ********** //

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    TimeSeries other = (TimeSeries) obj;
    if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean))
      return false;
    if (n != other.n)
      return false;
    if (observationTimes == null) {
      if (other.observationTimes != null)
        return false;
    } else if (!observationTimes.equals(other.observationTimes))
      return false;
    if (!Arrays.equals(series, other.series))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(mean);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + n;
    result = prime * result + ((observationTimes == null) ? 0 : observationTimes.hashCode());
    result = prime * result + Arrays.hashCode(series);
    return result;
  }

  @Override
  public String toString() {
    NumberFormat numFormatter = new DecimalFormat("#0.0000");
    StringBuilder builder = new StringBuilder();
    builder.append("n: ").append(n).append("\nmean: ").append(numFormatter.format(mean)).append("\nseries: ");
    if (series.length > 6) {
      for (double d : DoubleFunctions.slice(series, 0, 3)) {
        builder.append(numFormatter.format(d)).append(", ");
      }
      builder.append("..., ");
      for (double d : DoubleFunctions.slice(series, n - 3, n - 1)) {
        builder.append(numFormatter.format(d)).append(", ");
      }
      builder.append(numFormatter.format(series[n - 1]));
    } else {
      for (int i = 0; i < series.length - 1; i++) {
        builder.append(numFormatter.format(series[i])).append(", ");
      }
      builder.append(numFormatter.format(series[n - 1]));
    }
    builder.append("\nobservationTimes: ");
    if (series.length > 6) {
      for (OffsetDateTime date : observationTimes.subList(0, 3)) {
        builder.append(date.toString()).append(", ");
      }
      builder.append("..., ");
      for (OffsetDateTime date : observationTimes.subList(n - 3, n - 1)) {
        builder.append(date.toString()).append(", ");
      }
      builder.append(observationTimes.get(n - 1).toString());
    } else {
      for (int i = 0; i < observationTimes.size() - 1; i++) {
        builder.append(observationTimes.get(i).toString()).append(", ");
      }
      builder.append(observationTimes.get(n - 1).toString());
    }
    return builder.append("\ntimePeriod: \n").append(timePeriod).toString();
  }

  public final TimeSeries minus(final TimeSeries otherSeries) {
    final double[] subtracted = new double[this.series.length];
    for (int t = 0; t < subtracted.length; t++) {
      subtracted[t] = this.series[t] - otherSeries.series[t];
    }
    return new TimeSeries(this.timePeriod, observationTimes, subtracted);
  }

  public final TimeSeries minus(final double[] otherSeries) {
    final double[] subtracted = new double[this.series.length];
    for (int t = 0; t < subtracted.length; t++) {
      subtracted[t] = this.series[t] - otherSeries[t];
    }
    return new TimeSeries(this.timePeriod, observationTimes, subtracted);
  }

  public final TimePeriod timePeriod() {
    return this.timePeriod;
  }

}
