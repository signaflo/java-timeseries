/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package com.github.signaflo.timeseries;

import com.github.signaflo.data.DataSet;
import com.github.signaflo.data.DoubleDataSet;
import com.github.signaflo.math.operations.DoubleFunctions;
import com.github.signaflo.math.operations.Operators;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

/**
 * An immutable sequence of observations taken at regular time intervals.
 *
 * @author Jacob Rachiele
 */
public final class TimeSeries implements DataSet {

  private final TimePeriod timePeriod;
  private final int n;
  private final double mean;
  private final double[] series;
  private final List<Time> observationTimes;
  private final Map<Time, Integer> timeToIntegerMap;
  private final DoubleDataSet dataSet;

  private TimeSeries(final double... series) {
    this(Time.fromYear(1), series);
  }

  private TimeSeries(final Time startTime, final double... series) {
    this(TimePeriod.oneYear(), startTime, series);
  }

  private TimeSeries(final TimePeriod timePeriod, final double... series) {
    this(timePeriod, Time.fromYear(1), series);
  }

  private TimeSeries(final TimePeriod timePeriod, final CharSequence startTime,
                     final double... series) {
    this.dataSet = new DoubleDataSet(series);
    this.series = series.clone();
    this.n = series.length;
    this.mean = this.dataSet.mean();
    this.timePeriod = timePeriod;

    Map<Time, Integer> timeToIntegerMap = new HashMap<>(series.length);
    List<Time> times = new ArrayList<>(series.length);
    OffsetDateTime dateTime;
    Time time;
    try {
      dateTime = OffsetDateTime.parse(startTime);
      time = new Time(dateTime);
      times.add(new Time(dateTime));
      timeToIntegerMap.put(time, 0);
    } catch (DateTimeParseException e) {
      dateTime = OffsetDateTime.of(LocalDateTime.parse(startTime), ZoneOffset.ofHours(0));
      time = new Time(dateTime);
      times.add(time);
      timeToIntegerMap.put(time, 0);
    }

    for (int i = 1; i < series.length; i++) {
      time = times.get(i - 1).plus(timePeriod);
      times.add(time);
      timeToIntegerMap.put(time, i);
    }
    this.observationTimes = Collections.unmodifiableList(times);
    this.timeToIntegerMap = Collections.unmodifiableMap(timeToIntegerMap);
  }

  private TimeSeries(final TimePeriod timePeriod, final Time startTime, final double... series) {
    this.dataSet = new DoubleDataSet(series);
    this.series = series.clone();
    this.n = series.length;
    this.mean = this.dataSet.mean();
    this.timePeriod = timePeriod;

    List<Time> times = new ArrayList<>(series.length);
    Map<Time, Integer> timeIntegerMap = new HashMap<>(series.length);
    times.add(startTime);
    timeIntegerMap.put(startTime, 0);

    Time time;
    for (int i = 1; i < series.length; i++) {
      time = times.get(i - 1).plus(timePeriod);
      times.add(time);
      timeIntegerMap.put(time, i);
    }
    this.observationTimes = Collections.unmodifiableList(times);
    this.timeToIntegerMap = Collections.unmodifiableMap(timeIntegerMap);
  }

  private TimeSeries(final TemporalUnit timeUnit, final CharSequence startTime,
                     final double... series) {
    this(new TimePeriod(timeUnit, 1), startTime, series);
  }

  private TimeSeries(final TimePeriod timePeriod, final List<Time> observationTimes,
                     final double... series) {
    this.dataSet = new DoubleDataSet(series);
    this.series = series.clone();
    this.n = series.length;
    this.mean = this.dataSet.mean();
    this.timePeriod = timePeriod;
    this.observationTimes = Collections.unmodifiableList(observationTimes);

    Map<Time, Integer> timeIntegerMap = new HashMap<>(series.length);
    int i = 0;
    for (Time time : observationTimes) {
      timeIntegerMap.put(time, i);
      i++;
    }
    this.timeToIntegerMap = Collections.unmodifiableMap(timeIntegerMap);
  }

  /**
   * Create a new time series from the given data without regard to when the observations were made.
   * Use this constructor if the dates and times associated with the observations do not matter.
   *
   * @param series the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final double... series) {
    return new TimeSeries(series);
  }

  /**
   * Create a new time series from the given data with the supplied start time.
   *
   * @param startTime the time of the first observation.
   * @param series    the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final Time startTime, @NonNull final double... series) {
    return new TimeSeries(startTime, series);
  }

  /**
   * Create a new time series using the given time period and observation data. This constructor
   * will set the starting observation period to Jan. 1st of year 1. It is useful for when the time
   * period between observations matter, but the actual dates do not.
   *
   * @param timePeriod the period of time between observations.
   * @param series     the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final TimePeriod timePeriod,
                                @NonNull final double... series) {
    return new TimeSeries(timePeriod, series);
  }

  /**
   * Create a new time series using the given time period, the time of first observation, and
   * observation data.
   *
   * @param timePeriod the period of time between observations.
   * @param startTime  the time of the first observation. The characters must represent either a
   *                   valid {@link OffsetDateTime} or a valid {@link LocalDateTime}.
   *                   If a LocalDateTime, then the default UTC/Greenwich offset, i.e., an offset
   *                   of 0, will be used.
   * @param series     the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final TimePeriod timePeriod,
                                @NonNull final CharSequence startTime,
                                @NonNull final double... series) {
    return new TimeSeries(timePeriod, startTime, series);
  }

  /**
   * Create a new time series with the given time period, observation times, and observation data.
   *
   * @param timePeriod       the period of time between observations.
   * @param observationTimes the observation times.
   * @param series           the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final TimePeriod timePeriod,
                                @NonNull final List<Time> observationTimes,
                                @NonNull final double... series) {
    return new TimeSeries(timePeriod, observationTimes, series);
  }

  /**
   * Create a new time series using the given unit of time, the time of first observation, and the
   * observation data.
   *
   * @param timeUnit  the unit of time in which observations are made.
   * @param startTime the time of the first observation. The characters must represent either a
   *                  valid {@link Time} or a valid {@link LocalDateTime}. If a LocalDateTime, then
   *                  the default UTC/Greenwich offset, i.e., an offset of 0, will be used.
   * @param series    the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final TemporalUnit timeUnit,
                                @NonNull final CharSequence startTime,
                                @NonNull final double... series) {
    return new TimeSeries(timeUnit, startTime, series);
  }

  /**
   * Create a new time series with the given time period, the time of first observation, and the
   * observation data.
   *
   * @param timePeriod the period of time between observations.
   * @param startTime  the time of the first observation.
   * @param series     the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final TimePeriod timePeriod, @NonNull final Time startTime,
                                @NonNull final double... series) {
    return new TimeSeries(timePeriod, startTime, series);
  }

  /**
   * Create a new time series using the given unit of time, the time of first observation, and the
   * observation data.
   *
   * @param timeUnit  the unit of time in which observations are made.
   * @param startTime the time of the first observation.
   * @param series    the observation data.
   *
   * @return a new time series from the supplied data.
   */
  public static TimeSeries from(@NonNull final TemporalUnit timeUnit, @NonNull final Time startTime,
                                @NonNull final double... series) {
    return new TimeSeries(new TimePeriod(timeUnit, 1), startTime, series);
  }

  private static void validate(double[] series, int lag, int times) {
    if (times < 0) {
      throw new IllegalArgumentException(
          "The value of times must be non-negative but was " + times);
    }
    if (times * lag > series.length) {
      throw new IllegalArgumentException(
          "The product of lag and times must be less than or equal to the length of the series, "
              + "but " + times + " * " + lag + " = " + times * lag + " is greater than "
              + series.length);
    }
  }

  private static void validate(double[] series, int lag) {
    if (lag < 1) {
      throw new IllegalArgumentException("The lag must be positive, but was " + lag);
    }
    if (lag > series.length) {
      throw new IllegalArgumentException(
          "The lag must be less than or equal to the length of the series, but " + lag
              + " is greater than " + series.length);
    }
  }

  private static void validate(int lag) {
    if (lag < 1) {
      throw new IllegalArgumentException("The lag must be positive, but was " + lag);
    }
  }

  /**
   * Difference the given series the given number of times at the given lag.
   *
   * @param series the series to difference.
   * @param lag    the lag at which to take differences.
   * @param times  the number of times to difference the series at the given lag. Note that this
   *               argument may equal 0, in which case a copy of the original series is returned.
   *
   * @return a new time series differenced the given number of times at the given lag.
   *
   * @throws IllegalArgumentException if lag is less than 1.
   * @throws IllegalArgumentException if times is less than 0.
   * @throws IllegalArgumentException if the product of lag and times is greater than the length of
   *                                  the series.
   */
  public static double[] difference(@NonNull final double[] series, final int lag,
                                    final int times) {
    validate(lag);
    validate(series, lag, times);
    if (times == 0) {
      return series.clone();
    }
    double[] diffed = differenceArray(series, lag);
    for (int i = 1; i < times; i++) {
      diffed = differenceArray(diffed, lag);
    }
    return diffed;
  }

  private static double[] differenceArray(final double[] series, final int lag) {
    double[] differenced = new double[series.length - lag];
    for (int i = 0; i < differenced.length; i++) {
      differenced[i] = series[i + lag] - series[i];
    }
    return differenced;
  }

  /**
   * Difference the given series the given number of times at lag 1.
   *
   * @param series the series to difference.
   * @param times  the number of times to difference the series.
   *
   * @return a new time series differenced the given number of times at lag 1.
   *
   * @throws IllegalArgumentException if times is less than 0.
   * @throws IllegalArgumentException if times is greater than the length of the series.
   */
  public static double[] difference(final double[] series, final int times) {
    return difference(series, 1, times);
  }

  /**
   * Aggregate the observations in this series to the yearly level.
   *
   * @return a new time series with the observations in this series aggregated to the yearly level.
   */
  public final TimeSeries aggregateToYears() {
    return aggregate(TimePeriod.oneYear());
  }

  /**
   * Aggregate the observations in this series to the given time unit.
   *
   * @param timeUnit The time unit to aggregate up to.
   *
   * @return a new time series aggregated up to the given time unit.
   */
  public final TimeSeries aggregate(final TemporalUnit timeUnit) {
    return aggregate(new TimePeriod(timeUnit, 1));
  }

  /**
   * Aggregate the time series up to the given time period.
   *
   * @param timePeriod the time period to aggregate up to.
   *
   * @return A new time series aggregated up to the given time period.
   */
  public final TimeSeries aggregate(@NonNull final TimePeriod timePeriod) {
    final int period = (int) (this.timePeriod.frequencyPer(timePeriod));
    if (period == 0) {
      throw new IllegalArgumentException(
          "The given time period was of a smaller magnitude than the original time period. To "
              + "aggregate a series, the time period argument must be of a larger magnitude than "
              + "the " + "original.");
    }
    final List<Time> obsTimes = new ArrayList<>();
    double[] aggregated = new double[series.length / period];
    double sum;
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
   * Retrieve the value of the time series at the given index. Indexing begins at 0.
   *
   * @param index the index of the value to return.
   *
   * @return the value of the time series at the given index.
   */
  public final double at(final int index) {
    if (index < 0 || index >= this.series.length) {
      throw new IndexOutOfBoundsException("No observation available at index: " + index);
    }
    return this.series[index];
  }

  /**
   * Retrieve the value of the time series at the given time.
   *
   * @param time the time the observation was made.
   *
   * @return the value of the time series at the given time.
   *
   * @throws IllegalArgumentException if there is no observation at the given time.
   */
  public final double at(@NonNull final Time time) {
    if (!timeToIntegerMap.containsKey(time)) {
      throw new IllegalArgumentException("No observation available at time: " + time);
    }
    return this.series[timeToIntegerMap.get(time)];
  }

  /**
   * The correlation of this series with itself at lag k.
   *
   * @param k the lag to compute the autocorrelation at.
   *
   * @return the correlation of this series with itself at lag k.
   *
   * @throws IllegalArgumentException if k is less than 0.
   */
  public final double autoCorrelationAtLag(final int k) {
    validateLag(k);
    final double variance = autoCovarianceAtLag(0);
    return autoCovarianceAtLag(k) / variance;
  }

  /**
   * Every correlation coefficient of this series with itself up to the given lag.
   *
   * @param k the maximum lag to compute the autocorrelation at.
   *
   * @return every correlation coefficient of this series with itself up to the given lag.
   *
   * @throws IllegalArgumentException if k is less than 0.
   */
  public final double[] autoCorrelationUpToLag(final int k) {
    validateLag(k);
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
   *
   * @return the covariance of this series with itself at lag k.
   *
   * @throws IllegalArgumentException if k is less than 0.
   */
  public final double autoCovarianceAtLag(final int k) {
    validateLag(k);
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
   *
   * @return every covariance measure of this series with itself up to the given lag.
   *
   * @throws IllegalArgumentException if k is less than 0.
   */
  public final double[] autoCovarianceUpToLag(final int k) {
    validateLag(k);
    final double[] acv = new double[Math.min(k + 1, n)];
    for (int i = 0; i < Math.min(k + 1, n); i++) {
      acv[i] = autoCovarianceAtLag(i);
    }
    return acv;
  }

  private void validateLag(int k) {
    if (k < 0) {
      throw new IllegalArgumentException("The lag, k, must be non-negative, but was " + k);
    }
  }

  /**
   * Transform the series using a Box-Cox transformation with the given parameter value.
   * <p>
   * Setting boxCoxLambda equal to 0 corresponds to the natural logarithm while values other than 0
   * correspond to power transforms.
   * </p>
   * <p>
   * See the definition given
   * <a target="_blank" href="https://en.wikipedia.org/wiki/Power_transform#Box.E2.80.93Cox_transformation">
   * here.</a>
   * </p>
   *
   * @param boxCoxLambda the parameter to use for the transformation.
   *
   * @return a new time series transformed using the given Box-Cox parameter.
   *
   * @throws IllegalArgumentException if boxCoxLambda is not strictly between -1 and 2.
   */
  public final TimeSeries transform(final double boxCoxLambda) {
    if (boxCoxLambda > 2 || boxCoxLambda < -1) {
      throw new IllegalArgumentException("The BoxCox parameter must lie between"
                                             + " -1 and 2, but the provided parameter was equal to "
                                             + boxCoxLambda);
    }
    final double[] boxCoxed = DoubleFunctions.boxCox(this.series, boxCoxLambda);
    return new TimeSeries(this.timePeriod, this.observationTimes, boxCoxed);
  }

  /**
   * Perform the inverse of the Box-Cox transformation on this series and return the result in a new
   * time series.
   *
   * @param boxCoxLambda the Box-Cox transformation parameter to use for the inversion.
   *
   * @return a new time series with the inverse Box-Cox transformation applied.
   *
   * @throws IllegalArgumentException if boxCoxLambda is not strictly between -1 and 2.
   */
  public final TimeSeries backTransform(final double boxCoxLambda) {
    if (boxCoxLambda > 2 || boxCoxLambda < -1) {
      throw new IllegalArgumentException("The BoxCox parameter must lie between"
                                             + " -1 and 2, but the provided parameter was equal to "
                                             + boxCoxLambda);
    }
    final double[] invBoxCoxed = DoubleFunctions.inverseBoxCox(this.series, boxCoxLambda);
    return new TimeSeries(this.timePeriod, this.observationTimes, invBoxCoxed);
  }

  /**
   * Compute a moving average of order m.
   *
   * @param m the order of the moving average.
   *
   * @return a new time series with the smoothed observations.
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
    final List<Time> times = this.observationTimes.subList(k + c - 1, n - k);
    return new TimeSeries(this.timePeriod, times, average);
  }

  /**
   * Return a moving average of order m if m is odd and of order 2 &times; m if m is even.
   *
   * @param m the order of the moving average.
   *
   * @return a centered moving average of order m.
   */
  public final TimeSeries centeredMovingAverage(final int m) {
    if (m % 2 == 1) {
      return movingAverage(m);
    }
    TimeSeries firstAverage = movingAverage(m);
    final int k = m / 2;
    final List<Time> times = this.observationTimes.subList(k, n - k);
    return new TimeSeries(this.timePeriod, times, firstAverage.movingAverage(2).series);
  }

  /**
   * Remove the mean from this series and return the result as a new time series.
   *
   * @return a new time series representing this time series with its mean removed.
   */
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
   * @param lag   the lag at which to take differences.
   * @param times the number of times to difference the series at the given lag.
   *
   * @return a new time series differenced the given number of times at the given lag.
   *
   * @throws IllegalArgumentException if lag is less than 1.
   * @throws IllegalArgumentException if times is less than 0.
   * @throws IllegalArgumentException if the product of lag and times is greater than the length of
   *                                  the series.
   */
  public final TimeSeries difference(final int lag, final int times) {
    validate(this.series, lag, times);
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
   * Difference this time series at the given lag and return the result as a new time series.
   *
   * @param lag the lag at which to take differences.
   *
   * @return a new time series differenced at the given lag.
   *
   * @throws IllegalArgumentException if lag is less than 1.
   * @throws IllegalArgumentException if lag is greater than the size of this series.
   */
  public final TimeSeries difference(final int lag) {
    validate(this.series, lag);
    double[] diffed = differenceArray(this.asArray(), lag);
    final List<Time> obsTimes = this.observationTimes.subList(lag, n);
    return new TimeSeries(this.timePeriod, obsTimes, diffed);
  }

  /**
   * Difference this time series once at lag 1 and return the result as a new time series.
   *
   * @return a new time series differenced once at lag 1.
   */
  public final TimeSeries difference() {
    return difference(1);
  }

  /**
   * Subtract the given series from this time series and return the result as a new time series.
   * Note that if the other series is empty, then this series is returned. However, in all other
   * cases in which the two series differ in size, an IllegalArgumentException is thrown.
   *
   * @param otherSeries the series to subtract from this one.
   *
   * @return The difference between this series and the given series.
   *
   * @throws IllegalArgumentException if the other series is non-empty and the two series differ in
   *                                  size.
   */
  public final TimeSeries minus(@NonNull final TimeSeries otherSeries) {
    if (otherSeries.size() == 0) {
      return this;
    }
    if (otherSeries.size() != this.series.length) {
      throw new IllegalArgumentException("The two series must have the same length.");
    }
    final double[] subtracted = new double[this.series.length];
    for (int t = 0; t < subtracted.length; t++) {
      subtracted[t] = this.series[t] - otherSeries.series[t];
    }
    return new TimeSeries(this.timePeriod, observationTimes, subtracted);
  }

  /**
   * Subtract the given series from this series and return the result as a new time series. Note
   * that if the other series is empty, then this series is returned. However, in all other cases in
   * which the two series differ in size, an IllegalArgumentException is thrown.
   *
   * @param otherSeries the series to subtract from this one.
   *
   * @return The difference between this series and the given series.
   *
   * @throws IllegalArgumentException if the other series is non-empty and the two series differ in
   *                                  size.
   */
  public final TimeSeries minus(@NonNull final double[] otherSeries) {
    if (otherSeries.length == 0) {
      return this;
    }
    if (otherSeries.length != this.series.length) {
      throw new IllegalArgumentException("The two series must have the same length.");
    }
    final double[] subtracted = new double[this.series.length];
    for (int t = 0; t < subtracted.length; t++) {
      subtracted[t] = this.series[t] - otherSeries[t];
    }
    return new TimeSeries(this.timePeriod, observationTimes, subtracted);
  }

  /**
   * Return a slice of this time series from start (inclusive) to end (inclusive).
   *
   * @param start the beginning index of the slice. The value at the index is included in the
   *              returned TimeSeries.
   * @param end   the ending index of the slice. The value at the index is included in the returned
   *              TimeSeries.
   *
   * @return a slice of this time series from start (inclusive) to end (inclusive).
   */
  public final TimeSeries slice(final int start, final int end) {
    final double[] sliced = new double[end - start + 1];
    System.arraycopy(series, start, sliced, 0, end - start + 1);
    final List<Time> obsTimes = this.observationTimes.subList(start, end + 1);
    return new TimeSeries(this.timePeriod, obsTimes, sliced);
  }

  /**
   * Return a slice of this time series from start (inclusive) to end (inclusive).
   *
   * @param start the beginning date and time of the slice. The value at the given time is
   *              included in the returned time series.
   * @param end   the ending date and time of the slice. The value at the given time is
   *              included in the returned time series.
   *
   * @return a slice of this time series from start (inclusive) to end (inclusive).
   */
  public final TimeSeries slice(@NonNull final Time start, @NonNull final Time end) {
    final int startIdx = this.timeToIntegerMap.get(start);
    final int endIdx = this.timeToIntegerMap.get(end);
    final double[] sliced = new double[endIdx - startIdx + 1];
    System.arraycopy(series, startIdx, sliced, 0, endIdx - startIdx + 1);
    final List<Time> obsTimes = this.observationTimes.subList(startIdx, endIdx + 1);
    return new TimeSeries(this.timePeriod, obsTimes, sliced);
  }

  /**
   * Return a slice of this time series using R/Julia style indexing.
   *
   * @param start the beginning time index of the slice. The value at the time index is included in
   *              the returned time series.
   * @param end   the ending time index of the slice. The value at the time index is included in the
   *              returned time series.
   *
   * @return a slice of this time series from start (inclusive) to end (inclusive) using R/Julia
   *     style indexing.
   */
  public final TimeSeries timeSlice(final int start, final int end) {
    final double[] sliced = new double[end - start + 1];
    System.arraycopy(series, start - 1, sliced, 0, end - start + 1);
    final List<Time> obsTimes = this.observationTimes.subList(start - 1, end);
    return new TimeSeries(this.timePeriod, obsTimes, sliced);
  }

  /**
   * Print a descriptive summary of this time series.
   */
  public final void print() {
    System.out.println(this.toString());
  }

  /**
   * retrieve the observations as a list.
   *
   * @return the observations as a list.
   */
  public final List<Double> asList() {
    return DoubleFunctions.listFrom(this.series.clone());
  }

  /**
   * Retrieve the time period at which observations are made for this series.
   *
   * @return the time period at which observations are made for this series.
   */
  public final TimePeriod timePeriod() {
    return this.timePeriod;
  }

  /**
   * The time at which the first observation was made.
   *
   * @return the time at which the first observation was made.
   */
  public final Time startTime() {
    return this.observationTimes.get(0);
  }

  /**
   * Retrieve the list of observation times for this series.
   *
   * @return the list of observation times for this series.
   */
  public final List<Time> observationTimes() {
    return this.observationTimes;
  }

  /**
   * Retrieve the mapping of observation times to array indices for this series.
   *
   * @return the mapping of observation times to array indices for this series.
   */
  public final Map<Time, Integer> timeIndex() {
    return this.timeToIntegerMap;
  }

  /**
   * Retrieve the time series of observations as an array.
   *
   * @return the time series of observations as an array.
   */
  @Override
  public final double[] asArray() {
    return this.series.clone();
  }

  @Override
  public double sum() {
    return this.dataSet.sum();
  }

  @Override
  public double sumOfSquares() {
    return this.dataSet.sumOfSquares();
  }

  @Override
  public double mean() {
    return this.dataSet.mean();
  }

  @Override
  public double median() {
    return this.dataSet.median();
  }

  @Override
  public int size() {
    return this.dataSet.size();
  }

  @Override
  public TimeSeries times(@NonNull DataSet otherData) {
    return new TimeSeries(this.timePeriod(), this.observationTimes(),
                          Operators.productOf(this.asArray(), otherData.asArray()));
  }

  @Override
  public TimeSeries plus(@NonNull DataSet otherData) {
    return new TimeSeries(this.timePeriod(), this.observationTimes(),
                          Operators.sumOf(this.asArray(), otherData.asArray()));
  }

  @Override
  public double variance() {
    return this.dataSet.variance();
  }

  @Override
  public double stdDeviation() {
    return this.dataSet.stdDeviation();
  }

  @Override
  public double covariance(DataSet otherData) {
    return this.dataSet.covariance(otherData);
  }

  @Override
  public double correlation(DataSet otherData) {
    return this.dataSet.correlation(otherData);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    TimeSeries that = (TimeSeries) o;

    if (timePeriod != null ? !timePeriod.equals(that.timePeriod) : that.timePeriod != null) {
      return false;
    }
    if (!Arrays.equals(series, that.series)) {
      return false;
    }
    return observationTimes.equals(that.observationTimes);
  }

  @Override
  public int hashCode() {
    int result = timePeriod != null ? timePeriod.hashCode() : 0;
    result = 31 * result + Arrays.hashCode(series);
    result = 31 * result + observationTimes.hashCode();
    return result;
  }

  @Override
  public String toString() {
    String newLine = System.lineSeparator();
    NumberFormat numFormatter = new DecimalFormat("#0.00");
    return newLine + "Time Series: " + newLine + "number of observations: " + n + newLine + "mean: "
        + numFormatter.format(mean) + newLine + "std: " + numFormatter.format(stdDeviation())
        + newLine + "period: " + timePeriod;
  }
}
