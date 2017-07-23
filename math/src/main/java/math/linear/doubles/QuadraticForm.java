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

@EqualsAndHashCode @ToString
public final class QuadraticForm {

    private final Vector a;
    private final Matrix X;

    public QuadraticForm(Vector a, Matrix X) {
        validateArguments(a, X);
        this.a = a;
        this.X = X;
    }

    public static double multiply(Vector a, Matrix X) {
        int n = a.size();
        double result = 0.0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result += a.at(i) * a.at(j) * X.get(i, j);
            }
        }
        return result;
    }

    private void validateArguments(Vector a, Matrix X) {
        if (!X.isSquare()) {
            throw new IllegalArgumentException("The Matrix must be square.");
        }
        if (a.size() != X.nrow()) {
            throw new IllegalArgumentException("The number of Matrix rows must be the same" +
                                               " as the size of the vector.");
        }
    }

    public double multiply() {
        return multiply(this.a, this.X);
    }
}
