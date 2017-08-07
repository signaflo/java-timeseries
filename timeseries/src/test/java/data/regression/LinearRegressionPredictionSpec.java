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

import com.google.common.testing.EqualsTester;
import data.DoublePair;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;
import math.operations.DoubleFunctions;
import org.junit.Test;
import timeseries.TestData;

import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;

public class LinearRegressionPredictionSpec {

    private double[] mtcars_mpg = TestData.mtcars_mpg.clone();
    private double[] mtcars_hp = TestData.mtcars_hp.clone();
    private double[] mtcars_wt = TestData.mtcars_wt.clone();
    private double[][] predictors = {mtcars_hp, mtcars_wt};
    private MultipleLinearRegressionModel regression = MultipleLinearRegressionModel.builder()
                                                                            .response(mtcars_mpg)
                                                                            .predictors(predictors)
                                                                            .build();
    private MultipleLinearRegressionPredictor predictor = new MultipleLinearRegressionPredictor(regression);

    @Test
    public void whenPredictNewDataThenValueCorrect() {
        double[] newData = {300.0, 4.05};
        double predicted = predictor.predict(Vector.from(newData)).estimate();
        double expected = 11.99017;
        assertThat(predicted, is(closeTo(expected, 1E-4)));
        double[][] predictors = {{300.0, 4.05}, {320.0, 3.9}};
        Matrix predictionMatrix = Matrix.create(Matrix.Layout.BY_ROW, predictors);
        List<LinearRegressionPrediction> result = predictor.predict(predictionMatrix);
        double[] expectedPrediction = {11.99017, 11.93639};
        double[] expectedSeFit = {1.20136, 1.39828};
        for (int i = 0; i < expectedPrediction.length; i++) {
            LinearRegressionPrediction prediction = result.get(i);
            assertThat(prediction.estimate(), is(closeTo(expectedPrediction[i], 1E-4)));
            assertThat(prediction.fitStandardError(), is(closeTo(expectedSeFit[i], 1E-4)));
        }
    }

    @Test
    public void whenConfidenceIntervalThenCorrectPair() {
        Vector newData = Vector.from(300.0, 4.05);
        LinearRegressionPrediction prediction = predictor.predict(newData);
        DoublePair confidenceInterval = prediction.confidenceInterval();
        assertThat(confidenceInterval.first(), is(closeTo(9.533121, 1E-4)));
        assertThat(confidenceInterval.second(), is(closeTo(14.44722, 1E-4)));
    }

    @Test
    public void whenPredictionIntervalThenCorrectPair() {
        Vector newData = Vector.from(300.0, 4.05);
        LinearRegressionPrediction prediction = predictor.predict(newData);
        DoublePair predictionInterval = prediction.predictionInterval();
        assertThat(predictionInterval.first(), is(closeTo(6.144591, 1E-4)));
        assertThat(predictionInterval.second(), is(closeTo(17.83575, 1E-4)));
    }

    @Test
    public void equalsContract() {
        MultipleLinearRegressionModel other1 = this.regression.withHasIntercept(false);
        MultipleLinearRegressionModel other2 = this.regression.withPredictors(DoubleFunctions.boxCox(mtcars_hp, 0.0));
        MultipleLinearRegressionModel other3 = this.regression.withResponse(
                DoubleFunctions.boxCox(TestData.mtcars_mpg, 0.0));
        MultipleLinearRegressionPredictor predictor1 = new MultipleLinearRegressionPredictor(other1);
        MultipleLinearRegressionPredictor predictor2 = new MultipleLinearRegressionPredictor(other2);
        MultipleLinearRegressionPredictor predictor3 = new MultipleLinearRegressionPredictor(other3);
        new EqualsTester()
                .addEqualityGroup(this.predictor, new MultipleLinearRegressionPredictor(this.regression))
                .addEqualityGroup(predictor1, new MultipleLinearRegressionPredictor(other1))
                .addEqualityGroup(predictor2, new MultipleLinearRegressionPredictor(other2))
                .addEqualityGroup(predictor3, new MultipleLinearRegressionPredictor(other3))
                .testEquals();
    }
}
