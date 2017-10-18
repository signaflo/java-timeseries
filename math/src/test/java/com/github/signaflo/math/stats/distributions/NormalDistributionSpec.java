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

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NormalDistributionSpec {

    @Test
    public void whenNormalQuantileComputedResultCorrect() {
        Distribution norm = new Normal(0, 5);
        assertThat(norm.quantile(0.975), is(closeTo(1.96 * 5, 1E-2)));
        assertThat(norm.quantile(0.025), is(closeTo(-1.96 * 5, 1E-2)));
    }

    @Test
    public void whenNormalRandGeneratedDoubleReturned() {
        Distribution norm = new Normal(0, 2.5);
        assertThat(norm.rand(), isA(Double.class));
    }

    @Test
    public void testEqualsAndHashCode() {
        Distribution norm = new Normal(0, 5);
        Distribution norm2 = new Normal(0, 2.5);
        Distribution norm3 = new Normal(0, 5);
        Distribution norm4 = new Normal(1, 5);
        assertThat(norm, is(norm));
        MatcherAssert.assertThat(norm, is(not(norm2)));
        MatcherAssert.assertThat(norm2, is(not(norm)));
        assertThat(norm.hashCode(), is(not(norm2.hashCode())));
        assertThat(norm.hashCode(), is(norm3.hashCode()));
        assertThat(norm, is(norm3));
        MatcherAssert.assertThat(norm4, is(not(norm)));
        norm4 = null;
        MatcherAssert.assertThat(norm, is(not(norm4)));
        String string = "";
        MatcherAssert.assertThat(norm, is(not(string)));
    }

}
