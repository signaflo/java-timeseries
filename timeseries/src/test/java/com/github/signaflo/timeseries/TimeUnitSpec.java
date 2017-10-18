/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package com.github.signaflo.timeseries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class TimeUnitSpec {
  
  @Test
  public void whenTimeUnitFrequencyTimePeriodComputedThenResultCorrect() {
    TimeUnit timeUnit = TimeUnit.MONTH;
    TimePeriod timePeriod = new TimePeriod(TimeUnit.QUARTER, 2);
    assertThat(timeUnit.frequencyPer(timePeriod), is(equalTo(6.0)));
  }

  @Test
  public void whenMillisecondTimeUnitFrequencyWithSecondsPeriodThenResultCorrect() {
    TimeUnit timeUnit = TimeUnit.MILLISECOND;
    TimePeriod timePeriod = new TimePeriod(TimeUnit.SECOND, 5);
    assertThat(timeUnit.frequencyPer(timePeriod), is(equalTo(5E3)));
  }

  @Test
  public void whenNanoSecondTimeUnitFrequencyWithMicrosecondUnitThenResultCorrect() {
    TimeUnit nanosecond = TimeUnit.NANOSECOND;
    TimeUnit microsecond = TimeUnit.MICROSECOND;
    assertThat(nanosecond.frequencyPer(microsecond), is(closeTo(1E3, 1E-10)));
  }
  
  @Test
  public void whenNanoSecondTimeUnitFrequencyWithMicrosecondPeriodThenResultCorrect() {
    TimeUnit nanosecond = TimeUnit.NANOSECOND;
    TimePeriod microsecond = new TimePeriod(TimeUnit.MICROSECOND, 50);
    assertThat(nanosecond.frequencyPer(microsecond), is(closeTo(50E3, 1E-10)));
  }
}
