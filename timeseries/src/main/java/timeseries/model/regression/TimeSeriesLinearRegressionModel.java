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

import data.Range;
import data.regression.*;
import lombok.NonNull;
import lombok.ToString;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;
import math.operations.DoubleFunctions;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.forecast.Forecast;
import timeseries.forecast.Forecaster;
import timeseries.model.Model;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static math.operations.DoubleFunctions.copy;

/**
 * A linear regression model for time series data.
 */
@ToString
public final class TimeSeriesLinearRegressionModel implements TimeSeriesLinearRegression, Model {

    private final MultipleLinearRegression regression;
    private final TimeSeries timeSeries;
    private final TimePeriod seasonalCycle;
    private final Intercept intercept;
    private final TimeTrend timeTrend;
    private final Seasonal seasonal;
    private final double[][] externalRegressors;

    TimeSeriesLinearRegressionModel(Builder timeSeriesRegressionBuilder) {
        this.timeSeries = timeSeriesRegressionBuilder.response;
        this.seasonalCycle = timeSeriesRegressionBuilder.seasonalCycle;
        this.externalRegressors = timeSeriesRegressionBuilder.externalRegressors;
        double[][] allPredictors = DoubleFunctions.combine(timeSeriesRegressionBuilder.timeBasedPredictors,
                                                           timeSeriesRegressionBuilder.externalRegressors);
        MultipleRegressionBuilder regressionBuilder = MultipleLinearRegressionModel.builder();
        regressionBuilder.hasIntercept(timeSeriesRegressionBuilder.intercept.include())
                         .predictors(allPredictors)
                         .response(timeSeries.asArray());
        this.regression = regressionBuilder.build();
        this.intercept = timeSeriesRegressionBuilder.intercept;
        this.timeTrend = timeSeriesRegressionBuilder.timeTrend;
        this.seasonal = timeSeriesRegressionBuilder.seasonal;
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
    public TimeSeries timeSeries() {
        return this.observations();
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

    @Override
    public TimeSeries observations() {
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

    public static TimeSeriesLinearRegressionModel.Builder builder() {
        return new Builder();
    }

    static double[] getIthSeasonalRegressor(int nrows, int startRow, int seasonalFrequency) {
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

    Matrix getPredictionMatrix(int steps) {
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

    /**
     * A builder for a time series linear regression model.
     */
    public static final class Builder {
        private double[][] timeBasedPredictors = new double[0][0];
        private double[][] externalRegressors = new double[0][0];
        private TimeSeries response;
        private Intercept intercept = Intercept.INCLUDE;
        private TimeTrend timeTrend = TimeTrend.INCLUDE;
        private Seasonal seasonal = Seasonal.EXCLUDE;
        private TimePeriod seasonalCycle = TimePeriod.oneYear();

        /**
         * Copy the attributes of the given regression object to this builder and return this builder.
         *
         * @param regression the object to copy the attributes from.
         * @return this builder.
         */
        public final Builder from(TimeSeriesLinearRegression regression) {
            this.externalRegressors = copy(regression.predictors());
            this.response = regression.observations();
            this.intercept = regression.intercept();
            this.timeTrend = regression.timeTrend();
            this.seasonal = regression.seasonal();
            this.seasonalCycle = regression.seasonalCycle();
            return this;
        }

        /**
         * Specify prediction variable data for the linear regression model. Note that if this method has already been
         * called on this object, then the array of prediction variables will be <i>appended to</i> rather than
         * overwritten. Each element of the two dimensional external regressors outer array is interpreted as a
         * column vector of data for a single prediction variable.
         *
         * @param regressors the external regressors to add to the regression model specification.
         * @return this builder.
         */
        public Builder externalRegressors(@NonNull double[]... regressors) {
            int currentCols = this.externalRegressors.length;
            int currentRows = 0;
            if (currentCols > 0) {
                currentRows = this.externalRegressors[0].length;
            } else if (regressors.length > 0) {
                currentRows = regressors[0].length;
            }
            double[][] newPredictors = new double[currentCols + regressors.length][currentRows];
            for (int i = 0; i < currentCols; i++) {
                System.arraycopy(this.externalRegressors[i], 0, newPredictors[i], 0, currentRows);
            }
            for (int i = 0; i < regressors.length; i++) {
                newPredictors[i + currentCols] = regressors[i].clone();
            }
            this.externalRegressors = newPredictors;
            return this;
        }

        /**
         * Specify prediction variable data for the linear regression model. Note that if this method has already been
         * called on this object, then the array of prediction variables will be <i>appended to</i> rather than
         * overwritten. Each element of the two dimensional external predictors outer array is interpreted as a
         * column vector of data for a single prediction variable.
         *
         * @param predictors the external predictors to add to the regression model specification.
         * @return this builder.
         */
        private Builder timeBasedPredictors(@NonNull double[]... predictors) {
            int currentCols = this.timeBasedPredictors.length;
            int currentRows = 0;
            if (currentCols > 0) {
                currentRows = this.timeBasedPredictors[0].length;
            } else if (predictors.length > 0) {
                currentRows = predictors[0].length;
            }
            double[][] newPredictors = new double[currentCols + predictors.length][currentRows];
            for (int i = 0; i < currentCols; i++) {
                System.arraycopy(this.timeBasedPredictors[i], 0, newPredictors[i], 0, currentRows);
            }
            for (int i = 0; i < predictors.length; i++) {
                newPredictors[i + currentCols] = predictors[i].clone();
            }
            this.timeBasedPredictors = newPredictors;
            return this;
        }

        /**
         * Specify prediction variable data for the linear regression model. Note that if this method has already been
         * called on this object, then the matrix of prediction variables will be <i>appended to</i> rather than
         * overwritten.
         *
         * @param regressors the external regressors to add to the regression model specification.
         * @return this builder.
         */
        public Builder externalRegressors(@NonNull Matrix regressors) {
            externalRegressors(regressors.data2D(Matrix.Layout.BY_COLUMN));
            return this;
        }

        /**
         * Specify the response, or dependent variable, in the form of a time series.
         *
         * @param response the response, or dependent variable, in the form of a time series.
         * @return this builder.
         */
        public Builder response(@NonNull TimeSeries response) {
            this.response = response;
            return this;
        }

        /**
         * Specify whether to include an intercept in the regression model. The default is for an intercept to
         * be included.
         *
         * @param intercept whether or not to include an intercept in the model.
         * @return this builder.
         */
        public Builder hasIntercept(@NonNull Intercept intercept) {
            this.intercept = intercept;
            return this;
        }

        /**
         * Specify whether to include a time trend in the regression model. The default is for a time trend
         * to be included.
         *
         * @param timeTrend whether or not to include a time trend in the model.
         * @return this builder.
         */
        public Builder timeTrend(@NonNull TimeTrend timeTrend) {
            this.timeTrend = timeTrend;
            return this;
        }

        /**
         * Specify whether to include a seasonal component in the regression model. The default is for the seasonal
         * component to be excluded.
         *
         * @param seasonal whether or not to include a seasonal component in the model.
         * @return this builder.
         */
        public Builder seasonal(@NonNull Seasonal seasonal) {
            this.seasonal = seasonal;
            return this;
        }

        /**
         * Specify the length of time it takes for the seasonal pattern to complete one cycle.
         *
         * @param seasonalCycle the length of time it takes for the seasonal pattern to complete one cycle.
         *                      the default value for this property is one year.
         * @return this builder.
         */
        public Builder seasonalCycle(@NonNull TimePeriod seasonalCycle) {
            this.seasonalCycle = seasonalCycle;
            return this;
        }

        public TimeSeriesLinearRegressionModel build() {
            if (response == null) {
                throw new IllegalStateException("A time series linear regression model " +
                                                "must have a non-null response.");
            }
            if (this.timeTrend.include()) {
                this.timeBasedPredictors(Range.inclusiveRange(1, response.size()).asArray());
            }
            if (this.seasonal.include()) {
                int seasonalFrequency = (int) this.response.timePeriod().frequencyPer(this.seasonalCycle);
                int periodOffset = 0;
                double[][] seasonalRegressors = getSeasonalRegressors(this.response.size(), seasonalFrequency,
                                                                      periodOffset);
                this.timeBasedPredictors(seasonalRegressors);
            }
            return new TimeSeriesLinearRegressionModel(this);
        }
    }
}
