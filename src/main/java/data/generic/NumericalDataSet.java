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

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import math.Complex;
import math.FieldElement;

import java.util.List;

/**
 * A data set consisting of any type that extends the complex numbers. This class is immutable and thread-safe.
 *
 * @param <T> any type that extends the complex numbers.
 */
@ToString @EqualsAndHashCode
public class NumericalDataSet<T extends FieldElement<T>> implements DataSet<T> {

    private final List<T> data;

    public NumericalDataSet(@NonNull List<T> data) {
        this.data = ImmutableList.copyOf(data);
    }

    private void validateSize(DataSet<Complex> otherData) {
        if (this.size() != otherData.size()) {
            throw new IllegalArgumentException("The two data sets must have the same length.");
        }
    }

    @Override
    public T sum() {
        T sum;
        if (this.data.size() > 0) {
            sum = this.data.get(0);
            for (int i = 1; i < this.data.size(); i++) {
                sum = sum.plus((this.data.get(i)));
            }
        }
        return null;
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
    public DataSet<T> times(DataSet<T> otherData) {
        return null;
    }

    @Override
    public DataSet<T> plus(DataSet<T> otherData) {
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
    public T covariance(DataSet<T> otherData) {
        return null;
    }

    @Override
    public T correlation(DataSet<T> otherData) {
        return null;
    }

    @Override
    public List<T> data() {
        return null;
    }
}
