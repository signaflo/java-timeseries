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

/**
 * A partial implementation of a scalar-valued function of one variable.
 */
public abstract class AbstractFunction implements Function {

    protected int functionEvaluations = 0;
    protected int slopeEvaluations = 0;

    /**
     * Compute and return the slope at the given point.
     *
     * @param point an element of the function's domain.
     * @return the slope of the function at the given point.
     */
    public double slopeAt(final double point) {
        slopeEvaluations++;
        return NumericalDerivatives.centralDifferenceApproximation(this, point, 1E-3);
    }

    public double slopeAt(final double point, final double functionValue) {
        slopeEvaluations++;
        return NumericalDerivatives.forwardDifferenceApproximation(this, point, 1E-6, functionValue);
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
     * The number of times the slope has been computed.
     *
     * @return the number of times the slope has been computed.
     */
    public int slopeEvaluations() {
        return this.slopeEvaluations;
    }
}
