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

import data.DoubleFunctions;
import data.Range;
import data.regression.primitive.LinearRegressionPrediction;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;

public class TimeSeriesLinearRegressionForecast implements LinearRegressionPrediction {

    private final TimeSeriesLinearRegressionModel model;
    private final double[] predictedValues;

    TimeSeriesLinearRegressionForecast(TimeSeriesLinearRegressionModel model, int steps) {
        this.model = model;
        Vector beta = Vector.from(model.beta());
        Matrix X = getPredictionMatrix(model, steps);
        this.predictedValues = X.times(beta).elements();
    }

    private Matrix getPredictionMatrix(TimeSeriesLinearRegressionModel model, int steps) {
        int intercept = model.intercept().asInt();
        int timeTrend = model.timeTrend().asInt();
        int seasonal = model.seasonal().asInt();

        int ncols = intercept + timeTrend + (model.seasonalFrequency() - 1) * seasonal;
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
            double[][] seasonalMatrix = getSeasonalRegressors(model.seasonalFrequency(), steps);
            for (int i = 0; i < seasonalMatrix.length; i++) {
                designMatrix[i + intercept + timeTrend] = seasonalMatrix[i];
            }
        }
        return new Matrix(designMatrix, Matrix.Order.COLUMN_MAJOR);
    }

    // What we are doing here is equivalent to how R handles "factors" in linear regression models.
    private double[][] getSeasonalRegressors(int seasonalFrequency, int steps) {
        int startTime = model.response().length % model.seasonalFrequency() + 1;
        double[][] seasonalRegressors = new double[seasonalFrequency - 1][steps];
        for (int i = 1; i < seasonalFrequency; i++) {
            seasonalRegressors[i - 1] = getIthSeasonalRegressor((i - 1) + startTime, seasonalFrequency, steps);
        }
        return seasonalRegressors;
    }

    private double[] getIthSeasonalRegressor(int i, int seasonalFrequency, int steps) {
        double[] regressor = new double[steps];
        for (int j = 0; j < regressor.length && i < steps; j += seasonalFrequency) {
            regressor[j + i] = 1.0;
        }
        return regressor;
    }

    @Override
    public double[] predictedValues() {
        return this.predictedValues;
    }
}
