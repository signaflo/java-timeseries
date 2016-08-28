package timeseries;

import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;

public enum TimeScale {
  
  DECADE(ChronoUnit.DECADES, ChronoField.YEAR, 1L),
  YEAR(ChronoUnit.YEARS, ChronoField.YEAR, 1L),
  QUARTER(ChronoUnit.MONTHS, ChronoField.MONTH_OF_YEAR, 3L),
  MONTH(ChronoUnit.MONTHS, ChronoField.MONTH_OF_YEAR, 1L),
  WEEK(ChronoUnit.WEEKS, ChronoField.ALIGNED_WEEK_OF_YEAR, 1L),
  DAY(ChronoUnit.DAYS, ChronoField.DAY_OF_WEEK, 1L),
  HOUR(ChronoUnit.HOURS, ChronoField.HOUR_OF_DAY, 1L),
  MINUTE(ChronoUnit.MINUTES, ChronoField.MINUTE_OF_HOUR, 1L),
  SECOND(ChronoUnit.SECONDS, ChronoField.SECOND_OF_MINUTE, 1L),
  MILLISECOND(ChronoUnit.MILLIS, ChronoField.MILLI_OF_SECOND, 1L),
  NANOSECOND(ChronoUnit.NANOS, ChronoField.NANO_OF_SECOND, 1L);
  
  private final TemporalUnit timeUnit;
  private final TemporalField timeField;
  private final long periodLength;
  
  TimeScale(TemporalUnit timeUnit, TemporalField timeField, long periodLength) {
    this.timeUnit = timeUnit;
    this.timeField = timeField;
    this.periodLength = periodLength;
  }
  
  TemporalUnit timeUnit() {
    return this.timeUnit;
  }
  
  TemporalField timeField() {
    return this.timeField;
  }

  long periodLength() {
    return this.periodLength;
  }
}
