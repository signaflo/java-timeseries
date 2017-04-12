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

import lombok.NonNull;
import math.FieldElement;

import java.util.List;

public final class TimeSeries<T extends FieldElement<T>> implements DataSet<T> {

    private final DataSet<T> dataSet;
    private final List<T> list;
    private final Zero<T> zero;
    private final Cache cache;

    TimeSeries(Builder<T> builder) {
        this.dataSet = builder.dataSet();
        this.zero = builder.zero();
        this.list = this.dataSet.data();
        this.cache = initializeCache();
    }

    private Cache initializeCache() {
        Cache cache = new Cache();
        cache.mean = this.dataSet.mean();
        return cache;
    }

    public T at(int index) {
        return this.list.get(index);
    }

    public T autoCovarianceAtLag(int k) {
        if (k < 0) {
            throw new IllegalArgumentException("The lag, k, must be non-negative, but was " + k);
        }
        final int n = this.size();
        final T mean = this.cache.mean();
        T sumOfProductDeviations = zero.getValue();
        T leftFactor;
        T rightFactor;
        for (int t = 0; t < n - k; t++) {
            leftFactor = list.get(t).minus(mean);
            rightFactor = list.get(t + k).minus(mean);
            sumOfProductDeviations = sumOfProductDeviations.plus(leftFactor.times(rightFactor));
        }
        return sumOfProductDeviations.dividedBy(n);
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
        return this.cache.mean();
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
    public DataSet<T> times(@NonNull DataSet<T> otherData) {
        return dataSet.times(otherData);
    }

    @Override
    public DataSet<T> plus(@NonNull DataSet<T> otherData) {
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
    public T covariance(@NonNull DataSet<T> otherData) {
        return dataSet.covariance(otherData);
    }

    @Override
    public T correlation(@NonNull DataSet<T> otherData) {
        return dataSet.correlation(otherData);
    }

    @Override
    public List<T> data() {
        return dataSet.data();
    }

    private class Cache {
        private T mean;
        T mean() {
            return this.mean;
        }
    }

    public static class Builder<T extends FieldElement<T>> {

        private DataSet<T> dataSet;
        private final Zero<T> zero;

        Builder(@NonNull Zero<T> zero) {
            this.zero = zero;
        }

        Builder(@NonNull DataSet<T> dataSet, @NonNull Zero<T> zero) {
            this.dataSet = dataSet;
            this.zero = zero;
        }

        Builder(@NonNull List<T> data, @NonNull Zero<T> zero) {
            this.dataSet = new NumericalDataSet<T>(data, zero);
            this.zero = zero;
        }

        Builder<T> dataSet(@NonNull DataSet<T> dataSet) {
            this.dataSet = dataSet;
            return this;
        }

        Builder<T> dataSet(@NonNull List<T> data) {
            this.dataSet = new NumericalDataSet<T>(data, zero);
            return this;
        }

        private DataSet<T> dataSet() {
            return this.dataSet;
        }

        private Zero<T> zero() {
            return this.zero;
        }

        TimeSeries<T> build() {
            if (this.dataSet == null) {
                throw new IllegalStateException(
                        "The data underlying the series must be set before its construction."
                );
            }
            return new TimeSeries<>(this);
        }
    }
}
