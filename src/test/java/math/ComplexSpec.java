package math;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ComplexSpec {

    private static final double EPSILON = Math.ulp(1.0);

    @Test
    public void whenComplexNumberCreatedModulusCorrect() {
        assertThat(new Complex(3, 4).abs(), is(equalTo(5.0)));
    }

    @Test
    public void whenNoArgConstructorThenComplexZero() {
        Complex shouldBeZero = new Complex();
        assertThat(shouldBeZero.real(), is(0.0));
        assertThat(shouldBeZero.im(), is(0.0));
    }

    @Test
    public void whenComplexNumbersAddedCorrectSumGiven() {
        Complex c1 = new Complex(3, 5);
        Complex c2 = new Complex(2.4, 3.7);
        Complex sum = c1.plus(c2);
        assertThat(sum.real(), is(equalTo(5.4)));
        assertThat(sum.im(), is(equalTo(8.7)));
    }

    @Test
    public void whenComplexNumbersSubtractedCorrectDiffGiven() {
        Complex c1 = new Complex(3, 5);
        Complex c2 = new Complex(2.4, 3.7);
        Complex diff = c1.minus(c2);
        Complex expected = new Complex(0.6, 1.3);
        assertThat(diff.abs() - expected.abs(), is(lessThan(EPSILON)));
    }

    @Test
    public void whenComplexNumbersMultipliedCorrectProductGiven() {
        Complex c1 = new Complex(3, 5);
        Complex c2 = new Complex(2.4, 3.7);
        Complex product = c1.times(c2);
        assertThat(product.real(), is(equalTo(-11.3)));
        assertThat(product.im(), is(equalTo(23.1)));
    }

    @Test
    public void whenSqrtComputedThenResultCorrect() {
        Complex c1 = new Complex(3, 5);
        assertThat(c1.sqrt(), is(equalTo(new Complex(2.101303392521568, 1.189737764140758))));
    }

    @Test
    public void whenSqrtWithNegativeRealComputedThenResultCorrect() {
        Complex c1 = new Complex(-3.0, 1.5);
        Complex expected = new Complex(0.4207742662340964, 1.782428394950227);
        assertThat(c1.sqrt().abs() - expected.abs(), is(lessThan(EPSILON)));
    }

    @Test
    public void whenComplexPlusDoubleThenResultCorrect() {
        Complex z = new Complex(3, 5);
        double r = 5.830951894845301;
        Complex expected = new Complex(8.8309518948453, 5);
        assertThat(z.plus(r).abs() - expected.abs(), is(lessThan(EPSILON)));
    }

    @Test
    public void whenComplexDividedByDoubleThenResultCorrect() {
        Complex z = new Complex(3, 5);
        double r = 5.830951894845301;
        Complex zr = z.plus(r);
        Complex expected = new Complex(0.8701999067534788, 0.49269881498359286);
        assertThat(zr.dividedBy(zr.abs()).abs() - expected.abs(), is(lessThan(EPSILON)));
    }

    @Test
    public void whenComplexDividedByComplexThenResultCorrect() {
        Complex z = new Complex(3, 5);
        Complex w = new Complex(2.4, 3.7);
        Complex expected = new Complex(1.3213367609254498, 0.04627249357326473);
        assertThat(z.dividedBy(w).abs() - expected.abs(), is(lessThan(EPSILON)));
    }

    @SuppressWarnings("EqualsWithItself")
    @Test
    public void whenHashCodeAndEqualsThenValuesCorrect() {
        Complex c1 = new Complex(3, 5);
        Complex c2 = new Complex(2.4, 3.7);
        Complex c3 = new Complex(3, 5);
        assertThat(c1.hashCode(), is(not(c2.hashCode())));
        assertThat(c1.hashCode(), is(c3.hashCode()));
        assertThat(c1.equals(c1), is(true));
        assertThat(c1.equals(c2), is(false));
        assertThat(c1.equals(c3), is(true));
        //noinspection ObjectEqualsNull
        assertThat(c1.equals(null), is(false));
        assertThat(c1.equals(new Object()), is(false));
        assertThat(c1.equals(new Complex(2.0, 5.0)), is(false));
    }

}
