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

package com.github.signaflo.math.optim;

import com.github.signaflo.math.function.AbstractFunction;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public final class StrongWolfeLineSearchSpec {

    @Test
    public void whenSearchThenFirstWolfeConditionSatisfied() {
        AbstractFunction f = new TestFunction1();
        final double fAt0 = f.at(0);
        final double slopeAt0 = f.slopeAt(0);
        final double c1 = 1E-3;
        final double c2 = 0.1;
        StrongWolfeLineSearch lineSearch = StrongWolfeLineSearch.newBuilder(f, fAt0, slopeAt0)
                                                                .c1(c1).c2(c2)
                                                                .build();
        double alpha = lineSearch.search();
        MatcherAssert.assertThat(alpha, is(greaterThan(0.0)));
        assertThat(f.at(alpha), is(lessThanOrEqualTo(fAt0 + c1 * slopeAt0 * alpha)));
    }

    @Test
    public void whenSearchThenSecondWolfeConditionSatisfied() {
        AbstractFunction f = new TestFunction1();
        final double fAt0 = f.at(0);
        final double slopeAt0 = f.slopeAt(0);
        final double c1 = 1E-3;
        final double c2 = 0.1;
        StrongWolfeLineSearch lineSearch = StrongWolfeLineSearch.newBuilder(f, fAt0, slopeAt0)
                                                                .c1(c1).c2(c2)
                                                                .build();
        double alpha = lineSearch.search();
        MatcherAssert.assertThat(alpha, is(greaterThan(0.0)));
        assertThat(Math.abs(f.slopeAt(alpha)), is(lessThanOrEqualTo(c2 * Math.abs(slopeAt0))));
    }

}
