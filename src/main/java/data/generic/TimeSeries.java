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

import math.FieldElement;

import java.util.List;

public final class TimeSeries<T extends FieldElement<T>> implements DataSet<T> {

    private final DataSet<T> dataSet;

    TimeSeries(DataSet<T> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public T sum() {
        return dataSet.sum();
    }

    @Override
    public T sumOfSquares() {
        return dataSet.sumOfSquares();
    }

    @Override
    public T mean() {
        return dataSet.mean();
    }

    @Override
    public T median() {
        return dataSet.median();
    }

    @Override
    public int size() {
        return dataSet.size();
    }

    @Override
    public DataSet<T> times(DataSet<T> otherData) {
        return dataSet.times(otherData);
    }

    @Override
    public DataSet<T> plus(DataSet<T> otherData) {
        return dataSet.plus(otherData);
    }

    @Override
    public T variance() {
        return dataSet.variance();
    }

    @Override
    public T stdDeviation() {
        return dataSet.stdDeviation();
    }

    @Override
    public T covariance(DataSet<T> otherData) {
        return dataSet.covariance(otherData);
    }

    @Override
    public T correlation(DataSet<T> otherData) {
        return dataSet.correlation(otherData);
    }

    @Override
    public List<T> data() {
        return dataSet.data();
    }
}
