package com.github.signaflo.timeseries.model.arima;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import com.github.signaflo.timeseries.TimeSeries;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ArimaSimulationSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenSimulateNLessThanOneThenException() {
        exception.expect(IllegalArgumentException.class);
        ArimaSimulation.builder().setN(0);
    }

    @Test
    public void whenSimulateDistributionNullThenNPE() {
        exception.expect(NullPointerException.class);
        ArimaSimulation.builder().setDistribution(null);
    }

    @Test
    public void whenSimulateCoefficientsNullThenNPE() {
        exception.expect(NullPointerException.class);
        ArimaSimulation.builder().setCoefficients(null);
    }

    @Test
    public void whenSimulateThenSeriesOfSizeNReturned() {
        TimeSeries series = ArimaSimulation.builder().setN(50).build().sim();
        assertThat(series.size(), is(50));
    }
}
