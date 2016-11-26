package stats.distributions;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

/**
 * Created by jacob on 11/25/16.
 */
public class StudentsTDistributionSpec {

  @Test
  public void whenNormalQuantileComputedResultCorrect() {
    Distribution st = new StudentsT(5);
    assertThat(st.quantile(0.975), is(closeTo(2.570582, 1E-2)));
    assertThat(st.quantile(0.025), is(closeTo(-2.570582, 1E-2)));
  }

  @Test
  public void whenNormalRandGeneratedDoubleReturned() {
    Distribution st = new StudentsT(5);
    assertThat(st.rand(), isA(Double.class));
  }
}
