package timeseries.model.arima;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ArimaProcessSpec {

    private ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
    private ArimaCoefficients coefficients = builder.setMACoeffs(0.4).build();
    private ArimaProcess process = ArimaProcess.builder().setCoefficients(coefficients).build();

    @Test
    public void whenMAOnePastThenErrorsSizeOne() {
        assertThat(process.getErrors().length, is(1));
    }

    @Test
    public void whenOneDifferenceThenSeriesSizeOne() {
        coefficients = builder.setDifferences(1).build();
        process = ArimaProcess.builder().setCoefficients(coefficients).build();
        assertThat(process.getSeries().length, is(1));
    }

    @Test
    public void whenArimaProcessThenNextValueNotNull() {
        assertThat(process.next(), is(not(nullValue())));
    }

    @Test
    public void whenArimaProcessThenHasNextIsTrue() {
        assertThat(process.hasNext(), is(true));
    }
}
