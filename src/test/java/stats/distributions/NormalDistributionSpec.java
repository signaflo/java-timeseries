package stats.distributions;

import org.junit.Test;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class NormalDistributionSpec {
  
  @Test
  public void whenNormalQuantileComputedResultCorrect() {
    Normal norm = new Normal(0, 5);
    assertThat(norm.quantile(0.975), is(closeTo(1.96*5, 1E-2)));
    assertThat(norm.quantile(0.025), is(closeTo(-1.96*5, 1E-2)));
    
  }

}
