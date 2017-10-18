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
package com.github.signaflo.math.function;

import com.github.signaflo.math.linear.doubles.Vector;

/**
 * A partial implementation of a scalar-valued function of several variables.
 */
public abstract class AbstractMultivariateFunction implements MultivariateFunction {

    private static final double gradientTolerance = 1E-3;

    protected int functionEvaluations = 0;
    protected int gradientEvalutations = 0;

    public Vector gradientAt(Vector point) {
        gradientEvalutations++;
        return NumericalDerivatives.centralDifferenceGradient(this, point, gradientTolerance);
    }

    public Vector gradientAt(final Vector point, final double functionValue) {
        gradientEvalutations++;
        return NumericalDerivatives.forwardDifferenceGradient(this, point, gradientTolerance * gradientTolerance,
                                                              functionValue);
    }

    /**
     * The number of times this function has been evaluated.
     *
     * @return the number of times this function has been evaluated.
     */
    public int functionEvaluations() {
        return this.functionEvaluations;
    }

    /**
     * The number of times the gradient has been computed.
     *
     * @return the number of times the gradient has been computed.
     */
    public int gradientEvaluations() {
        return this.gradientEvalutations;
    }

}
