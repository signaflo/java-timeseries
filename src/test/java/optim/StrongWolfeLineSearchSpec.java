/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

import math.function.AbstractFunction;
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
        assertThat(alpha, is(greaterThan(0.0)));
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
        assertThat(alpha, is(greaterThan(0.0)));
        assertThat(Math.abs(f.slopeAt(alpha)), is(lessThanOrEqualTo(c2 * Math.abs(slopeAt0))));
    }

}
