package timeseries;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

//Immutable
public enum TimeScale {

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

  private final TemporalUnit timeUnit;
  private final long periodLength;

  TimeScale(TemporalUnit timeUnit, long periodLength) {
    this.timeUnit = timeUnit;
    this.periodLength = periodLength;
  }

  public TemporalUnit timeUnit() {
    return this.timeUnit;
  }

  public long periodLength() {
    return this.periodLength;
  }

  double per(final TimeScale otherTimeScale) {
    return otherTimeScale.totalDuration() / this.totalDuration();

  }

  /**
   * The total amount of time in this time scale measured in seconds, the base SI unit of time.
   * @return the total amount of time in this time scale measured in seconds.
   */
  double totalDuration() {
    
    Duration thisDuration = this.timeUnit.getDuration();
    
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
