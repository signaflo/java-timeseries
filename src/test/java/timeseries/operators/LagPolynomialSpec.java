package timeseries.operators;

import data.TestData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import timeseries.TimeSeries;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public final class LagPolynomialSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenDifferencesLessThanZeroThenException() {
        exception.expect(IllegalArgumentException.class);
        LagPolynomial.differences(-1);
    }

    @Test
    public void whenSeasDifferencesLessThanZeroThenException() {
        exception.expect(IllegalArgumentException.class);
        LagPolynomial.seasonalDifferences(12, -1);
    }

    @Test
    public void whenSeasonalDifferenceThenRightDataReturned() {
        LagPolynomial poly = LagPolynomial.seasonalDifferences(4, 1);
        LagPolynomial expected = new LagPolynomial(0.0, 0.0, 0.0, -1.0);
        assertThat(poly, is(expected));
    }

    @Test
    public void whenLagPolyFitThenResultCorrect() {
        TimeSeries series = TestData.ausbeer();
        LagPolynomial poly = LagPolynomial.firstDifference();
        assertThat(poly.fit(series, 2), is(equalTo(series.at(1))));
        OffsetDateTime thirdObservationPeriod = OffsetDateTime.of(1956, 7, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertThat(poly.fit(series, thirdObservationPeriod), is(equalTo(series.at(1))));
        poly = LagPolynomial.movingAverage(0.5);
        assertThat(poly.fit(series, 2), is(equalTo(series.at(1) * 0.5)));
        assertThat(poly.fit(series.asArray(), 2), is(equalTo(series.at(1) * 0.5)));
    }

    @Test
    public void whenLagPolyAppliedThenResultCorrect() {
        TimeSeries series = TestData.ausbeer();
        LagPolynomial poly = LagPolynomial.firstDifference();
        assertThat(poly.apply(series, 1), is(equalTo(series.at(1) - series.at(0))));
        OffsetDateTime secondObservationPeriod = OffsetDateTime.of(1956, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertThat(poly.apply(series, secondObservationPeriod), is(equalTo(series.at(1) - series.at(0))));
    }

    @Test
    public void whenLagPolyTwoDiffFitThenResultCorrect() {
        TimeSeries series = TestData.ausbeer();
        LagPolynomial poly = LagPolynomial.differences(2);
        assertThat(poly.fit(series, 2), is(equalTo(2 * series.at(1) - series.at(0))));
    }

    @Test
    public void whenZeroDegreeLagPolynomialCreatedThenZeroDegreePolyReturned() {
        LagPolynomial poly = new LagPolynomial();
        assertThat(poly.coefficients(), is(equalTo(new double[]{1.0})));
    }

    @Test
    public void whenZeroDegreeLagPolyMultipliedByDiffPolyCoeffsCorrect() {
        LagPolynomial poly = new LagPolynomial();
        LagPolynomial diff = LagPolynomial.firstDifference();
        LagPolynomial polyDiff = poly.times(diff);
        assertThat(polyDiff.coefficients(), is(equalTo(new double[]{1.0, -1.0})));
    }

    @Test
    public void whenSecondDiffLagPolyCreatedCoefficientsCorrect() {
        LagPolynomial poly = LagPolynomial.differences(2);
        assertThat(poly.coefficients(), is(equalTo(new double[]{1.0, -2.0, 1.0})));
    }

    @Test
    public void whenSecondDiffArOneLagPolyCreatedCoefficientsCorrect() {
        LagPolynomial diffPoly = LagPolynomial.differences(2);
        LagPolynomial arOne = LagPolynomial.autoRegressive(0.5);
        LagPolynomial poly = diffPoly.times(arOne);
        assertThat(poly.coefficients(), is(equalTo(new double[]{1.0, -2.5, 2.0, -0.5})));
    }

    @Test
    public void whenNewAutoRegressivePolyCreatedCofficientsCorrect() {
        LagPolynomial poly = LagPolynomial.autoRegressive(0.4);
        assertThat(poly.coefficients(), is(equalTo(new double[]{1.0, -0.4})));
    }

    @Test
    public void whenNewMovingAveragePolyCreatedCofficientsCorrect() {
        LagPolynomial poly = LagPolynomial.movingAverage(0.4);
        assertThat(poly.coefficients(), is(equalTo(new double[]{1.0, 0.4})));
    }

    @Test
    public void whenGetParametersThenSameAsArgsPassedIn() {
        LagPolynomial poly = new LagPolynomial(0.4);
        assertThat(poly.parameters(), is(new double[]{0.4}));
    }

    @Test
    public void whenSeasonalDifferenceThenPolyAsExpected() {
        LagPolynomial poly = LagPolynomial.seasonalDifferences(4, 2);
        assertThat(poly.coefficients(), is(new double[]{1.0, 0.0, 0.0, 0.0, -2.0, 0.0, 0.0, 0.0, 1.0}));
    }

    @Test
    public void whenLagOperatorAppliedThenExpectedResult() {
        TimeSeries series = TestData.ausbeer();
        OffsetDateTime dateTime = OffsetDateTime.of(1956, 4, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertThat(LagOperator.apply(series, dateTime), is(series.at(0)));
        assertThat(LagOperator.apply(series, 1), is(series.at(0)));
    }
}
  