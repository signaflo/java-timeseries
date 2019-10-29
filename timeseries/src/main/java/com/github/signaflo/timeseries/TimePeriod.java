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

package com.github.signaflo.timeseries;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import lombok.NonNull;

/**
 * An amount of time expressed in a particular time unit. This class wraps a {@link TemporalUnit}
 * together with a positive integer length, allowing one to create a wide range of different
 * time periods. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class TimePeriod {

  private final TemporalUnit timeUnit;
  private final long length;

  /**
   * Create a new time period with the given unit of time and length.
   *
   * @param timeUnit     the unit of time underlying this time period
   * @param length       the length of this time period relative to the given unit of time. Note
   *                     that the length must be a long. Most decimal time periods can be
   *                     modeled by converting to an appropriate time unit with a smaller order of
   *                     magnitude. For example, the {@link TimePeriod#halfMonth} constructor works
   *                     by converting 15.2184375 days to 1314873 seconds.
   *
   * @throws IllegalArgumentException if the given length is less than or equal to 0.
   */
  public TimePeriod(@NonNull TemporalUnit timeUnit, long length) {
    validate(length);
    this.timeUnit = timeUnit;
    this.length = length;
  }

  /**
   * Create and return a new TimePeriod representing exactly one hour.
   *
   * @return a new TimePeriod representing exactly one hour.
   */
  public static TimePeriod oneHour() {
    return new TimePeriod(ChronoUnit.HOURS, 1);
  }

  /**
   * Create and return a new TimePeriod representing exactly one year.
   *
   * @return a new TimePeriod representing exactly one year.
   */
  public static TimePeriod oneYear() {
    return new TimePeriod(ChronoUnit.YEARS, 1);
  }

  /**
   * Create and return a new TimePeriod representing exactly two years.
   *
   * @return a new TimePeriod representing exactly two years.
   */
  public static TimePeriod twoYears() {
    return new TimePeriod(ChronoUnit.YEARS, 2);
  }

  /**
   * Create and return a new TimePeriod representing one half of a decade.
   *
   * @return a new TimePeriod representing one half of a decade.
   */
  public static TimePeriod halfDecade() {
    return new TimePeriod(ChronoUnit.YEARS, 5);
  }

  /**
   * Create and return a new TimePeriod representing exactly one month.
   *
   * @return a new TimePeriod representing exactly one month.
   */
  public static TimePeriod oneMonth() {
    return new TimePeriod(ChronoUnit.MONTHS, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a month.
   *
   * @return a new TimePeriod representing one half of a month.
   */
  public static TimePeriod halfMonth() {
    final int secondsInHalfMonth = 1314873;
    return new TimePeriod(ChronoUnit.SECONDS, secondsInHalfMonth);
  }

  /**
   * Create and return a new TimePeriod representing one quarter of a year.
   *
   * @return a new TimePeriod representing one quarter of a year.
   */
  public static TimePeriod oneQuarter() {
    return new TimePeriod(ChronoUnit.MONTHS, 3);
  }

  /**
   * Create and return a new TimePeriod representing one week.
   *
   * @return a new TimePeriod representing one week.
   */
  public static TimePeriod oneWeek() {
    return new TimePeriod(ChronoUnit.WEEKS, 1);
  }

  /**
   * Create and return a new TimePeriod representing one day.
   *
   * @return a new TimePeriod representing one day.
   */
  public static TimePeriod oneDay() {
    return new TimePeriod(ChronoUnit.DAYS, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a year.
   *
   * @return a new TimePeriod representing one half of a year.
   */
  public static TimePeriod halfYear() {
    return new TimePeriod(ChronoUnit.MONTHS, 6);
  }

  /**
   * Create and return a new TimePeriod representing exactly one decade.
   *
   * @return a new TimePeriod representing exactly one decade.
   */
  public static TimePeriod oneDecade() {
    return new TimePeriod(ChronoUnit.DECADES, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a century.
   *
   * @return a new TimePeriod representing one half of a century.
   */
  public static TimePeriod halfCentury() {
    return new TimePeriod(ChronoUnit.DECADES, 5);
  }

  /**
   * Create and return a new TimePeriod representing exactly one century.
   *
   * @return a new TimePeriod representing exactly one century.
   */
  public static TimePeriod oneCentury() {
    return new TimePeriod(ChronoUnit.CENTURIES, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of an hour.
   *
   * @return a new TimePeriod representing one half of an hour.
   */
  public static TimePeriod halfHour() {
    return new TimePeriod(ChronoUnit.MINUTES, 30);
  }

  /**
   * Create and return a new TimePeriod representing one half of a day.
   *
   * @return a new TimePeriod representing one half of a day.
   */
  public static TimePeriod halfDay() {
    return new TimePeriod(ChronoUnit.HOURS, 12);
  }

  /**
   * Create and return a new TimePeriod representing one third of a year.
   *
   * @return a new TimePeriod representing one third of a year.
   */
  public static TimePeriod triAnnual() {
    return new TimePeriod(ChronoUnit.MONTHS, 4);
  }

  /**
   * Create and return a new TimePeriod representing one second.
   *
   * @return a new TimePeriod representing one second.
   */
  public static TimePeriod oneSecond() {
    return new TimePeriod(ChronoUnit.SECONDS, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a second.
   *
   * @return a new TimePeriod representing one half of a second.
   */
  public static TimePeriod halfSecond() {
    return new TimePeriod(ChronoUnit.MILLIS, 500);
  }

  /**
   * Create and return a new TimePeriod representing one tenth of a second.
   *
   * @return a new TimePeriod representing one tenth of a second.
   */
  public static TimePeriod oneTenthSecond() {
    return new TimePeriod(ChronoUnit.MILLIS, 100);
  }

  /**
   * The unit of time underlying this time period.
   *
   * @return the unit of time underlying this time period.
   */
  TemporalUnit timeUnit() {
    return this.timeUnit;
  }

  /**
   * The length of this time period relative to the underlying time unit.
   *
   * @return the length of this time period relative to the underlying time unit.
   */
  public long length() {
    return this.length;
  }

  /**
   * Compute and return the number of times this time period occurs in the given time period.
   *
   * <p>For example, if this time period is a month and the given time period is half of a year,
   * the return value is 6 since a month occurs 6 times in half of a year.
   *
   * @param otherTimePeriod the time period for which the frequency of occurrence of this time
   *                        period is to be found.
   *
   * @return the number of times this time period occurs in the provided time period.
   */
  public double frequencyPer(final TimePeriod otherTimePeriod) {
    return otherTimePeriod.totalSeconds() / this.totalSeconds();
  }

  /**
   * The total amount of time in this time period measured in seconds, the base SI unit of time.
   *
   * @return the total amount of time in this time period measured in seconds.
   */
  public double totalSeconds() {
    final double nanoSecondsPerSecond = 1E9;
    Duration thisDuration = this.timeUnit.getDuration();
    double seconds = thisDuration.getSeconds() * this.length;
    double nanos = thisDuration.getNano();
    nanos = (nanos * this.length);
    nanos = (nanos / nanoSecondsPerSecond);
    return seconds + nanos;
  }

  private void validate(final long periodLength) {
    if (periodLength <= 0) {
      throw new IllegalArgumentException(
          "The given period length must be a positive integer but was " + periodLength);
    }
  }

  @Override
  public String toString() {
    return length + " " + ((length > 1) ? timeUnit : timeUnit.toString().substring(
        0, timeUnit.toString().length() - 1));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TimePeriod that = (TimePeriod) o;
    return length == that.length && timeUnit.equals(that.timeUnit);
  }

  @Override
  public int hashCode() {
    return Objects.hash(timeUnit, length);
  }
}
