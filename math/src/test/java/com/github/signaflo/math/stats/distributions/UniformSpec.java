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

package com.github.signaflo.math.stats.distributions;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UniformSpec {

    private static final double EPSILON = Math.ulp(1.0);
    private double a = -2.5;
    private double b = 3.0;
    private Uniform uniform = new Uniform(a, b);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenRandomThenNumberBetweenMinAndMax() {
        a = 2.9;
        uniform = new Uniform(a, b);
        assertThat(uniform.rand(), is(lessThan(b + EPSILON)));
        assertThat(uniform.rand(), is(greaterThan(a - EPSILON)));
    }

    @Test
    public void whenQuantileThenCorrectValueReturned() {
        assertThat(uniform.quantile(0.25), is(-1.125));
    }

    @Test
    public void whenQuantileAtZeroThenMinReturned() {
        assertThat(uniform.quantile(0.0), is(a));
    }

    @Test
    public void whenQuantileAtOneThenMaxReturned() {
        assertThat(uniform.quantile(1.0), is(b));
    }

    @Test
    public void whenQuantileWithProbLessThanZeroThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        uniform.quantile(-0.1);
    }

    @Test
    public void whenQuantileWithProbGreaterThanOneThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        uniform.quantile(1.01);
    }

    @Test
    public void testEqualsAndHashCode() {
        Uniform uniform2 = new Uniform(a, b);
        Uniform uniform3 = new Uniform(-a, b);
        Uniform uniform4 = new Uniform(a, 2 * b);
        assertThat(uniform, is(uniform));
        assertThat(uniform, is(uniform2));
        assertThat(uniform.hashCode(), is(uniform2.hashCode()));
        assertThat(uniform2, is(not(uniform3)));
        assertThat(uniform2, is(not(uniform4)));
        assertThat(uniform2, is(not(nullValue())));
        assertThat(uniform2, is(not(new Object())));

    }
}
