package timeseries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class TimeUnitSpec {
  
  @Test
  public void whenTimeUnitFrequencyTimeScaleComputedThenResultCorrect() {
    TimeUnit timeUnit = TimeUnit.MONTH;
    TimeScale timeScale = new TimeScale(TimeUnit.QUARTER, 2);
    assertThat(timeUnit.frequencyPer(timeScale), is(equalTo(6.0)));
  }

  @Test
  public void whenMillisecondTimeUnitFrequencyWithSecondsScaleThenResultCorrect() {
    TimeUnit timeUnit = TimeUnit.MILLISECOND;
    TimeScale timeScale = new TimeScale(TimeUnit.SECOND, 5);
    assertThat(timeUnit.frequencyPer(timeScale), is(equalTo(5E3)));
  }

  @Test
  public void whenNanoSecondTimeUnitFrequencyWithMicrosecondUnitThenResultCorrect() {
    TimeUnit nanosecond = TimeUnit.NANOSECOND;
    TimeUnit microsecond = TimeUnit.MICROSECOND;
    assertThat(nanosecond.frequencyPer(microsecond), is(closeTo(1E3, 1E-10)));
  }
  
  @Test
  public void whenNanoSecondTimeUnitFrequencyWithMicrosecondScaleThenResultCorrect() {
    TimeUnit nanosecond = TimeUnit.NANOSECOND;
    TimeScale microsecond = new TimeScale(TimeUnit.MICROSECOND, 50);
    assertThat(nanosecond.frequencyPer(microsecond), is(closeTo(50E3, 1E-10)));
  }
}
