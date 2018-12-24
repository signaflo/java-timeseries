package com.github.signaflo.timeseries;

import lombok.NonNull;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Represents a particular point in time.
 */
public final class Time {

  private final OffsetDateTime dateTime;

  Time(@NonNull OffsetDateTime dateTime) {
    this.dateTime = dateTime;
  }

  static Time fromYear(int year) {
    ZoneOffset utc = ZoneOffset.UTC;
    OffsetDateTime dateTime = OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, utc);
    return new Time(dateTime);
  }

  Time plus(long amountToAdd, TimePeriod timePeriod) {
    OffsetDateTime addedDateTime = this.dateTime.plus(amountToAdd, timePeriod.timeUnit().temporalUnit());
    return new Time(addedDateTime);
  }
}
