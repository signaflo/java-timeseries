package timeseries.model.arima;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ArimaCoefficientsSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenDifferencesWithMeanSetThenIllegalState() {
        exception.expect(IllegalStateException.class);
        ArimaCoefficients.builder().setDifferences(1).setMean(50.0).build();
    }

    @Test
    public void whenMoreThanOneDifferenceWithDriftSetThenIllegalState() {
        exception.expect(IllegalStateException.class);
        ArimaCoefficients.builder().setDifferences(1).setSeasonalDifferences(1).setDrift(50.0).build();
    }

    @Test
    public void testArimaCoefficientsEqualsAndHashCode() {
        ArimaCoefficients coeffs1 = ArimaCoefficients.builder().setARCoeffs(0.3).build();
        ArimaCoefficients coeffs2 = ArimaCoefficients.builder().setMACoeffs(-0.2).build();
        ArimaCoefficients coeffs3 = ArimaCoefficients.builder().setARCoeffs(0.3).build();
        assertThat(coeffs1, is(coeffs1));
        assertThat(coeffs1.hashCode(), is(coeffs3.hashCode()));
        assertThat(coeffs1, is(coeffs3));
        assertThat(coeffs1, is(not(coeffs2)));
        assertThat(coeffs1, is(not(new Object())));
        assertThat(coeffs1.equals(null), is(false));
    }
}
