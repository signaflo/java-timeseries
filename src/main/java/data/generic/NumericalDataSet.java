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
import math.FieldElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A data set consisting of any type that extends the T numbers. This class is immutable and thread-safe.
 *
 * @param <T> any type that extends the T numbers.
 */
@ToString @EqualsAndHashCode
public class NumericalDataSet<T extends FieldElement<T>> implements DataSet<T> {

    private final List<T> data;
    private final Zero<T> zero;

    NumericalDataSet(List<T> data, Zero<T> zero) {
        this.zero = zero;
        this.data = ImmutableList.copyOf(data);
    }


    @Override
    public T sum() {
        T sum = zero.getValue();
        for (T t : data) {
            sum = sum.plus(t);
        }
        return sum;
    }

    @Override
    public T sumOfSquares() {
        T sum = zero.getValue();
        for (T t : data) {
            sum = sum.plus(t.times(t));
        }
        return sum;
    }

    @Override
    public T mean() {
        return sum().dividedBy(this.size());
    }

    @Override
    public T median() {
        List<T> sorted = new ArrayList<>(data);
        Collections.sort(sorted);
        if (sorted.size() % 2 == 0) {
            return (sorted.get(data.size() / 2 - 1).plus(sorted.get(data.size() / 2)).dividedBy(2.0));
        }
        return sorted.get((data.size() - 1) / 2);
    }

    @Override
    public int size() {
        return this.data.size();
    }

    @Override
    public DataSet<T> times(@NonNull DataSet<T> otherData) {
        validateSize(otherData);
        List<T> thisDataSet = this.data();
        List<T> otherDataSet = otherData.data();
        ImmutableList.Builder<T> newDataSet = ImmutableList.builder();
        for (int i = 0; i < this.size(); i++) {
            newDataSet.add(thisDataSet.get(i).times(otherDataSet.get(i)));
        }
        return new NumericalDataSet<>(newDataSet.build(), this.zero);
    }

    @Override
    public DataSet<T> plus(@NonNull DataSet<T> otherData) {
        validateSize(otherData);
        List<T> thisDataSet = this.data();
        List<T> otherDataSet = otherData.data();
        ImmutableList.Builder<T> newDataSet = ImmutableList.builder();
        for (int i = 0; i < this.size(); i++) {
            newDataSet.add(thisDataSet.get(i).plus(otherDataSet.get(i)));
        }
        return new NumericalDataSet<>(newDataSet.build(), this.zero);
    }

    @Override
    public T variance() {
        T mean = this.mean();
        T difference;
        T squaredDifference;
        T sumOfSquaredDifferences = this.zero.getValue();
        for (T c : this.data) {
            difference = c.minus(mean);
            squaredDifference = difference.times(difference.conjugate());
            sumOfSquaredDifferences = sumOfSquaredDifferences.plus(squaredDifference);
        }
        return sumOfSquaredDifferences.dividedBy(this.size() - 1);
    }

    @Override
    public T stdDeviation() {
        return this.variance().sqrt();
    }

    @Override
    public T covariance(@NonNull DataSet<T> otherData) {
        validateSize(otherData);
        T thisMean = this.mean();
        T otherMean = otherData.mean();
        T thisDifference, otherDifference;
        T product;
        T sum = this.zero.getValue();

        T c, d;
        for (int i = 0; i < this.size(); i++) {
            c = this.data().get(i);
            d = otherData.data().get(i);
            thisDifference = c.minus(thisMean);
            otherDifference = d.minus(otherMean);
            product = thisDifference.conjugate().times(otherDifference);
            sum = sum.plus(product);
        }
        return sum.dividedBy(this.size() - 1);
    }

    @Override
    public T correlation(@NonNull DataSet<T> otherData) {
        return this.covariance(otherData).dividedBy(this.stdDeviation().times(otherData.stdDeviation()).abs());
    }

    @Override
    public List<T> data() {
        return ImmutableList.copyOf(this.data);
    }

    private void validateSize(DataSet<T> otherData) {
        if (this.size() != otherData.size()) {
            throw new IllegalArgumentException("The two data sets must have the same length.");
        }
    }
}
