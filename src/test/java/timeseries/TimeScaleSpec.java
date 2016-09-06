package timeseries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class TimeScaleSpec {

  @Test
  public final void whenTotalDurationComputedThenResultCorrect() {
    TimeScale fiveDays = new TimeScale(TimeUnit.DAY, 5);
    assertThat(fiveDays.totalDuration(), is(closeTo(432000, 1E-15)));
  }

}
