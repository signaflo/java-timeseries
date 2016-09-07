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
  public final void whenFractionalSecondsTotalComputedResultCorrect() {
    TimeScale fractionSecond = new TimeScale(TimeUnit.SECOND, 0.48);
    assertThat(fractionSecond.totalSeconds(), is(equalTo(0.48)));
  }
  
  @Test
  public final void whenMillisecondsTotalComputedResultCorrect() {
    TimeScale millis = new TimeScale(TimeUnit.MILLISECOND, 480);
    assertThat(millis.totalSeconds(), is(equalTo(0.48)));
  }

}
