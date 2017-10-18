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

package com.github.signaflo.math.function;

import com.github.signaflo.math.linear.doubles.Vector;
import com.github.signaflo.math.optim.SphereFunction;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

public class GeneralFunctionSpec {

    @Test
    public void whenFunctionEvaluatedCorrectResult() {
        GeneralFunction f = new GeneralFunction((x) -> x * x);
        assertThat(f.at(3.0), is(9.0));
    }

    @Test
    public void whenSlopeAtThenSlopeCorrect() {
        GeneralFunction f = new GeneralFunction((x) -> x * x, (x) -> 2 * x);
        assertThat(f.slopeAt(3.0), is(6.0));
    }

    @Test
    public void whenAbstractFunctionGradientThenCorrectVector() {
        AbstractMultivariateFunction f = new SphereFunction();
        assertArrayEquals(Vector.from(2.0, 4.0, 6.0).elements(), f.gradientAt(Vector.from(1.0, 2.0, 3.0)).elements(),
                          1E-4);
    }

    @Test
    public void whenDfNotSetThenDerivativeApproximated() {
        GeneralFunction f = new GeneralFunction((x) -> x * x);
        assertThat(f.slopeAt(3.0), is(closeTo(6.0, 1E-8)));
    }

    @Test
    public void whenDfSetThenDerivativeExact() {
        GeneralFunction f = new GeneralFunction((x) -> x * x);
        f.setDf((x) -> 2 * x);
        assertThat(f.slopeAt(3.0), is(6.0));
    }
}
