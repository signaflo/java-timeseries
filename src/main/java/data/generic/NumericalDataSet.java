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

package data.generic;

import math.Complex;
import math.FieldElement;

import java.util.Collections;
import java.util.List;

public class NumericalDataSet<T extends Complex> implements DataSet<Complex> {

    private final List<T> data;

    NumericalDataSet(List<T> data) {
        this.data = Collections.unmodifiableList(data);
    }

    @Override
    public FieldElement<Complex> sum() {
        Complex sum = Complex.zero();
        for (Complex complex : data) {
            sum = sum.plus(complex);
        }
        return sum;
    }

    @Override
    public T sumOfSquares() {
        return null;
    }

    @Override
    public T mean() {
        return null;
    }

    @Override
    public T median() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public DataSet<Complex> times(DataSet<Complex> otherData) {
        return null;
    }

    @Override
    public DataSet<Complex> plus(DataSet<Complex> otherData) {
        return null;
    }

    @Override
    public T variance() {
        return null;
    }

    @Override
    public T stdDeviation() {
        return null;
    }

    @Override
    public T covariance(DataSet otherData) {
        return null;
    }

    @Override
    public T correlation(DataSet otherData) {
        return null;
    }

    @Override
    public List<Complex> data() {
        return Collections.unmodifiableList(this.data);
    }
}
