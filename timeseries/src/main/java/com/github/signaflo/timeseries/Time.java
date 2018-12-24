package com.github.signaflo.timeseries;

import lombok.NonNull;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Represents a particular point in time.
 */
public final class Time {

  private final OffsetDateTime dateTime;

  Time(@NonNull OffsetDateTime dateTime) {
    this.dateTime = dateTime;
  }

  public Time plus(TimePeriod timePeriod) {
    return plus(timePeriod.periodLength(), timePeriod);
  }

  private Time plus(long amountToAdd, TimePeriod timePeriod) {
    OffsetDateTime addedDateTime = this.dateTime.plus(amountToAdd, timePeriod.timeUnit());
    return new Time(addedDateTime);
  }

  public Instant toInstant() {
    return this.dateTime.toInstant();
  }

  static Time now() {
    return new Time(OffsetDateTime.now());
  }

  public static Time fromYear(int year) {
    ZoneOffset utc = ZoneOffset.UTC;
    OffsetDateTime dateTime = OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, utc);
    return new Time(dateTime);
  }

  public static Time fromYearMonth(int year, int month) {
    ZoneOffset utc = ZoneOffset.UTC;
    OffsetDateTime dateTime = OffsetDateTime.of(year, month, 1, 0, 0, 0, 0, utc);
    return new Time(dateTime);
  }

  public static Time fromYearMonthDay(int year, int month, int day) {
    ZoneOffset utc = ZoneOffset.UTC;
    OffsetDateTime dateTime = OffsetDateTime.of(year, month, day, 0, 0, 0, 0, utc);
    return new Time(dateTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Time time = (Time) o;
    return dateTime.equals(time.dateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dateTime);
  }
}
