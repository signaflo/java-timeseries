/*
 * Copyright (c) 2016 Jacob Rachiele
 */

package timeseries;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * A set of constants representing standard time units, raging from nanoseconds to centuries.
 * Note that this enum essentially wraps the ChronoUnit enums, with a period length to allow
 * for additional time units (most notably, a quarter of a year).
 * @author Jacob Rachiele
 *
 */
public enum TimeUnit {

  CENTURY(ChronoUnit.CENTURIES, 1L),
  DECADE(ChronoUnit.DECADES, 1L),
  YEAR(ChronoUnit.YEARS, 1L),
  QUARTER(ChronoUnit.MONTHS, 3L),
  MONTH(ChronoUnit.MONTHS, 1L),
  WEEK(ChronoUnit.WEEKS, 1L),
  DAY(ChronoUnit.DAYS, 1L),
  HOUR(ChronoUnit.HOURS, 1L),
  MINUTE(ChronoUnit.MINUTES, 1L),
  SECOND(ChronoUnit.SECONDS, 1L),
  MILLISECOND(ChronoUnit.MILLIS, 1L),
  MICROSECOND(ChronoUnit.MICROS, 1L),
  NANOSECOND(ChronoUnit.NANOS, 1L);

  private final TemporalUnit temporalUnit;
  private final long periodLength;

  TimeUnit(final TemporalUnit timeUnit, final long periodLength) {
    this.temporalUnit = timeUnit;
    this.periodLength = periodLength;
  }

  public TemporalUnit temporalUnit() {
    return this.temporalUnit;
  }

  public long periodLength() {
    return this.periodLength;
  }

  /**
   * Compute and return the number of times this TimeUnit occurs in another TimeUnit.
   * <p>
   * For example, if this time unit is a month and the other time unit is a year, the
   * return value should equal 12, since a month occurs 12 times in one year. Note that
   * for practical purposes the double returned by this method will very often be
   * coerced to a long or integer.
   * </p>
   * @param otherTimeUnit the time unit for which the frequency of occurrence of this time unit 
   * is to be found.
   * @return the number of times this TimeUnit occurs in the provided TimeUnit.
   */
  public double frequencyPer(final TimeUnit otherTimeUnit) {
    return otherTimeUnit.totalDuration() / this.totalDuration();

  }
  
  /**
   * Compute and return the number of times this TimeUnit occurs in the given TimeScale.
   * <p>
   * For example, if this time unit is a month and the given time scale is half a year, the
   * return value should equal 6, since a month occurs 6 times in one year. Note that
   * for practical purposes the double returned by this method will very often be
   * coerced to a long or integer.
   * </p>
   * @param timeScale the time unit for which the frequency of occurrence of this time unit 
   * is to be found.
   * @return the number of times this TimeUnit occurs in the provided TimeUnit.
   */
  public double frequencyPer(final TimeScale timeScale) {
    return timeScale.totalDuration() / this.totalDuration();
  }

  /**
   * The total amount of time in this time unit measured in seconds, the base SI unit of time.
   * @return the total amount of time in this time unit measured in seconds.
   */
  double totalDuration() {
    
    Duration thisDuration = this.temporalUnit.getDuration();
    
    // Since the duration is measured in seconds and is treated by the Duration class as a long, we need
    //     to treat time scales less than one second as special cases and return the values ourselves.
    switch (this) {
      case NANOSECOND:
        return 1E-9;
      case MICROSECOND:
        return 1E-6;
      case MILLISECOND:
        return 1E-3;
      default:
        return thisDuration.getSeconds() * this.periodLength;
    }
    
  }
}
