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
import math.Real;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class DataSets {

    private DataSets(){}

    public static DataSet<Complex> dataSetFrom(List<Complex> data) {
        Zero<Complex> zero = Zero.instance(Complex.zero());
        return new NumericalDataSet<>(data, zero);
    }

    public static TimeSeries<Complex> complexSeriesFrom(List<Complex> data) {
        Zero<Complex> zero = Zero.instance(Complex.zero());
        return new TimeSeries.Builder<>(data, zero).build();
    }

    public static TimeSeries<Real> realSeriesFrom(List<Real> data) {
        Zero<Real> zero = Zero.instance(Real.zero());
        return new TimeSeries.Builder<>(data, zero).build();
    }

    public static TimeSeries<Real> realSeriesFrom(double... data) {
        Zero<Real> zero = Zero.instance(Real.zero());
        List<Real> reals = new ArrayList<>(data.length);
        for (double d : data) {
            reals.add(Real.from(d));
        }
        return new TimeSeries.Builder<>(reals, zero).build();
    }

    public static TimeSeries<Complex> timeSeriesFrom(DataSet<Complex> dataSet) {
        Zero<Complex> zero = Zero.instance(Complex.zero());
        return new TimeSeries.Builder<>(dataSet, zero).build();
    }

    public static TimeSeries.Builder<Complex> timeSeriesBuilder() {
        Zero<Complex> zero = Zero.instance(Complex.zero());
        return new TimeSeries.Builder<>(zero);
    }

    public static TimeSeries.Builder<Complex> timeSeriesBuilder(List<Complex> data) {
        Zero<Complex> zero = Zero.instance(Complex.zero());
        return new TimeSeries.Builder<>(data, zero);
    }

    public static TimeSeries.Builder<Complex> timeSeriesBuilder(DataSet<Complex> dataSet) {
        Zero<Complex> zero = Zero.instance(Complex.zero());
        return new TimeSeries.Builder<>(dataSet, zero);
    }
}
