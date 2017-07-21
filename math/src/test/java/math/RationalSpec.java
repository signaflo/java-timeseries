/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class RationalSpec {

    Rational r1 = Rational.from(3, 4);
    Rational r2 = Rational.from(7, 9);
    Rational r3 = Rational.from(4);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenQIsZeroThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        Rational.from(2, 0);
    }

    @Test
    public void whenDivisionByRationalThenResultCorrect() {
        Rational expected = Rational.from(27, 28);
        assertThat(r1.dividedBy(r2), is(expected));
    }

    @Test
    public void whenComparedThenOrderCorrect() {
        assertThat(r1.compareTo(r2), is(lessThan(0)));
        assertThat(r2.compareTo(r1), is(greaterThan(0)));
        r3 = Rational.from(3, 4);
        assertThat(r1.compareTo(r3), is(0));
        r3 = Rational.from(-3, 4);
        assertThat(r1.compareTo(r3), is(greaterThan(0)));
        assertThat(r3.compareTo(r1), is(lessThan(0)));
        exception.expect(NullPointerException.class);
        r1.compareTo(null);
    }

    @Test
    public void whenConjugateThenSelf() {
        assertThat(r1.conjugate(), is(r1));
    }

    @Test
    public void whenMinusThenResultCorrect() {
        assertThat(r2.minus(r1), is(Rational.from(1, 36)));
    }

    @Test
    public void whenTimesThenResultCorrect() {
        assertThat(r2.times(r1), is(Rational.from(7, 12)));
    }

    @Test
    public void whenSqrtTopNotRationalThenIllegalState() {
        exception.expect(IllegalStateException.class);
        r1.sqrt();
    }

    @Test
    public void whenSqrtBottomNotRationalThenIllegalState() {
        r1 = Rational.from(4, 3);
        exception.expect(IllegalStateException.class);
        r1.sqrt();
    }

    @Test
    public void whenSqrtThenResultCorrect() {
        r1 = Rational.from(9, 16);
        assertThat(r1.sqrt(), is(Rational.from(3, 4)));
    }

    @Test
    public void whenAbsThenResultCorrect() {
        r2 = Rational.from(-3, 4);
        assertThat(r2.abs(), is(3.0 / 4.0));
    }

    @Test
    public void whenAdditiveInverseThenSumIsZero() {
        assertThat(r1.plus(r1.additiveInverse()), is(Rational.from(0)));
    }

    @Test
    public void testHashCodeAndEquals() {
        Rational r4 = Rational.from(36, 48);
        Real real = Real.from(3.0/4.0);
        assertThat(r1.equals(r4), is(true));
        assertThat(r1.hashCode(), is(r4.hashCode()));
        assertThat(r1.hashCode(), is(not(r2.hashCode())));
        assertThat(r1.equals(real), is(false));
        assertThat(r4.hashCode(), is(not(real.hashCode())));
    }
}
