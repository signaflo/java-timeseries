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

package timeseries.models.regression;

import math.operations.DoubleFunctions;
import data.Range;
import data.regression.LinearRegressionPrediction;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;

@EqualsAndHashCode @ToString
public class TimeSeriesLinearRegressionForecast implements LinearRegressionPrediction {

    private final TimeSeriesLinearRegressionModel model;
    private final double[] predictedValues;

    private TimeSeriesLinearRegressionForecast(TimeSeriesLinearRegressionModel model, int steps) {
        this.model = model;
        Vector beta = Vector.from(model.beta());
        Matrix X = getPredictionMatrix(model, steps);
        this.predictedValues = X.times(beta).elements();
    }

    public static TimeSeriesLinearRegressionForecast forecast(TimeSeriesLinearRegressionModel model, int steps) {
        return new TimeSeriesLinearRegressionForecast(model, steps);
    }

    private Matrix getPredictionMatrix(TimeSeriesLinearRegressionModel model, int steps) {
        int intercept = model.intercept().asInt();
        int timeTrend = model.timeTrend().asInt();
        int seasonal = model.seasonal().asInt();
        int seasonalFrequency = model.seasonalFrequency();
        int ncols = intercept + timeTrend + (seasonalFrequency - 1) * seasonal;

        double[][] designMatrix = new double[ncols][steps];
        if (model.intercept().include()) {
            designMatrix[0] = DoubleFunctions.fill(steps, 1.0);
        }
        if (model.timeTrend().include()) {
            int startTime = model.response().length + 1;
            int endTime = startTime + steps;
            designMatrix[intercept] = Range.exclusiveRange(startTime, endTime).asArray();
        }
        if (model.seasonal().include()) {
            int periodOffset = model.response().length % seasonalFrequency;
            double[][] seasonalMatrix = TimeSeriesLinearRegressionModel.getSeasonalRegressors(steps, seasonalFrequency, periodOffset);
            for (int i = 0; i < seasonalMatrix.length; i++) {
                designMatrix[i + intercept + timeTrend] = seasonalMatrix[i];
            }
        }
        return Matrix.create(designMatrix, Matrix.Order.BY_COLUMN);
    }

    @Override
    public double[] predictedValues() {
        return this.predictedValues;
    }
}
