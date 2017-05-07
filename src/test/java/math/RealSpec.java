/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package math;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RealSpec {

    private static final double EPSILON = Math.ulp(1.0);

    private Real a;
    private Real b;

    @Before
    public void beforeMethod() {
        a = Real.from(3.0);
        b = Real.from(4.5);
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenRealAddedThenCorrectRealReturned() {
        assertThat(a.plus(b), is(Real.from(7.5)));
    }

    @Test
    public void whenRealSubtractedThenCorrectRealReturned() {
        assertThat(a.minus(b), is(Real.from(-1.5)));
    }

    @Test
    public void whenRealSqrtThenRightComplexReturned() {
        assertThat(a.sqrt(), is(Real.from(Math.sqrt(3.0))));
        a = Real.from(-3.0);
        assertThat(a.complexSqrt(), is(new Complex(0.0, Math.sqrt(3.0))));
    }

    @Test
    public void whenRealValuesComparedThenOrderingCorrect() {
        assertThat(a.compareTo(b), is(lessThan(0)));
        assertThat(b.compareTo(a), is(greaterThan(0)));
        Real c = Real.from(3.0);
        assertThat(a.compareTo(c), is(0));
        c = Real.from(-3.0);
        assertThat(a.compareTo(c), is(greaterThan(0)));
        b = Real.from(-4.0);
        assertThat(c.compareTo(b), is(greaterThan(0)));
        assertThat(b.compareTo(c), is(lessThan(0)));
        exception.expect(NullPointerException.class);
        a.compareTo(null);
    }

    @Test
    public void whenRealAbsThenAbsoluteValueReturned() {
        Real c = Real.from(-3.0);
        assertThat(c.abs(), is(3.0));
    }

    @Test
    public void whenDividedByZeroThenArithmeticException() {
        exception.expect(ArithmeticException.class);
        a.dividedBy(0);
    }

    @Test
    public void whenZeroConstructorThenZeroCreated() {
        a = Real.zero();
        assertThat(a.value(), is(0.0));
    }

    @Test
    public void whenDividedByIntThenResultCorrect() {
        assertThat(a.dividedBy(2).value(), is(1.5));
    }

    @Test
    public void whenConjugateThenSelf() {
        assertThat(a.conjugate(), is(a));
    }

    @Test
    public void whenNegativeSqrtThenIllegalStateException() {
        Real c = Real.from(-3.0);
        exception.expect(IllegalStateException.class);
        c.sqrt();
    }

    @Test
    public void whenAdditiveInverseThenRightNumberReturned() {
        assertThat(a.additiveInverse(), is(Real.from(-3.0)));
    }

    @Test
    public void whenRealNumberSquaredResultCorrect() {
        assertThat(a.squared(), is(Real.from(9.0)));
    }

    @Test
    public void whenRealNumberCubedResultCorrect() {
        assertThat(a.cubed(), is(Real.from(27.0)));
    }

    @Test
    public void whenRealNumberDividedResultCorrect() {
        assertThat(a.dividedBy(Real.from(2.0)), is(Real.from(1.5)));
    }

    @Test
    public void whenRealIntervalThenLowerAndUpperReturned() {
        Real.Interval interval = new Real.Interval(3.0, 10.0);
        assertThat(interval.lower(), is(Real.from(3.0)));
        assertThat(interval.upper(), is(Real.from(10.0)));
        assertThat(interval.lowerDbl(), is(3.0));
        assertThat(interval.upperDbl(), is(10.0));
    }

    @Test
    public void whenIntervalEndpointsEqualThenTrue() {
        Real.Interval interval = new Real.Interval(1E-30, 1E-30);
        assertThat(interval.endpointsEqual(), is(true));
        assertThat(interval.endpointsEqual(EPSILON), is(true));
    }

    @Test
    public void whenIntervalEndpointsEqualWithinToleranceThenTrue() {
        Real.Interval interval = new Real.Interval(1E-53, 1E-54);
        assertThat(interval.endpointsEqual(), is(false));
        assertThat(interval.endpointsEqual(EPSILON), is(true));
    }

    @Test
    public void whenIntervalEndpointsNotEqualThenFalse() {
        Real.Interval interval = new Real.Interval(1.0, 1.0 + 1E-14);
        assertThat(interval.endpointsEqual(), is(false));
        assertThat(interval.endpointsEqual(EPSILON), is(false));
    }

    @Test
    public void whenRealIntervalContainsNumberThenTrue() {
        Real.Interval interval = new Real.Interval(3.0, 10.0);
        assertThat(interval.contains(9.99999999), is(true));
        assertThat(interval.contains(10.0000001), is(false));
        assertThat(interval.contains(2.9999999), is(false));
        interval = new Real.Interval(Real.from(10.0), Real.from(3.0));
        assertThat(interval.contains(9.99999999), is(true));
        assertThat(interval.contains(10.0000001), is(false));
        assertThat(interval.contains(2.99999999), is(false));
    }

    @Test
    public void whenRealIntervalDoesntContainNumberThenFalse() {
        Real.Interval interval = new Real.Interval(3.0, 10.0);
        assertThat(interval.doesntContain(9.99999999), is(false));
        assertThat(interval.doesntContain(10.0000001), is(true));
        interval = new Real.Interval(Real.from(10.0), Real.from(3.0));
        assertThat(interval.doesntContain(9.99999999), is(false));
        assertThat(interval.doesntContain(10.0000001), is(true));
    }

    @Test
    public void testEqualsAndHashCode() {
        Real c = Real.from(3.0);
        //noinspection ObjectEqualsNull
        assertThat(a.equals(null), is(false));
        assertThat(a, is(not((b))));
        assertThat(a, is(a));
        assertThat(a, is(c));
        assertThat(a.hashCode(), is(c.hashCode()));
    }

    @Test
    public void testIntervalEqualsAndHashCode() {
        Real.Interval a = new Real.Interval(3.0, 5.0);
        Real.Interval b = new Real.Interval(5.0, 3.0);
        Real.Interval c = new Real.Interval(3.0, 5.0);
        //noinspection ObjectEqualsNull
        assertThat(a.equals(null), is(false));
        assertThat(a, is(not(new Object())));
        assertThat(a, is(not((b))));
        assertThat(a, is(a));
        assertThat(a, is(c));
        assertThat(a.hashCode(), is(c.hashCode()));
    }
}
