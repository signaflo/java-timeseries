package timeseries.models.arima;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import timeseries.TimeSeries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArimaSimulationSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenSimulateNLessThanOneThenException() {
        exception.expect(IllegalArgumentException.class);
        ArimaSimulation.newBuilder().setN(0);
    }

    @Test
    public void whenSimulateDistributionNullThenNPE() {
        exception.expect(NullPointerException.class);
        ArimaSimulation.newBuilder().setDistribution(null);
    }

    @Test
    public void whenSimulateCoefficientsNullThenNPE() {
        exception.expect(NullPointerException.class);
        ArimaSimulation.newBuilder().setCoefficients(null);
    }

    @Test
    public void whenSimulateThenSeriesOfSizeNReturned() {
        TimeSeries series = ArimaSimulation.newBuilder().setN(50).build().sim();
        assertThat(series.size(), is(50));
    }
}
