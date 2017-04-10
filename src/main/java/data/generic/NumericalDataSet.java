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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ToString @EqualsAndHashCode
public class NumericalDataSet<T extends Complex> implements DataSet<Complex> {

    private final List<T> data;

    NumericalDataSet(List<T> data) {
        this.data = ImmutableList.copyOf(data);
    }


    @Override
    public Complex sum() {
        Complex sum = new Complex();
        for (T t : data) {
            sum = sum.plus(t);
        }
        return sum;
    }

    @Override
    public Complex sumOfSquares() {
        Complex sum = new Complex();
        for (T t : data) {
            sum = sum.plus(t.times(t));
        }
        return sum;
    }

    @Override
    public Complex mean() {
        return sum().dividedBy(this.size());
    }

    @Override
    public Complex median() {
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
    public DataSet<Complex> times(@NonNull DataSet<Complex> otherData) {
        validateSize(otherData);
        List<Complex> thisDataSet = this.data();
        List<Complex> otherDataSet = otherData.data();
        ImmutableList.Builder<Complex> newDataSet = ImmutableList.builder();
        for (int i = 0; i < this.size(); i++) {
            newDataSet.add(thisDataSet.get(i).times(otherDataSet.get(i)));
        }
        return new NumericalDataSet<>(newDataSet.build());
    }

    @Override
    public DataSet<Complex> plus(@NonNull DataSet<Complex> otherData) {
        validateSize(otherData);
        List<Complex> thisDataSet = this.data();
        List<Complex> otherDataSet = otherData.data();
        ImmutableList.Builder<Complex> newDataSet = ImmutableList.builder();
        for (int i = 0; i < this.size(); i++) {
            newDataSet.add(thisDataSet.get(i).plus(otherDataSet.get(i)));
        }
        return new NumericalDataSet<>(newDataSet.build());
    }

    @Override
    public Complex variance() {
        Complex mean = this.mean();
        Complex difference;
        Complex squaredDifference;
        Complex sumOfSquaredDifferences = new Complex();
        for (Complex c : this.data) {
            difference = c.minus(mean);
            squaredDifference = difference.times(difference.conjugate());
            sumOfSquaredDifferences = sumOfSquaredDifferences.plus(squaredDifference);
        }
        return sumOfSquaredDifferences.dividedBy(this.size() - 1);
    }

    @Override
    public Complex stdDeviation() {
        return this.variance().sqrt();
    }

    @Override
    public Complex covariance(@NonNull DataSet<Complex> otherData) {
        return null;
    }

    @Override
    public Complex correlation(@NonNull DataSet<Complex> otherData) {
        return null;
    }

    @Override
    public List<Complex> data() {
        return ImmutableList.copyOf(this.data);
    }

    private void validateSize(DataSet<Complex> otherData) {
        if (this.size() != otherData.size()) {
            throw new IllegalArgumentException("The two data sets must have the same length.");
        }
    }
}
