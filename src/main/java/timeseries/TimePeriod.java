package timeseries;

/**
 * Represents an amount of time expressed in a particular time unit.
 * This class wraps a {@link TimeUnit} together with a positive integer period length, 
 * allowing one to create a wide range of different time periods.
 * This class is immutable and thread-safe.
 * 
 * @author Jacob Rachiele
 *
 */
public final class TimePeriod {

  private final TimeUnit timeUnit;
  private final long periodLength;

  /**
   * Construct a new TimePeriod with the given unit of time and period length.
   * 
   * @param timeUnit the unit of time underlying this time period
   * @param periodLength the length of this time period relative to the given unit of time. Note the
   * periodLength argument must be a long. Most decimal time periods can be modeled by converting to
   * an appropriate time unit with a smaller order of magnitude. For example, the {@link TimePeriod#halfMonth}
   * constructor works by converting 15.2184375 days to 1314873 seconds.
   */
  public TimePeriod(final TimeUnit timeUnit, final long periodLength) {
    validate(periodLength);
    this.timeUnit = timeUnit;
    this.periodLength = periodLength;
  }

  /**
   * The unit of time underlying this time period.
   * 
   * @return the unit of time underlying this time period.
   */
  public final TimeUnit timeUnit() {
    return this.timeUnit;
  }

  /**
   * The length of this time period relative to the underlying time unit.
   * 
   * @return the length of this time period relative to the underlying time unit.
   */
  public final long periodLength() {
    return this.periodLength;
  }

  /**
   * Compute and return the number of times this TimePeriod occurs in the given TimePeriod.
   * <p>
   * For example, if this time period is a month and the given time period is half a year, the return value is
   * 6 since a month occurs 6 times in one year.
   * </p>
   * 
   * @param otherTimePeriod the time period for which the frequency of occurrence of this time period is to be found.
   * @return the number of times this TimePeriod occurs in the provided TimePeriod.
   */
  public double frequencyPer(final TimePeriod otherTimePeriod) {
    return otherTimePeriod.totalSeconds() / this.totalSeconds();
  }

  /**
   * The total amount of time in this time period measured in seconds, the base SI unit of time.
   * 
   * @return the total amount of time in this time period measured in seconds.
   */
  public final double totalSeconds() {
    double thisDuration = this.timeUnit.totalDuration();
    return thisDuration * this.periodLength;
  }
  
  /**
   * Create and return a new TimePeriod representing exactly one year.
   * 
   * @return a new TimePeriod representing exactly one year.
   */
  public static TimePeriod oneYear() {
    return new TimePeriod(TimeUnit.YEAR, 1);
  }

  /**
   * Create and return a new TimePeriod representing exactly two years.
   * 
   * @return a new TimePeriod representing exactly two years.
   */
  public static TimePeriod twoYears() {
    return new TimePeriod(TimeUnit.YEAR, 2);
  }

  /**
   * Create and return a new TimePeriod representing one half of a decade.
   * 
   * @return a new TimePeriod representing one half of a decade.
   */
  public static TimePeriod halfDecade() {
    return new TimePeriod(TimeUnit.YEAR, 5);
  }

  /**
   * Create and return a new TimePeriod representing exactly one month.
   * 
   * @return a new TimePeriod representing exactly one month.
   */
  public static TimePeriod oneMonth() {
    return new TimePeriod(TimeUnit.MONTH, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a month.
   * 
   * @return a new TimePeriod representing one half of a month.
   */
  public static TimePeriod halfMonth() {
    return new TimePeriod(TimeUnit.SECOND, 1314873);
  }

  /**
   * Create and return a new TimePeriod representing one quarter of a year.
   * 
   * @return a new TimePeriod representing one quarter of a year.
   */
  public static TimePeriod oneQuarter() {
    return new TimePeriod(TimeUnit.QUARTER, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a year.
   * 
   * @return a new TimePeriod representing one half of a year.
   */
  public static TimePeriod halfYear() {
    return new TimePeriod(TimeUnit.MONTH, 6);
  }

  /**
   * Create and return a new TimePeriod representing exactly one decade.
   * 
   * @return a new TimePeriod representing exactly one decade.
   */
  public static TimePeriod oneDecade() {
    return new TimePeriod(TimeUnit.DECADE, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of a century
   * 
   * @return a new TimePeriod representing one half of a century.
   */
  public static TimePeriod halfCentury() {
    return new TimePeriod(TimeUnit.DECADE, 5);
  }

  /**
   * Create and return a new TimePeriod representing exactly one century.
   * 
   * @return a new TimePeriod representing exactly one century.
   */
  public static TimePeriod oneCentury() {
    return new TimePeriod(TimeUnit.CENTURY, 1);
  }

  /**
   * Create and return a new TimePeriod representing one half of an hour.
   * 
   * @return a new TimePeriod representing one half of an hour.
   */
  public static TimePeriod halfHour() {
    return new TimePeriod(TimeUnit.MINUTE, 30);
  }

  /**
   * Create and return a new TimePeriod representing one half of a day.
   * 
   * @return a new TimePeriod representing one half of a day.
   */
  public static TimePeriod halfDay() {
    return new TimePeriod(TimeUnit.HOUR, 12);
  }

  /**
   * Create and return a new TimePeriod representing one third of a year.
   * 
   * @return a new TimePeriod representing one third of a year.
   */
  public static TimePeriod triAnnual() {
    return new TimePeriod(TimeUnit.MONTH, 4);
  }
  
  /**
   * Create and return a new TimePeriod representing one half of a second.
   * 
   * @return a new TimePeriod representing one half of a second.
   */
  public static TimePeriod halfSecond() {
    return new TimePeriod(TimeUnit.MILLISECOND, 500);
  }
  
  /**
   * Create and return a new TimePeriod representing one tenth of a second.
   * 
   * @return a new TimePeriod representing one tenth of a second.
   */
  public static TimePeriod oneTenthSecond() {
    return new TimePeriod(TimeUnit.MILLISECOND, 100);
  }
  
  private void validate(final long unitLength) {
    if (unitLength <= 0) {
      throw new IllegalArgumentException("The provided unitLength must be a positive integer");
    }
    
  }

  @Override
  public String toString() {
    return "timeUnit: " + timeUnit + "\nperiodLength: " + periodLength;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((timeUnit == null) ? 0 : timeUnit.hashCode());
    long temp;
    temp = Double.doubleToLongBits(periodLength);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    TimePeriod other = (TimePeriod) obj;
    return timeUnit == other.timeUnit && Double.doubleToLongBits(periodLength) == Double.doubleToLongBits(other.periodLength);
  }

}
