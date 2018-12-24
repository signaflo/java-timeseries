package com.github.signaflo.timeseries;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import java.time.OffsetDateTime;

public class TimeSpec {

  private OffsetDateTime dateTime = null;

  @Test
  public void whenNullOffsetDateTimeThenNPE() {
    new Time(dateTime);
  }

  @Test
  public void whenOnlyHaveYearThenNewTimeDeadSimpleToCreate() {
    int year = 2020;
    Time time = Time.fromYear(year);
    assertThat(time, is(notNullValue()));
  }
}
