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
package math.linear;

import math.Complex;

import java.util.ArrayList;
import java.util.List;

/**
 * Static methods for creating complex-valued vectors.
 */
final class ComplexVectors {

    private ComplexVectors() {
    }

    /**
     * Produce a math.linear combination from the given vectors and scalars.
     *
     * @param vectors the vector components of the math.linear combination.
     * @param scalars the scalar components of the math.linear combination.
     * @return a math.linear combination of the given vectors and scalars.
     */
    static FieldVector<Complex> linearCombination(List<FieldVector<Complex>> vectors, List<Complex> scalars) {
        FieldVector<Complex> result = zeroVector(scalars.size());
        for (int i = 0; i < vectors.size(); i++) {
            result = result.plus(vectors.get(i).scaledBy(scalars.get(i)));
        }
        return result;
    }

    /**
     * Scale vector x by &alpha; then add y and return the result in a new vector.
     *
     * @param x     the vector to be scaled.
     * @param y     the vector to be added to scaled x.
     * @param alpha the scalar to scale x by.
     * @return x scaled by &alpha; then added to y.
     */
    static FieldVector<Complex> axpy(final FieldVector<Complex> x, final FieldVector<Complex> y, final Complex alpha) {
        return (x.scaledBy(alpha).plus(y));
    }

    /**
     * Create a new vector filled with zeros.
     *
     * @param size the size of the new vector.
     * @return a new vector filled with zeros.
     */
    static FieldVector<Complex> zeroVector(final int size) {
        List<Complex> zeros = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            zeros.add(new Complex(0, 0));
        }
        return new FieldVector<>(zeros);
    }

}
