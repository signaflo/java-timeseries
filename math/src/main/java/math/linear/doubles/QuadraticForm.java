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

package math.linear.doubles;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a quadratic form (see <a target="_blank" href="http://cs229.stanford.edu/section/cs229-linalg.pdf">
 * section 3.11 here</a>). Note that an object of this class may be constructed with any square
 * matrix, not just a symmetric one.
 */
@EqualsAndHashCode @ToString
public final class QuadraticForm {

    private final Vector x;
    private final Matrix A;

    public QuadraticForm(Vector x, Matrix A) {
        validateArguments(x, A);
        this.x = x;
        this.A = A;
    }

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
        if (!A.isSquare()) {
            throw new IllegalArgumentException("The matrix must be square.");
        }
        if (x.size() != A.nrow()) {
            throw new IllegalArgumentException("The number of matrix rows must be the same" +
                                               " as the size of the vector.");
        }
    }

    public double multiply() {
        return multiply(this.x, this.A);
    }
}
