package timeseries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class TimeScaleSpec {

  @Test
  public final void whenDayTotalSecondsComputedThenResultCorrect() {
    TimeScale fiveDays = new TimeScale(TimeUnit.DAY, 5);
    assertThat(fiveDays.totalSeconds(), is(closeTo(432000, 1E-15)));
  }
  
  @Test
  public final void whenMillisecondsTotalComputedResultCorrect() {
    TimeScale millis = new TimeScale(TimeUnit.MILLISECOND, 480);
    assertThat(millis.totalSeconds(), is(equalTo(0.48)));
  }
  
  @Test
  public final void whenNanosecondsTotalComputedResultCorrect() {
    TimeScale nanos = new TimeScale(TimeUnit.NANOSECOND, 480);
    assertThat(nanos.totalSeconds(), is(equalTo(480 * 1E-9)));
  }
  
  @Test
  public final void whenFrequencyPerComputedResultCorrect() {
    TimeScale nanos = new TimeScale(TimeUnit.MINUTE, 4);
    assertThat(nanos.frequencyPer(TimeScale.halfHour()), is(equalTo(7.5)));
  }
  
  @Test
  public final void whenMilliFrequencyPerComputedResultCorrect() {
    TimeScale nanos = new TimeScale(TimeUnit.MILLISECOND, 480);
    assertThat(nanos.frequencyPer(TimeScale.halfHour()), is(equalTo(3750.0)));
  }
  
  @Test
  public final void whenNanoFrequencyPerComputedResultCorrect() {
    TimeScale nanos = new TimeScale(TimeUnit.NANOSECOND, 480);
    assertThat(nanos.frequencyPer(TimeScale.halfHour()), is(closeTo(3750.0*1E+6, 1E-4)));
  }
  
  @Test
  public final void whenFrequencyPerReverseComputedResultCorrect() {
    TimeScale minutes = new TimeScale(TimeUnit.MINUTE, 45);
    assertThat(minutes.frequencyPer(new TimeScale(TimeUnit.SECOND, 15)), is(closeTo(0.00555555556, 1E-4)));
  }

}
