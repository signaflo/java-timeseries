package timeseries.model.arima;

import org.junit.Test;
import timeseries.TimePeriod;
import timeseries.TimeSeries;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ArimaProcessSpec {

    private ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
    private ArimaCoefficients coefficients = builder.setMACoeffs(0.4)
                                                    .setARCoeffs(0.7)
                                                    .build();
    private ArimaProcess process = ArimaProcess.builder().setCoefficients(coefficients).build();

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

//    @Test
//    public void whenArimaProcessThenHasNextIsTrue() {
//        assertThat(process.hasNext(), is(true));
//    }

    @Test
    public void testProcess() {
        coefficients = ArimaCoefficients.builder().setDifferences(2).setARCoeffs(0.5)
                                        .setSeasonalDifferences(1).build();
        process = ArimaProcess.builder().setCoefficients(coefficients).build();
        double[] sim = new double[1000];
        for (int i = 0; i < sim.length; i++) {
            sim[i] = process.getAsDouble();
        }
        TimeSeries ts = TimeSeries.from(TimePeriod.oneMonth(), sim);
        ArimaOrder order = ArimaOrder.order(1, 2, 0, 0, 1, 0);
        Arima model = Arima.model(ts, order);
        System.out.println(model.sigma2());
    }
}
