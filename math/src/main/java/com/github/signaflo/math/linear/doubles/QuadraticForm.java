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

package com.github.signaflo.math.linear.doubles;

import lombok.ToString;
import com.github.signaflo.math.operations.Validate;

/**
 * Represents a quadratic form (see <a target="_blank" href="http://cs229.stanford.edu/section/cs229-linalg.pdf">
 * section 3.11 here</a>). Note that an object of this class may be constructed with any square
 * matrix, not just a symmetric one.
 */
@ToString
public final class QuadraticForm {

    private final Vector x;
    private final Matrix A;

    public QuadraticForm(Vector x, Matrix A) {
        validateArguments(x, A);
        this.x = x;
        this.A = A;
    }

    /**
     * Compute <em>x</em><sup>T</sup><em>A</em><em>x</em> and return the resulting value.
     *
     * @param x the vector component of the quadratic form.
     * @param A the matrix component of the quadratic form.
     * @return the result of <em>x</em><sup>T</sup><em>A</em><em>x</em>.
     */
    public static double multiply(Vector x, Matrix A) {
        validateArguments(x, A);
        int n = x.size();
        double result = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result += x.at(i) * x.at(j) * A.get(i, j);
            }
        }
        return result;
    }

    private static void validateArguments(Vector x, Matrix A) {
        Validate.argumentsNotNull(x.getClass(), x);
        Validate.argumentsNotNull(A.getClass(), A);
        if (!A.isSquare()) {
            throw new IllegalArgumentException("The matrix must be square.");
        }
        if (x.size() != A.nrow()) {
            throw new IllegalArgumentException("The number of matrix columns must be the same" +
                                               " as the size of the vector.");
        }
    }

    /**
     * Compute <em>x</em><sup>T</sup><em>A</em><em>x</em> and return the resulting value.
     *
     * @return the result of <em>x</em><sup>T</sup><em>A</em><em>x</em>.
     */
    public double multiply() {
        return multiply(this.x, this.A);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuadraticForm that = (QuadraticForm) o;

        if (!x.equals(that.x)) return false;
        return A.equals(that.A);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + A.hashCode();
        return result;
    }
}
