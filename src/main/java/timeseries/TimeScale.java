package timeseries;

import java.time.Duration;

/**
 * Wraps a {@link TimeUnit} together with an integer unit length, allowing one to specify a broader range of time
 * periods than using a TimeUnit alone. This class is immutable and thread-safe.
 * 
 * @author Jacob Rachiele
 *
 */
public final class TimeScale {

  private final TimeUnit timeUnit;
  private final double unitLength;

  /**
   * Construct a new TimeScale with the given unit of time and unit length.
   * 
   * @param timeUnit the unit of time underlying this time scale
   * @param unitLength the length of this unit of time relative to the given time scale.
   */
  public TimeScale(final TimeUnit timeUnit, final double unitLength) {
    this.timeUnit = timeUnit;
    this.unitLength = unitLength;
  }

  /**
   * The time scale underlying this unit of time.
   * 
   * @return the time scale underlying this unit of time.
   */
  public final TimeUnit timeUnit() {
    return this.timeUnit;
  }

  /**
   * The length of this unit of time relative to the underlying time scale.
   * 
   * @return the length of this unit of time relative to the underlying time scale.
   */
  public final double unitLength() {
    return this.unitLength;
  }

  /**
   * Compute and return the number of times this TimeScale occurs in the given TimeScale.
   * <p>
   * For example, if this time scale is a month and the given time scale is half a year, the return value should equal
   * 6, since a month occurs 6 times in one year. Note that for practical purposes the double returned by this method
   * will very often be coerced to a long or integer.
   * </p>
   * 
   * @param otherTimeScale the time scale for which the frequency of occurrence of this time scale is to be found.
   * @return the number of times this TimeScale occurs in the provided TimeScale.
   */
  public double frequencyPer(final TimeScale otherTimeScale) {
    return otherTimeScale.totalDuration() / this.totalDuration();
  }

  /**
   * The total amount of time in this time scale measured in seconds, the base SI unit of time.
   * 
   * @return the total amount of time in this time scale measured in seconds.
   */
  public final double totalDuration() {

    double thisDuration = this.timeUnit.totalDuration();
    return thisDuration * this.unitLength;

  }
  
  /**
   * Create and return a new TimeScale representing exactly one year.
   * 
   * @return a new TimeScale representing exactly one year.
   */
  public static final TimeScale oneYear() {
    return new TimeScale(TimeUnit.YEAR, 1);
  }

  public static final TimeScale twoYears() {
    return new TimeScale(TimeUnit.YEAR, 2);
  }

  public static final TimeScale halfDecade() {
    return new TimeScale(TimeUnit.YEAR, 5);
  }

  public static final TimeScale oneMonth() {
    return new TimeScale(TimeUnit.MONTH, 1);
  }

  public static final TimeScale halfMonth() {
    return new TimeScale(TimeUnit.DAY, 15.2184375);
  }

  public static final TimeScale oneQuarter() {
    return new TimeScale(TimeUnit.QUARTER, 1);
  }

  public static final TimeScale halfYear() {
    return new TimeScale(TimeUnit.MONTH, 6);
  }

  public static final TimeScale oneDecade() {
    return new TimeScale(TimeUnit.DECADE, 1);
  }

  public static final TimeScale halfCentury() {
    return new TimeScale(TimeUnit.DECADE, 5);
  }

  public static final TimeScale oneCentury() {
    return new TimeScale(TimeUnit.CENTURY, 1);
  }

  public static final TimeScale halfHour() {
    return new TimeScale(TimeUnit.MINUTE, 30);
  }

  public static final TimeScale halfDay() {
    return new TimeScale(TimeUnit.HOUR, 12);
  }

  public static final TimeScale triAnnual() {
    return new TimeScale(TimeUnit.MONTH, 4);
  }

}
