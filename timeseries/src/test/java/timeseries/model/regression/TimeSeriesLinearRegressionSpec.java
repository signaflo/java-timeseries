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

package timeseries.model.regression;

import com.google.common.testing.EqualsTester;
import org.junit.Test;
import timeseries.TestData;
import timeseries.TimeSeries;

import static org.junit.Assert.assertArrayEquals;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class TimeSeriesLinearRegressionSpec {

    private TimeSeries livestock = TestData.livestock;
    private TimeSeriesLinearRegressionModel model =
            TimeSeriesLinearRegressionModel.builder()
                                           .response(livestock)
                                           .build();

    @Test
    public void whenTSLRFitThenBetaCorrect() {
        TimeSeries debitcards = TestData.debitcards.timeSlice(1, 156);
        TimeSeriesLinearRegressionModel.Builder tslmBuilder =
                TimeSeriesLinearRegressionModel.builder()
                                               .response(debitcards)
                                               .seasonal(TimeSeriesLinearRegressionModel.Seasonal.INCLUDE);
        TimeSeriesLinearRegression regression = tslmBuilder.build();
        double[] expected = {6568.304945, 92.552198, -344.706044, 590.510989, 367.035714, 2166.637363, 2343.239011,
                2621.840659, 3399.442308, 1197.505495, 1310.491758, 868.631868, 5646.618132};
        assertArrayEquals(expected, regression.beta(), 1E-6);
    }

    @Test
    public void whenNotSpecifiedThenModelHasInterceptByDefault() {
        assertThat(model.hasIntercept(), is(true));
    }

    @Test
    public void whenModelFitThenSigma2Correct() {
        assertThat(model.sigma2(), is(closeTo(264.01, 1E-2)));
    }

    @Test
    public void whenModelFitThenFittedValuesCorrect() {
        assertArrayEquals(getFittedValues(), model.fitted(), 1E-6);
    }

    @Test
    public void whenModelFitThenResidualsCorrect() {
        double[] expectedResiduals = livestock.minus(getFittedValues()).asArray();
        assertArrayEquals(expectedResiduals, model.residuals(), 1E-6);
    }

    @Test
    public void whenModelFitThenCorrectPredictorsReturned() {
        double[][] expectedPredictors = new double[][] {};
        assertThat(model.predictors(), is(expectedPredictors));
    }
//
//    @Test
//    public void whenModelFitThenCorrectDesignMatrix() {
//        double[] ones = DoubleFunctions.fill(47, 1.0);
//        double[] time = Range.inclusiveRange(1, 47).asArray();
//        double[][] expectedDesignMatrix = new double[][] {ones, time};
//        assertThat(model.designMatrix(), is(expectedDesignMatrix));
//    }

    @Test
    public void equalsContract() {
        TimeSeriesLinearRegression otherModel = TimeSeriesLinearRegressionModel.builder().from(model).build();
        TimeSeriesLinearRegression modelA =
                TimeSeriesLinearRegressionModel.builder()
                                               .response(livestock)
                                               .hasIntercept(TimeSeriesLinearRegressionModel.Intercept.EXCLUDE)
                                               .build();
        TimeSeriesLinearRegression modelB = TimeSeriesLinearRegressionModel.builder().from(modelA).build();
        TimeSeriesLinearRegression model2A =
                TimeSeriesLinearRegressionModel.builder()
                                               .response(livestock)
                                               .timeTrend(TimeSeriesLinearRegressionModel.TimeTrend.EXCLUDE)
                                               .build();
        TimeSeriesLinearRegression model2B = TimeSeriesLinearRegressionModel.builder().from(model2A).build();
        TimeSeriesLinearRegression model3A =
                TimeSeriesLinearRegressionModel.builder()
                                               .response(livestock.demean())
                                               .build();
        TimeSeriesLinearRegression model3B = TimeSeriesLinearRegressionModel.builder().from(model3A).build();
        new EqualsTester()
                .addEqualityGroup(model, otherModel)
                .addEqualityGroup(modelA, modelB)
                .addEqualityGroup(model2A, model2B)
                .addEqualityGroup(model3A, model3B)
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
