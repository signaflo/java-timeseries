package timeseries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class TimePeriodSpec {

  @Test
  public final void whenDayTotalSecondsComputedThenResultCorrect() {
    TimePeriod fiveDays = new TimePeriod(TimeUnit.DAY, 5);
    assertThat(fiveDays.totalSeconds(), is(closeTo(432000, 1E-15)));
  }
  
  @Test
  public final void whenMillisecondsTotalComputedResultCorrect() {
    TimePeriod millis = new TimePeriod(TimeUnit.MILLISECOND, 480);
    assertThat(millis.totalSeconds(), is(equalTo(0.48)));
  }
  
  @Test
  public final void whenNanosecondsTotalComputedResultCorrect() {
    TimePeriod nanos = new TimePeriod(TimeUnit.NANOSECOND, 480);
    assertThat(nanos.totalSeconds(), is(equalTo(480 * 1E-9)));
  }
  
  @Test
  public final void whenFrequencyPerComputedResultCorrect() {
    TimePeriod nanos = new TimePeriod(TimeUnit.MINUTE, 4);
    assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(equalTo(7.5)));
  }
  
  @Test
  public final void whenMilliFrequencyPerComputedResultCorrect() {
    TimePeriod nanos = new TimePeriod(TimeUnit.MILLISECOND, 480);
    assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(equalTo(3750.0)));
  }
  
  @Test
  public final void whenNanoFrequencyPerComputedResultCorrect() {
    TimePeriod nanos = new TimePeriod(TimeUnit.NANOSECOND, 480);
    assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(closeTo(3750.0*1E+6, 1E-4)));
  }
  
  @Test
  public final void whenFrequencyPerReverseComputedResultCorrect() {
    TimePeriod minutes = new TimePeriod(TimeUnit.MINUTE, 45);
    assertThat(minutes.frequencyPer(new TimePeriod(TimeUnit.SECOND, 15)), is(closeTo(0.00555555556, 1E-4)));
  }

}
