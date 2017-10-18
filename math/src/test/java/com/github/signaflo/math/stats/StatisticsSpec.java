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

package com.github.signaflo.math.stats;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public final class StatisticsSpec {

    public static final double TOL = 1E-4;

    @Test
    public void whenSumComputedThenResultCorrect() {
        double[] data = {3.5, 4.9, 9.1};
        double expected = 17.5;
        double sum = Statistics.sumOf(data);
        MatcherAssert.assertThat(sum, is(equalTo(expected)));
    }

    @Test
    public void whenMeanComputedThenResultCorrect() {
        double[] data = {3.0, 10.5, 1.5};
        double expected = 5.0;
        double mean = Statistics.meanOf(data);
        MatcherAssert.assertThat(mean, is(equalTo(expected)));
    }

    @Test
    public void whenVarianceComputedThenResultCorrect() {
        double[] data = {3.5, 7.0, 11.5};
        double expected = 16.08333;
        double variance = Statistics.varianceOf(data);
        MatcherAssert.assertThat(variance, is(closeTo(expected, TOL)));
    }

    @Test
    public void whenSumOfSquaresComputedThenResultCorrect() {
        double[] data = {3.5, 7.0, 11.5};
        double expected = 193.5;
        double sumOfSquares = Statistics.sumOfSquared(data);
        MatcherAssert.assertThat(sumOfSquares, is(equalTo(expected)));
    }

    @Test
    public void whenDifferencesTakenThenResultCorrect() {
        double[] data = {3.0, 9.0};
        double point = 6.0;
        double[] expected = new double[]{-3.0, 3.0};
        double[] diffs = Statistics.differences(data, point);
        MatcherAssert.assertThat(diffs, is(equalTo(expected)));
    }

    @Test
    public void whenSquaresTakenThenResultCorrect() {
        double[] data = {3.0, 9.0};
        double[] expected = {9.0, 81.0};
        double[] squared = Statistics.squared(data);
        MatcherAssert.assertThat(squared, is(equalTo(expected)));
    }

    @Test
    public void whenSumOfSquaredDifferencesComputedThenResultCorrect() {
        double[] data = {3.0, 9.0};
        double point = 6.0;
        double expected = 18.0;
        double sumOfSquaredDiffs = Statistics.sumOfSquaredDifferences(data, point);
        MatcherAssert.assertThat(sumOfSquaredDiffs, is(equalTo(expected)));
    }

    @Test
    public void whenStdDeviationComputedThenResultCorrect() {
        double[] data = {3.5, 7.0, 11.5};
        double expected = 4.010403;
        double stdDeviation = Statistics.stdDeviationOf(data);
        MatcherAssert.assertThat(stdDeviation, is(closeTo(expected, TOL)));
    }

    @Test
    public void whenCovarianceComputedThenResultCorrect() {
        double[] data = {3.5, 7.0, 11.5};
        double[] data2 = {3.0, 1.4, 10.0};
        double expected = 14.85;
        assertThat(Statistics.covarianceOf(data, data2), is(closeTo(expected, TOL)));
    }

    @Test
    public void whenCorrelationComputedThenResultCorrect() {
        double[] data = {3.5, 7.0, 11.5};
        double[] data2 = {3.0, 1.4, 10.0};
        double expected = 0.80957592; //Julia
        assertThat(Statistics.correlationOf(data, data2), is(closeTo(expected, TOL)));
    }

    @Test
    public void whenMedianComputedThenResultCorrect() {
        double[] data = {11.5, 3.0, 7.0};
        double[] data2 = {3.0, 1.4, 7.0, 9.5};
        double expected = 7.0;
        double expected2 = 5.0;
        assertThat(Statistics.medianOf(data), is(equalTo(expected)));
        assertThat(Statistics.medianOf(data2), is(equalTo(expected2)));
    }

}
