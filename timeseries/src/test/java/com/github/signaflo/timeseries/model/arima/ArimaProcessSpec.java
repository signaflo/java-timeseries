package com.github.signaflo.timeseries.model.arima;

import com.google.common.testing.EqualsTester;
import org.junit.Test;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ArimaProcessSpec {

    private ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
    private ArimaCoefficients coefficients = builder.setMACoeffs(0.4)
                                                    .setARCoeffs(0.7)
                                                    .build();
    private ArimaProcess process = ArimaProcess.builder()
                                               .setCoefficients(coefficients)
                                               .build();

    @Test
    public void whenMAOneThenErrorsSizeOne() {
        process.getNext(10);
        assertThat(process.getErrors().length, is(1));
    }

    @Test
    public void whenAROneThenDifferencedSeriesSizeOne() {
        process.getNext(10);
        assertThat(process.getDiffSeries().length, is(1));
    }

    @Test
    public void whenOneDifferenceThenSeriesSizeOne() {
        coefficients = ArimaCoefficients.builder().setDifferences(1).build();
        process = ArimaProcess.builder().setCoefficients(coefficients).build();
        process.getNext(10);
        assertThat(process.getSeries().length, is(1));
    }

    @Test
    public void whenArimaProcessThenNextValueNotNull() {
        assertThat(process.getAsDouble(), is(not(nullValue())));
    }

    @Test
    public void whenArimaProcessThenHasNextIsTrue() {
        assertThat(process.hasNext(), is(true));
    }

    @Test
    public void whenStartOverThenNewProcessEqualsOldOne() {
        process.getAsDouble();
        process.nextDouble();
        assertThat(process.startOver(), is(process));
    }

    @Test
    public void whenSimulateThenSeriesOfCorrectSizeReturned() {
        TimeSeries ts = process.simulate(10);
        assertThat(ts.size(), is(10));
    }

    @Test
    public void testEqualsAndHashCode() {
        ArimaProcess process1 = ArimaProcess.builder()
                                            .setCoefficients(coefficients)
                                            .build();
        ArimaCoefficients coeffs = ArimaCoefficients.builder().build();
        ArimaProcess processA1 = ArimaProcess.builder()
                                             .setCoefficients(coeffs)
                                             .build();
        ArimaProcess processA2 = ArimaProcess.builder()
                                             .setCoefficients(coeffs)
                                             .build();
        ArimaProcess processB1 = ArimaProcess.builder()
                                             .setPeriod(TimePeriod.halfDay())
                                             .build();
        ArimaProcess processB2 = ArimaProcess.builder()
                                             .setPeriod(TimePeriod.halfDay())
                                             .build();
        new EqualsTester()
                .addEqualityGroup(process, process1)
                .addEqualityGroup(processA1, processA2)
                .addEqualityGroup(processB1, processB2)
                .testEquals();
    }
}
