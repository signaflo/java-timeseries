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

package data.regression;

import data.Pair;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;
import org.junit.Test;
import timeseries.TestData;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class LinearRegressionPredictionSpec {

    double[] mtcars_mpg = TestData.mtcars_mpg.clone();
    double[] mtcars_hp = TestData.mtcars_hp.clone();
    double[] mtcars_wt = TestData.mtcars_wt.clone();
    double[][] predictors = {mtcars_hp, mtcars_wt};
    MultipleLinearRegressionModel model = MultipleLinearRegressionModel.builder()
                                                               .response(mtcars_mpg)
                                                               .predictors(predictors)
                                                               .build();
    MultipleLinearRegressionPredictor predictor = new MultipleLinearRegressionPredictor(model);

    @Test
    public void whenPredictNewDataThenValueCorrect() {
        double[] newData = {300.0, 4.05};
        double predicted = predictor.predict(Vector.from(newData));
        double expected = 11.99017;
        assertThat(predicted, is(closeTo(expected, 1E-4)));
        double[][] predictors = {{300.0, 4.05}, {320.0, 3.9}};
        Matrix predictionMatrix = Matrix.create(predictors, Matrix.Order.BY_ROW);
        Vector result = predictor.predict(predictionMatrix);
        Vector seFit = predictor.standardErrorFit(predictionMatrix);
        System.out.println(result);
        System.out.println(seFit);
    }

    @Test
    public void whenConfidenceIntervalThenCorrectPair() {
        double[] newData = {300.0, 4.05};
        Pair<Double, Double> confidenceInterval = predictor.confidenceInterval(0.05, Vector.from(newData));
        assertThat(confidenceInterval.first, is(closeTo(9.533121, 1E-4)));
        assertThat(confidenceInterval.second, is(closeTo(14.44722, 1E-4)));
    }
}
