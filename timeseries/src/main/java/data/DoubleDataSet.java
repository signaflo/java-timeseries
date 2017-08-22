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
package data;

import lombok.NonNull;
import math.operations.Operators;

import math.stats.Statistics;

import java.text.DecimalFormat;
import java.util.Arrays;

/**
 * A collection of numerical observations represented as primitive doubles. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class DoubleDataSet implements DataSet {

    private final double[] data;

    /**
     * Construct a new data set from the given data.
     *
     * @param data the collection of observations.
     */
    public DoubleDataSet(@NonNull final double... data) {
        this.data = data.clone();
    }

    @Override
    public final double sum() {
        return Statistics.sumOf(this.data);
    }

    @Override
    public final double sumOfSquares() {
        return Statistics.sumOfSquared(this.data);
    }

    @Override
    public final double mean() {
        return Statistics.meanOf(this.data);
    }

    @Override
    public final double median() {
        return Statistics.medianOf(this.data);
    }

    @Override
    public final int size() {
        return this.data.length;
    }

    @Override
    public final DataSet times(@NonNull final DataSet otherData) {
        return new DoubleDataSet(Operators.productOf(this.data, otherData.asArray()));
    }

    @Override
    public final DataSet plus(@NonNull final DataSet otherData) {
        return new DoubleDataSet(Operators.sumOf(this.data, otherData.asArray()));
    }

    @Override
    public final double variance() {
        return Statistics.varianceOf(this.data);
    }

    @Override
    public final double stdDeviation() {
        return Statistics.stdDeviationOf(this.data);
    }

    @Override
    public final double covariance(@NonNull final DataSet otherData) {
        return Statistics.covarianceOf(this.data, otherData.asArray());
    }

    @Override
    public final double correlation(@NonNull final DataSet otherData) {
        return Statistics.correlationOf(this.data, otherData.asArray());
    }

    @Override
    public final double[] asArray() {
        return this.data.clone();
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.##");
        return "\nValues: " + Arrays.toString(data) + "\nLength: " + data.length + "\nMean: " + mean() +
               "\nStandard deviation: " + df.format(stdDeviation());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DoubleDataSet other = (DoubleDataSet) obj;
        return Arrays.equals(data, other.data);
    }

}
