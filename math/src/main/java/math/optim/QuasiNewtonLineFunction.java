/*
 * Copyright (c) 2016 Jacob Rachiele
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
package math.optim;

import math.linear.doubles.Vector;
import math.function.AbstractFunction;
import math.function.AbstractMultivariateFunction;

/**
 * A function for the line search component of a quasi-Newton algorithm.
 *
 * @author Jacob Rachiele
 */
final class QuasiNewtonLineFunction extends AbstractFunction {

    private final AbstractMultivariateFunction f;
    private final Vector x;
    private final Vector p;

    /**
     * Construct a new line function for the quasi-Newton algorithm with the given function,
     * point vector, and search direction.
     *
     * @param f               the function being optimized.
     * @param point           the current input point.
     * @param searchDirection the current search direction.
     */
    QuasiNewtonLineFunction(final AbstractMultivariateFunction f, final Vector point, final Vector searchDirection) {
        this.f = f;
        this.x = point;
        this.p = searchDirection;
    }

    @Override
    public final double at(final double alpha) {
        functionEvaluations++;
        return f.at(x.plus(p.scaledBy(alpha)));
    }

}
