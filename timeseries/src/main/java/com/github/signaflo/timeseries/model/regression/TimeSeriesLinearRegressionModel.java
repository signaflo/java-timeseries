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

package com.github.signaflo.timeseries.model.regression;

import com.github.signaflo.data.Range;
import com.github.signaflo.data.regression.*;
import lombok.ToString;
import com.github.signaflo.math.linear.doubles.Matrix;
import com.github.signaflo.math.linear.doubles.Vector;
import com.github.signaflo.math.operations.DoubleFunctions;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.forecast.Forecaster;
import com.github.signaflo.timeseries.model.Model;

import java.util.Arrays;

import static com.github.signaflo.math.operations.DoubleFunctions.copy;

/**
 * A linear regression model for time series com.github.signaflo.data.
 */
@ToString
final class TimeSeriesLinearRegressionModel implements TimeSeriesLinearRegression, Model {

    private final MultipleLinearRegression regression;
    private final TimeSeries timeSeries;
    private final TimePeriod seasonalCycle;
    private final Intercept intercept;
    private final TimeTrend timeTrend;
    private final Seasonal seasonal;
    private final double[][] externalRegressors;

    TimeSeriesLinearRegressionModel(TimeSeriesLinearRegressionBuilder timeSeriesRegressionBuilder) {
        this.timeSeries = timeSeriesRegressionBuilder.response();
        this.seasonalCycle = timeSeriesRegressionBuilder.seasonalCycle();
        this.externalRegressors = timeSeriesRegressionBuilder.externalRegressors();
        double[][] allPredictors = DoubleFunctions.combine(timeSeriesRegressionBuilder.timeBasedPredictors(),
                                                           timeSeriesRegressionBuilder.externalRegressors());
        MultipleRegressionBuilder regressionBuilder = MultipleLinearRegression.builder();
        regressionBuilder.hasIntercept(timeSeriesRegressionBuilder.intercept().include())
                         .predictors(allPredictors)
                         .response(timeSeries.asArray());
        this.regression = regressionBuilder.build();
        this.intercept = timeSeriesRegressionBuilder.intercept();
        this.timeTrend = timeSeriesRegressionBuilder.timeTrend();
        this.seasonal = timeSeriesRegressionBuilder.seasonal();
    }

    @Override
    public double[][] predictors() {
        return copy(this.externalRegressors);
    }

    @Override
    public double[][] XtXInverse() {
        return this.regression.XtXInverse();
    }

    @Override
    public double[][] designMatrix() {
        return this.regression.designMatrix();
    }

    @Override
    public double[] response() {
        return regression.response();
    }

    @Override
    public double[] beta() {
        return regression.beta();
    }

    @Override
    public double[] standardErrors() {
        return regression.standardErrors();
    }

    @Override
    public double[] fitted() {
        return regression.fitted();
    }

    @Override
    public Forecast forecast(int steps, double alpha) {
        MultipleLinearRegressionPredictor predictor = MultipleLinearRegressionPredictor.from(this);
        Vector beta = Vector.from(this.beta());
        Matrix predictionMatrix = getPredictionMatrix(steps);
        Forecaster forecaster = new TimeSeriesRegressionForecaster(this.timeSeries, predictor, beta, predictionMatrix);
        return forecaster.forecast(steps, alpha);

    }

    @Override
    public TimeSeries observations() {
        return this.timeSeriesResponse();
    }

    @Override
    public TimeSeries fittedSeries() {
        return null;
    }

    @Override
    public double sigma2() {
        return regression.sigma2();
    }

    @Override
    public boolean hasIntercept() {
        return regression.hasIntercept();
    }

    @Override
    public TimePeriod seasonalCycle() {
        return this.seasonalCycle;
    }

    public TimeSeries timeSeriesResponse() {
        return this.timeSeries;
    }

    @Override
    public Intercept intercept() {
        return this.intercept;
    }

    @Override
    public TimeTrend timeTrend() {
        return this.timeTrend;
    }

    @Override
    public Seasonal seasonal() {
        return this.seasonal;
    }

    @Override
    public int seasonalFrequency() {
        return (int) this.timeSeries.timePeriod().frequencyPer(this.seasonalCycle);
    }

    private static double[] getIthSeasonalRegressor(int nrows, int startRow, int seasonalFrequency) {
        double[] regressor = new double[nrows];
        for (int j = 0; j < regressor.length - startRow; j += seasonalFrequency) {
            regressor[j + startRow] = 1.0;
        }
        return regressor;
    }

    // What we are doing here is equivalent to how R handles "factors" in linear regression model.
    static double[][] getSeasonalRegressors(int nrows, int seasonalFrequency, int periodOffset) {
        int ncols = seasonalFrequency - 1;
        double[][] seasonalRegressors = new double[ncols][nrows];
        for (int i = 0; i < ncols; i++) {
            // Apparently, the "modulus" operator in Java is not actually a modulus operator, but a remainder operator.
            // floorMod was added in Java 8 to give results one would expect when doing modular arithmetic.
            int startRow = Math.floorMod(i + 1 - periodOffset, seasonalFrequency);
            seasonalRegressors[i] = getIthSeasonalRegressor(nrows, startRow, seasonalFrequency);
        }
        return seasonalRegressors;
    }

    private Matrix getPredictionMatrix(int steps) {
        int intercept = this.intercept().asInt();
        int timeTrend = this.timeTrend().asInt();
        int seasonal = this.seasonal().asInt();
        int seasonalFrequency = this.seasonalFrequency();
        int ncols = intercept + timeTrend + (seasonalFrequency - 1) * seasonal;

        double[][] designMatrix = new double[ncols][steps];
        if (this.intercept().include()) {
            designMatrix[0] = DoubleFunctions.fill(steps, 1.0);
        }
        if (this.timeTrend().include()) {
            int startTime = this.response().length + 1;
            int endTime = startTime + steps;
            designMatrix[intercept] = Range.exclusiveRange(startTime, endTime).asArray();
        }
        if (this.seasonal().include()) {
            int periodOffset = this.response().length % seasonalFrequency;
            double[][] seasonalMatrix = getSeasonalRegressors(steps, seasonalFrequency, periodOffset);
            for (int i = 0; i < seasonalMatrix.length; i++) {
                designMatrix[i + intercept + timeTrend] = seasonalMatrix[i];
            }
        }
        return Matrix.create(Matrix.Layout.BY_COLUMN, designMatrix);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSeriesLinearRegressionModel that = (TimeSeriesLinearRegressionModel) o;

        if (!timeSeries.equals(that.timeSeries)) return false;
        if (!seasonalCycle.equals(that.seasonalCycle)) return false;
        if (intercept != that.intercept) return false;
        if (timeTrend != that.timeTrend) return false;
        if (seasonal != that.seasonal) return false;
        return Arrays.deepEquals(externalRegressors, that.externalRegressors);
    }

    @Override
    public int hashCode() {
        int result = timeSeries.hashCode();
        result = 31 * result + seasonalCycle.hashCode();
        result = 31 * result + intercept.hashCode();
        result = 31 * result + timeTrend.hashCode();
        result = 31 * result + seasonal.hashCode();
        result = 31 * result + Arrays.deepHashCode(externalRegressors);
        return result;
    }

}
