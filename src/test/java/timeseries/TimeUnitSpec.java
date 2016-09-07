package timeseries;

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
