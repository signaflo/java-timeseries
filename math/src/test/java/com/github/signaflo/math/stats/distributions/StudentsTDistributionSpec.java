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

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StudentsTDistributionSpec {

    @Test
    public void whenNormalQuantileComputedResultCorrect() {
        Distribution st = new StudentsT(5);
        assertThat(st.quantile(0.975), is(closeTo(2.570582, 1E-2)));
        assertThat(st.quantile(0.025), is(closeTo(-2.570582, 1E-2)));
    }

    @Test
    public void whenNormalRandGeneratedDoubleReturned() {
        Distribution st = new StudentsT(5);
        assertThat(st.rand(), isA(Double.class));
    }

    @Test
    public void testEqualsAndHashCode() {
        Distribution t = new StudentsT(5);
        Distribution t2 = new StudentsT(20);
        Distribution t3 = new StudentsT(5);
        assertThat(t, is(t));
        assertThat(t.hashCode(), is(t3.hashCode()));
        assertThat(t, is(t3));
        assertThat(t, is(not(t2)));
        assertThat(t2, is(not(t)));
        assertThat(t, is(not(nullValue())));
        assertThat(t, is(not(new Object())));
    }
}
