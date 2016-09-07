package timeseries;

/**
 * Wraps a {@link TimeUnit} together with a positive integer unit length, allowing one to specify a broader range of time
 * periods than using a TimeUnit alone. This class is immutable and thread-safe.
 * 
 * @author Jacob Rachiele
 *
 */
public final class TimeScale {

  private final TimeUnit timeUnit;
  private final long unitLength;

  /**
   * Construct a new TimeScale with the given unit of time and unit length.
   * 
   * @param timeUnit the unit of time underlying this time scale
   * @param unitLength the length of this time scale relative to the given unit of time. Note the
   * unitLength argument must be a long. Most decimal time scales can be modeled by converting to
   * an appropriate time unit with a smaller order of magnitude. For example, the {@link halfMonth} 
   * constructor works by converting 15.2184375 days to 1314873 seconds.
   */
  public TimeScale(final TimeUnit timeUnit, final long unitLength) {
    validate(unitLength);
    this.timeUnit = timeUnit;
    this.unitLength = unitLength;
  }

  /**
   * The unit of time underlying this time scale.
   * 
   * @return the unit of time underlying this time scale.
   */
  public final TimeUnit timeUnit() {
    return this.timeUnit;
  }

  /**
   * The length of this unit of time relative to the underlying time scale.
   * 
   * @return the length of this unit of time relative to the underlying time scale.
   */
  public final long unitLength() {
    return this.unitLength;
  }

  /**
   * Compute and return the number of times this TimeScale occurs in the given TimeScale.
   * <p>
   * For example, if this time scale is a month and the given time scale is half a year, the return value should equal
   * 6, since a month occurs 6 times in one year.
   * </p>
   * 
   * @param otherTimeScale the time scale for which the frequency of occurrence of this time scale is to be found.
   * @return the number of times this TimeScale occurs in the provided TimeScale.
   */
  public double frequencyPer(final TimeScale otherTimeScale) {
    return otherTimeScale.totalSeconds() / this.totalSeconds();
  }

  /**
   * The total amount of time in this time scale measured in seconds, the base SI unit of time.
   * 
   * @return the total amount of time in this time scale measured in seconds.
   */
  public final double totalSeconds() {

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

  /**
   * Create and return a new TimeScale representing exactly two years.
   * 
   * @return a new TimeScale representing exactly two years.
   */
  public static final TimeScale twoYears() {
    return new TimeScale(TimeUnit.YEAR, 2);
  }

  /**
   * Create and return a new TimeScale representing one half of a decade.
   * 
   * @return a new TimeScale representing one half of a decade.
   */
  public static final TimeScale halfDecade() {
    return new TimeScale(TimeUnit.YEAR, 5);
  }

  /**
   * Create and return a new TimeScale representing exactly one month.
   * 
   * @return a new TimeScale representing exactly one month.
   */
  public static final TimeScale oneMonth() {
    return new TimeScale(TimeUnit.MONTH, 1);
  }

  /**
   * Create and return a new TimeScale representing one half of a month.
   * 
   * @return a new TimeScale representing one half of a month.
   */
  public static final TimeScale halfMonth() {
    return new TimeScale(TimeUnit.SECOND, 1314873);
  }

  /**
   * Create and return a new TimeScale representing one quarter of a year.
   * 
   * @return a new TimeScale representing one quarter of a year.
   */
  public static final TimeScale oneQuarter() {
    return new TimeScale(TimeUnit.QUARTER, 1);
  }

  /**
   * Create and return a new TimeScale representing one half of a year.
   * 
   * @return a new TimeScale representing one half of a year.
   */
  public static final TimeScale halfYear() {
    return new TimeScale(TimeUnit.MONTH, 6);
  }

  /**
   * Create and return a new TimeScale representing exactly one decade.
   * 
   * @return a new TimeScale representing exactly one decade.
   */
  public static final TimeScale oneDecade() {
    return new TimeScale(TimeUnit.DECADE, 1);
  }

  /**
   * Create and return a new TimeScale representing half of a century
   * 
   * @return a new TimeScale representing half of a century.
   */
  public static final TimeScale halfCentury() {
    return new TimeScale(TimeUnit.DECADE, 5);
  }

  /**
   * Create and return a new TimeScale representing exactly one century.
   * 
   * @return a new TimeScale representing exactly one century.
   */
  public static final TimeScale oneCentury() {
    return new TimeScale(TimeUnit.CENTURY, 1);
  }

  /**
   * Create and return a new TimeScale representing half on one hour.
   * 
   * @return a new TimeScale representing half on one hour.
   */
  public static final TimeScale halfHour() {
    return new TimeScale(TimeUnit.MINUTE, 30);
  }

  /**
   * Create and return a new TimeScale representing half of one day.
   * 
   * @return a new TimeScale representing half of one day.
   */
  public static final TimeScale halfDay() {
    return new TimeScale(TimeUnit.HOUR, 12);
  }

  /**
   * Create and return a new TimeScale representing one third of a year.
   * 
   * @return a new TimeScale representing one third of a year.
   */
  public static final TimeScale triAnnual() {
    return new TimeScale(TimeUnit.MONTH, 4);
  }
  
  public static final TimeScale halfSecond() {
    return new TimeScale(TimeUnit.MILLISECOND, 500);
  }
  
  public static final TimeScale oneTenthSecond() {
    return new TimeScale(TimeUnit.MILLISECOND, 100);
  }
  
  private final void validate(final long unitLength) {
    if (unitLength <= 0) {
      throw new IllegalArgumentException("The provided unitLength must be a positive integer");
    }
    
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("timeUnit: ").append(timeUnit).append("\nunitLength: ").append(unitLength);
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((timeUnit == null) ? 0 : timeUnit.hashCode());
    long temp;
    temp = Double.doubleToLongBits(unitLength);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    TimeScale other = (TimeScale) obj;
    if (timeUnit != other.timeUnit)
      return false;
    if (Double.doubleToLongBits(unitLength) != Double.doubleToLongBits(other.unitLength))
      return false;
    return true;
  }

}
