/*
 * Copyright (c) 2016-2017 Jacob Rachiele
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
package stats;

import java.util.Arrays;

import static data.operations.Operators.productOf;

/**
 * Static methods for computing basic statistics.
 *
 * @author Jacob Rachiele
 */
public final class Statistics {

    private Statistics() {}

    public static double sumOf(final double[] data) {
        double sum = 0.0;
        for (double element : data) {
            sum += element;
        }
        return sum;
    }

    public static double meanOf(final double[] data) {
        final double sum = sumOf(data);
        return sum / data.length;
    }

    public static double varianceOf(final double[] data) {
        final int n = data.length;
        return sumOfSquaredDifferences(data, meanOf(data)) / (n - 1);
    }

    public static double stdDeviationOf(final double[] data) {
        return Math.sqrt(varianceOf(data));
    }

    public static double sumOfSquared(final double[] data) {
        return sumOf(squared(data));
    }

    static double sumOfSquaredDifferences(final double[] data, final double point) {
        return sumOf(squared(differences(data, point)));
    }

    static double[] squared(final double[] data) {
        final double[] squared = new double[data.length];
        for (int i = 0; i < squared.length; i++) {
            squared[i] = data[i] * data[i];
        }
        return squared;
    }

    static double[] differences(final double[] data, final double point) {
        final double[] differenced = new double[data.length];
        for (int i = 0; i < differenced.length; i++) {
            differenced[i] = data[i] - point;
        }
        return differenced;
    }

    public static double covarianceOf(final double[] data, final double[] data2) {
        return sumOf(productOf(differences(data, meanOf(data)), differences(data2, meanOf(data2)))) / (data.length - 1);
    }

    public static double correlationOf(final double[] data, final double[] data2) {
        return covarianceOf(data, data2) / (stdDeviationOf(data) * stdDeviationOf(data2));
    }

    // Arrays.sort uses quicksort algorithm as of Java 8.
    public static double medianOf(final double[] data) {
        double[] sorted = data.clone();
        Arrays.sort(sorted);
        if (sorted.length % 2 == 0) {
            return (sorted[(sorted.length / 2) - 1] + sorted[(sorted.length / 2)]) / 2.0;
        } else {
            return sorted[(sorted.length - 1) / 2];
        }
    }

}
