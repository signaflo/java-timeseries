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

package timeseries.models.regression.primitive;

import com.google.common.testing.EqualsTester;
import data.DoubleFunctions;
import data.Range;
import math.operations.Operators;
import timeseries.TestData;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertArrayEquals;

public class LinearRegressionSpec {

    private double[] time = Range.inclusiveRange(1, 47, 1.0).asArray();
    private double[] response = TestData.livestock.asArray();
    private boolean hasIntercept = true;
    private MultipleLinearRegression regression = MultipleLinearRegression.builder()
                                                                          .predictors(time)
                                                                          .response(response)
                                                                          .hasIntercept(hasIntercept)
                                                                          .build();

    @Test
    public void whenBuiltThenDataProperlySet() {
        assertThat(regression.hasIntercept(), is(hasIntercept));
        assertThat(regression.response(), is(response));
        assertThat(regression.predictors()[0], is(time));
        assertThat(regression.beta(), is(not(nullValue())));
    }

    @Test
    public void whenSimpleRegressionThenBetaEstimatedCorrectly() {
        double[] expected = {217.818827, 4.883391};
        assertArrayEquals(expected, regression.beta(), 1E-4);
    }

    @Test
    public void whenSimpleLinearRegressionThenStandardErrorsAccurate() {
        double[] expected = new double[]{4.8168057, 0.1747239};
        assertArrayEquals(expected, regression.standardErrors(), 1E-4);
    }

    @Test
    public void whenSimpleRegressionThenFittedValuesCorrect() {
        double[] fitted = getFittedValues();
        assertArrayEquals(fitted, regression.fitted(), 1E-4);
        double[] residuals = Operators.differenceOf(response, fitted);
        assertArrayEquals(residuals, regression.residuals(), 1E-4);
    }

    @Test
    public void whenSimpleRegressionNoInterceptThenBetaEstimatedCorrectly() {
        LinearRegression regression = MultipleLinearRegression.builder().from(this.regression).hasIntercept(false)
                                                              .build();
        double[] expected = {11.76188};
        assertArrayEquals(expected, regression.beta(), 1E-4);
    }

    @Test
    public void whenSimpleLinearRegressionThenSigma2Accurate() {
        assertThat(regression.sigma2(), is(closeTo(264.01, 1E-2)));
    }

    @Test
    public void whenInterceptDirectlyGivenThenResultsEquivalent() {
        double[] ones = DoubleFunctions.fill(47, 1.0);
        LinearRegression multipleRegression = MultipleLinearRegression.builder()
                                                                      .from(this.regression)
                                                                      .hasIntercept(false)
                                                                      .predictors(ones, time)
                                                                      .build();
        assertThat(multipleRegression.beta(), is(this.regression.beta()));
    }

    @Test
    public void equalsContract() {
        MultipleLinearRegression other = this.regression.withHasIntercept(!hasIntercept);
        MultipleLinearRegression other2 = this.regression
                .withPredictors(Range.inclusiveRange(1961, 2007, 1.0).asArray());
        MultipleLinearRegression other3 = this.regression.withResponse(TestData.livestock.demean().asArray());
        new EqualsTester()
                .addEqualityGroup(this.regression, MultipleLinearRegression.builder().from(this.regression).build())
                .addEqualityGroup(other, MultipleLinearRegression.builder().from(other).build())
                .addEqualityGroup(other2, MultipleLinearRegression.builder().from(other2).build())
                .addEqualityGroup(other3, MultipleLinearRegression.builder().from(other3).build())
                .testEquals();
    }

    private double[] getFittedValues() {
        return new double[]{222.702218, 227.58561, 232.469001, 237.352393, 242.235784, 247.119176, 252.002567,
                256.885959, 261.76935, 266.652742, 271.536133, 276.419525, 281.302916, 286.186308, 291.069699,
                295.953091, 300.836482, 305.719874, 310.603265, 315.486657, 320.370048, 325.25344, 330.136831,
                335.020223, 339.903614, 344.787006, 349.670397, 354.553788, 359.43718, 364.320571, 369.203963,
                374.087354, 378.970746, 383.854137, 388.737529, 393.62092, 398.504312, 403.387703, 408.271095,
                413.154486, 418.037878, 422.921269, 427.804661, 432.688052, 437.571444, 442.454835, 447.338227};
    }
}
