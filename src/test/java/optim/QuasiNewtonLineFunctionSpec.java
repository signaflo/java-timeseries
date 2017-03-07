/*
 *
 *  * Copyright (c) ${YEAR} Jacob Rachiele
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 *  * and associated documentation files (the "Software"), to deal in the Software without restriction
 *  * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 *  * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 *  * do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all copies or
 *  * substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 *  * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 *  * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *  * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 *  * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *  *
 *  * Contributors:
 *  *
 *  * Jacob Rachiele
 *
 */

package optim;

import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Mar. 07, 2017
 */
public class QuasiNewtonLineFunctionSpec {

    @Test
    public void whenAbstractMultivariateFunctionThenComputedProperly() {
        AbstractMultivariateFunction f = new RosenbrockFunction();
        Vector point = Vector.from(2, 3);
        Vector searchDirection = Vector.from(1, -1);
        QuasiNewtonLineFunction lineFunction = new QuasiNewtonLineFunction(f, point, searchDirection);
        double result = lineFunction.at(0.5);
        assertThat(result, is(f.at(point.plus(searchDirection.scaledBy(0.5)))));
    }
}
