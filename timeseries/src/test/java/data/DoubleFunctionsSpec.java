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

package data;

import math.stats.Statistics;
import org.junit.Test;
import timeseries.TestData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static data.DoubleFunctions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DoubleFunctionsSpec {

    @Test
    public void whenNewArrayCreatedOutputValuesEqualToInputValues() {
        double[] expected = new double[]{3.0, 7.5, 10.0};
        double[] actual = arrayFrom(3.0, 7.5, 10.0);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void whenNewArrayCreatedWithEmptyArgumentListEmptyArrayReturned() {
        double[] expected = new double[]{};
        double[] actual = arrayFrom();
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void whenNewArrayFromTwoThenCombinedIntoOneProperly() {
        double[] expected = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] array1 = {1.0, 2.0};
        double[] array2 = {3.0, 4.0, 5.0};
        assertThat(combine(array1, array2), is(expected));
    }

    @Test
    public void whenNewArrayFromThreeThenCombinedIntoOneProperly() {
        double[] expected = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0};
        double[] array1 = {1.0, 2.0};
        double[] array2 = {3.0, 4.0, 5.0};
        double[] array3 = {6.0, 7.0};
        assertThat(combine(array1, array2, array3), is(expected));
    }

    @Test
    public void whenNewArrayFromIndicesThenCorrectValuesExtracted() {
        double[] original = {2.0, 4.0, 8.0, 16.0, 32.0, 64.0, 128.0};
        int[] indices = {1, 4, 6};
        double[] expected = {4.0, 32.0, 128.0};
        assertThat(arrayFrom(original, indices), is(expected));
    }

    @Test
    public void whenAppendedThenNewArrayCorrect() {
        double[] expected = {1.0, 2.0, 3.0};
        double[] array1 = {1.0, 2.0};
        double value = 3.0;
        assertThat(append(array1, value), is(expected));
    }

    @Test
    public void whenFillMethodThenArrayFilledWithGivenValue() {
        double[] expected = new double[]{1.0, 1.0, 1.0, 1.0, 1.0};
        double[] actual = fill(5, 1.0);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void whenArraySlicedValuesWithinSliceRangeReturned() {
        double[] expected = new double[]{2.0, 4.5, -3.0, 9.2, 2.1};
        double[] data = arrayFrom(1.3, 7.0, 2.0, 4.5, -3.0, 9.2, 2.1, 7.4);
        double[] actual = slice(data, 2, 7);
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    public void whenSqrtMethodThenSqrtOfEachValueReturned() {
        double[] data = arrayFrom(1.0, 4.0, 9.0, 16.0);
        double[] expected = arrayFrom(1.0, 2.0, 3.0, 4.0);
        assertThat(sqrt(data), is(equalTo(expected)));
    }

    @Test
    public void whenDataMeanRemovedThenResultMeanZero() {
        double[] data = TestData.internetTraffic.asArray();
        assertThat(Statistics.meanOf(demean(data)), is(closeTo(0.0, 1E-10)));
    }

    @Test
    public void whenBoxCoxThenTransformationApplied() {
        double[] data = TestData.debitcards.asArray();
        double[] transformed = boxCox(data, 0.5);
        assertThat(Statistics.meanOf(transformed), is(closeTo(244.1229, 1E-4)));
        assertThat(Statistics.stdDeviationOf(transformed), is(closeTo(38.60176, 1E-4)));
    }

    @Test
    public void whenInvBoxCoxThenTransformationUnapplied() {
        double[] transformed = boxCox(TestData.debitcards.asArray(), 0.5);
        double[] original = inverseBoxCox(transformed, 0.5);
        assertThat(Statistics.meanOf(original), is(closeTo(15514.25641, 1E-4)));
        assertThat(Statistics.stdDeviationOf(original), is(closeTo(4688.38717, 1E-4)));
    }

    @Test
    public void whenNegativeOfThenNegativeTaken() {
        double[] data = TestData.debitcards.asArray();
        double[] neg = negativeOf(data);
        assertThat(Statistics.meanOf(neg), is(closeTo(-15514.25641, 1E-4)));
        assertThat(Statistics.stdDeviationOf(neg), is(closeTo(4688.38717, 1E-4)));
    }

    @Test
    public void whenNewArrayFromBoxedDoubleArrayThenCorrectArrayCreated() {
        Double[] boxed = new Double[] {new Double(1.0), new Double(2.0), new Double(3.0)};
        double[] expected = new double[] {1.0, 2.0, 3.0};
        assertThat(DoubleFunctions.arrayFrom(boxed), is(expected));
    }

    @Test
    public void whenRoundThenValueRoundedCorrectly() {
        double value = 3.347;
        double[] values = {value, 5.008};
        assertThat(round(value, 2), is(3.35));
        assertThat(round(values, 2), is(new double[] {3.35, 5.01}));
    }

    @Test
    public void whenTwoDListFromArrayAndViceVersaThanCorrectObjects() {
        List<Double> column1 = Arrays.asList(1.0, 3.0);
        List<Double> column2 = Arrays.asList(2.0, 4.0);
        List<List<Double>> list2d = Arrays.asList(column1, column2);
        double[][] primitive2d = new double[][] {new double[] {1.0, 3.0}, new double[] {2.0, 4.0}};
        assertThat(DoubleFunctions.twoDListFrom(primitive2d), is(list2d));
        assertThat(DoubleFunctions.twoDArrayFrom(list2d), is(primitive2d));
    }

}
