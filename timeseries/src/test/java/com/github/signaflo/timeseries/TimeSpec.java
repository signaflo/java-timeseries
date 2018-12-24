package com.github.signaflo.timeseries;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.common.testing.EqualsTester;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimeSpec {

  private OffsetDateTime dateTime = null;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenNullOffsetDateTimeThenNPE() {
    exception.expect(NullPointerException.class);
    new Time(dateTime);
  }

  @Test
  public void whenOnlyHaveYearThenNewTimeDeadSimpleToCreate() {
    int year = 2020;
    Time time = Time.fromYear(year);
    assertThat(time, is(notNullValue()));
  }

  @Test
  public void whenToInstantThenCorrectInstantReturned() {
    Instant instant = Instant.ofEpochSecond(3000L);
    Time time = new Time(instant.atOffset(ZoneOffset.UTC));
    assertThat(time.toInstant(), is(instant));
  }

  @Test
  public void testEqualsAndHashCode() {
    EqualsTester tester = new EqualsTester();
    Time year2020 = Time.fromYear(2020);
    Time year2021 = Time.fromYear(2021);
    Time year2020April = Time.fromYearMonth(2020, 4);
    Time year2020May = Time.fromYearMonth(2020, 5);
    Time year2021April = Time.fromYearMonth(2021, 4);
    Time year2020April1st = Time.fromYearMonthDay(2020, 4, 1);
    Time year2020April2nd = Time.fromYearMonthDay(2020, 4, 2);
    Time year2021April1st = Time.fromYearMonthDay(2021, 4, 1);
    tester.addEqualityGroup(year2020, Time.fromYear(2020))
        .addEqualityGroup(year2021, Time.fromYear(2021))
        .addEqualityGroup(year2020April, year2020April1st)
        .addEqualityGroup(year2020May)
        .addEqualityGroup(year2021April, year2021April1st)
        .addEqualityGroup(year2020April2nd);
    tester.testEquals();
  }
}
