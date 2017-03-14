package timeseries.models;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import timeseries.TimeSeries;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RandomWalkSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenInstantiatedNullSeriesThenNPE() {
        exception.expect(NullPointerException.class);
        new RandomWalk(null);
    }

    @Test
    public void whenSimWithNullDistributionThenNPE() {
        exception.expect(NullPointerException.class);
        RandomWalk.simulate(null, 100);
    }

    @Test
    public void whenEmptyTimeSeriesThenIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);
        new RandomWalk(new TimeSeries());
    }

    @Test
    public void whenSimulatedThenSeriesHasGivenLength() {
        TimeSeries simulated = RandomWalk.simulate(0, 2, 10);
        assertThat(simulated.n(), is(10));
    }

}
