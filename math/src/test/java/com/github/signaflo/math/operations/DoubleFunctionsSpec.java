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

package com.github.signaflo.math.operations;

import com.github.signaflo.math.stats.Statistics;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.github.signaflo.math.operations.DoubleFunctions.*;
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
    public void whenCombineTwoDArraysThenCorrectTwoDArrayReturned() {
        double[][] twoDArray1 = {{1.0, 3.0}, {2.0, 4.0}};
        double[][] twoDArray2 = {{5.0, 7.0}, {6.0, 8.0}, {0.0, 9.0}};
        double[][] expected = {{1.0, 3.0}, {2.0, 4.0}, {5.0, 7.0}, {6.0, 8.0}, {0.0, 9.0}};
        double[][] combined = combine(twoDArray1, twoDArray2);
        assertThat(combined, is(expected));
    }

    @Test
    public void whenNewArrayFromIndicesThenCorrectValuesExtracted() {
        double[] original = {2.0, 4.0, 8.0, 16.0, 32.0, 64.0, 128.0};
        int[] indices = {1, 4, 6};
        double[] expected = {4.0, 32.0, 128.0};
        assertThat(DoubleFunctions.arrayFrom(original, indices), is(expected));
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
    public void whenListFromArrayThenCorrectList() {
        double[] data = arrayFrom(1.0, 4.0, 9.0, 16.0);
        List<Double> expected = Arrays.asList(1.0, 4.0, 9.0, 16.0);
        assertThat(DoubleFunctions.listFrom(data), is(expected));
    }

    @Test
    public void whenDataMeanRemovedThenResultMeanZero() {
        double[] data = debitcardData;
        assertThat(Statistics.meanOf(demean(data)), is(closeTo(0.0, 1E-10)));
    }

    @Test
    public void whenBoxCoxThenTransformationApplied() {
        double[] data = debitcardData;
        double[] transformed = boxCox(data, 0.5);
        assertThat(Statistics.meanOf(transformed), is(closeTo(244.1229, 1E-4)));
        assertThat(Statistics.stdDeviationOf(transformed), is(closeTo(38.60176, 1E-4)));
    }

    @Test
    public void whenInvBoxCoxThenTransformationUnapplied() {
        double[] transformed = boxCox(debitcardData, 0.5);
        double[] original = inverseBoxCox(transformed, 0.5);
        assertThat(Statistics.meanOf(original), is(closeTo(15514.25641, 1E-4)));
        assertThat(Statistics.stdDeviationOf(original), is(closeTo(4688.38717, 1E-4)));
    }

    @Test
    public void whenNegativeOfThenNegativeTaken() {
        double[] data = debitcardData;
        double[] neg = negativeOf(data);
        assertThat(Statistics.meanOf(neg), is(closeTo(-15514.25641, 1E-4)));
        assertThat(Statistics.stdDeviationOf(neg), is(closeTo(4688.38717, 1E-4)));
    }

    @Test
    public void whenNewArrayFromBoxedDoubleArrayThenCorrectArrayCreated() {
        Double[] boxed = new Double[] {1.0, 2.0, 3.0};
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

    @Test
    public void whenPushToArrayThenNewValueAtFront() {
        double[] data = {2.0, 3.0};
        double[] expected = {1.0, 2.0, 3.0};
        assertThat(DoubleFunctions.push(1.0, data), is(expected));
    }

    @Test
    public void whenPushTo2DArrayThenNewArrayAtFront() {
        double[][] primitive2d = {new double[] {1.0, 3.0}, new double[] {2.0, 4.0}};
        double[] newArray = {5.0, 7.0};
        double[][] expected = {newArray, primitive2d[0], primitive2d[1]};
        assertThat(DoubleFunctions.push(newArray, primitive2d), is(expected));

    }

    private double[] debitcardData = {7204, 7335, 7812, 7413, 9136, 8725, 8751,
            9609, 8601, 8930, 8835, 11688, 8078, 7892,
            8151, 8738, 9416, 9533, 9943, 10859, 8789,
            9960, 9619, 12900, 8620, 8401, 8546, 10004,
            10675, 10115, 11206, 11555, 10453, 10421, 9950,
            13975, 9315, 9366, 9910, 10302, 11371, 11857,
            12387, 12421, 12073, 11963, 10666, 15613,
            10586, 10558, 12064, 11899, 12077, 13918,
            13611, 14132, 13509, 13152, 13993, 18203,
            14262, 13024, 14062, 14718, 16544, 16732,
            16230, 18126, 16016, 15601, 15394, 20439,
            14991, 14908, 17459, 14501, 18271, 17963,
            17026, 18111, 15989, 16735, 15949, 20216,
            16198, 15060, 16168, 16376, 18403, 19113,
            19303, 20560, 16621, 18788, 17970, 22464,
            16658, 16214, 16043, 16418, 17644, 17705,
            18107, 17975, 17598, 17658, 15750, 22414,
            16065, 15467, 16297, 16530, 18410, 20274,
            21311, 20991, 18305, 17832, 18223, 23987,
            15964, 16606, 19216, 16419, 19638, 19773,
            21052, 22011, 19039, 17893, 19276, 25167,
            16699, 16619, 17851, 18160, 22032, 21395,
            22217, 24565, 21095, 20114, 19931, 26120,
            18580, 18492, 19724, 20123, 22582, 22595,
            23379, 24920, 20325, 22038, 20988, 26675};
}
