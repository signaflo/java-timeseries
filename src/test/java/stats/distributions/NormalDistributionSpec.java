package stats.distributions;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NormalDistributionSpec {

    @Test
    public void whenNormalQuantileComputedResultCorrect() {
        Distribution norm = new Normal(0, 5);
        assertThat(norm.quantile(0.975), is(closeTo(1.96 * 5, 1E-2)));
        assertThat(norm.quantile(0.025), is(closeTo(-1.96 * 5, 1E-2)));
    }

    @Test
    public void whenNormalRandGeneratedDoubleReturned() {
        Distribution norm = new Normal(0, 2.5);
        assertThat(norm.rand(), isA(Double.class));
    }

    @Test
    public void testEqualsAndHashCode() {
        Distribution norm = new Normal(0, 5);
        Distribution norm2 = new Normal(0, 2.5);
        Distribution norm3 = new Normal(0, 5);
        Distribution norm4 = new Normal(1, 5);
        assertThat(norm, is(norm));
        assertThat(norm, is(not(norm2)));
        assertThat(norm2, is(not(norm)));
        assertThat(norm.hashCode(), is(not(norm2.hashCode())));
        assertThat(norm.hashCode(), is(norm3.hashCode()));
        assertThat(norm, is(norm3));
        assertThat(norm4, is(not(norm)));
        norm4 = null;
        assertThat(norm, is(not(norm4)));
        String string = "";
        assertThat(norm, is(not(string)));
    }

}
