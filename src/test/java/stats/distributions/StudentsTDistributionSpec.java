package stats.distributions;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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

    @Test
    public void testEqualsAndHashCode() {
        Distribution t = new StudentsT(5);
        Distribution t2 = new StudentsT(20);
        Distribution t3 = new StudentsT(5);
        assertThat(t, is(t));
        assertThat(t, is(not(t2)));
        assertThat(t2, is(not(t)));
        assertThat(t.hashCode(), is(not(t2.hashCode())));
        assertThat(t.hashCode(), is(t3.hashCode()));
        assertThat(t, is(t3));
        t3 = null;
        assertThat(t, is(not(t3)));
        String string = "";
        assertThat(t, is(not(string)));
    }
}
