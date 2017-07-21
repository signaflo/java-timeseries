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

import data.Range;
import data.regression.LinearRegressionModel;
import data.regression.MultipleLinearRegressionModel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import math.linear.doubles.Matrix;
import timeseries.TimePeriod;
import timeseries.TimeSeries;

/**
 * A linear regression model for time series data.
 */
@EqualsAndHashCode @ToString
public final class TimeSeriesLinearRegressionModel implements LinearRegressionModel {

    private final LinearRegressionModel regression;
    private final TimeSeries timeSeries;
    private final TimePeriod seasonalCycle;
    private final Intercept intercept;
    private final TimeTrend timeTrend;
    private final Seasonal seasonal;

    TimeSeriesLinearRegressionModel(Builder timeSeriesRegressionBuilder) {
        this.timeSeries = timeSeriesRegressionBuilder.response;
        this.seasonalCycle = timeSeriesRegressionBuilder.seasonalCycle;
        MultipleLinearRegressionModel.Builder regressionBuilder = MultipleLinearRegressionModel.builder();
        regressionBuilder.hasIntercept(timeSeriesRegressionBuilder.intercept.include())
                         .predictors(timeSeriesRegressionBuilder.predictors)
                         .response(timeSeries.asArray());
        this.regression = regressionBuilder.build();
        this.intercept = timeSeriesRegressionBuilder.intercept;
        this.timeTrend = timeSeriesRegressionBuilder.timeTrend;
        this.seasonal = timeSeriesRegressionBuilder.seasonal;
    }

    @Override
    public double[][] predictors() {
        return regression.predictors();
    }

    @Override
    public double[][] designMatrix() {
        return regression.designMatrix();
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
    public double[] residuals() {
        return regression.residuals();
    }

    @Override
    public double sigma2() {
        return regression.sigma2();
    }

    @Override
    public boolean hasIntercept() {
        return regression.hasIntercept();
    }

    public Intercept intercept() {
        return this.intercept;
    }

    public TimeTrend timeTrend() {
        return this.timeTrend;
    }

    public Seasonal seasonal() {
        return this.seasonal;
    }

    public int seasonalFrequency() {
        return (int) this.timeSeries.timePeriod().frequencyPer(this.seasonalCycle);
    }

    public static TimeSeriesLinearRegressionModel.Builder builder() {
        return new Builder();
    }

    /**
     * An indicator for whether a time series regression model has an intercept.
     */
    public enum Intercept {
        INCLUDE(1), EXCLUDE(0);

        private final int intercept;

        Intercept(final int intercept) {
            this.intercept = intercept;
        }

        boolean include() {
            return this == INCLUDE;
        }
        int asInt() {
            return this.intercept;
        }
    }

    /**
     * An indicator for whether a time series regression model has a time trend.
     */
    public enum TimeTrend {
        INCLUDE(1), EXCLUDE(0);

        private final int timeTrend;

        TimeTrend(final int timeTrend) {
            this.timeTrend = timeTrend;
        }

        boolean include() {
            return this == INCLUDE;
        }
        int asInt() {
            return this.timeTrend;
        }
    }

    /**
     * An indictor for whether a time series regresson model has a seasonal component.
     */
    public enum Seasonal {
        INCLUDE(1), EXCLUDE(0);

        private final int seasonal;

        Seasonal(final int seasonal) {
            this.seasonal = seasonal;
        }

        boolean include() {
            return this == INCLUDE;
        }
        int asInt() {
            return this.seasonal;
        }
    }

    static double[] getIthSeasonalRegressor(int nrows, int startRow, int seasonalFrequency) {
        double[] regressor = new double[nrows];
        for (int j = 0; j < regressor.length - startRow; j += seasonalFrequency) {
            regressor[j + startRow] = 1.0;
        }
        return regressor;
    }

    // What we are doing here is equivalent to how R handles "factors" in linear regression models.
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

    /**
     * A builder for a time series linear regression model.
     */
    public static final class Builder {
        private double[][] predictors = new double[0][0];
        private TimeSeries response;
        private Intercept intercept = Intercept.INCLUDE;
        private TimeTrend timeTrend = TimeTrend.INCLUDE;
        private Seasonal seasonal = Seasonal.EXCLUDE;
        private TimePeriod seasonalCycle = TimePeriod.oneYear();

        /**
         * Specify prediction variable data for the linear regression model. Note that if this method has already been
         * called on this object, then the array of prediction variables will be <i>appended to</i> rather than
         * overwritten. Each element of the two dimensional predictors outer array is interpreted as a column vector of
         * data for a single prediction variable.
         *
         * @param predictors the predictors to add to the regression model specification.
         * @return this builder.
         */
        public Builder externalRegressors(@NonNull double[]... predictors) {
            int currentCols = this.predictors.length;
            int currentRows = 0;
            if (currentCols > 0) {
                currentRows = this.predictors[0].length;
            } else if (predictors.length > 0) {
                currentRows = predictors[0].length;
            }
            double[][] newPredictors = new double[currentCols + predictors.length][currentRows];
            for (int i = 0; i < currentCols; i++) {
                System.arraycopy(this.predictors[i], 0, newPredictors[i], 0, currentRows);
            }
            for (int i = 0; i < predictors.length; i++) {
                newPredictors[i + currentCols] = predictors[i].clone();
            }
            this.predictors = newPredictors;
            return this;
        }

        /**
         * Specify prediction variable data for the linear regression model. Note that if this method has already been
         * called on this object, then the matrix of prediction variables will be <i>appended to</i> rather than
         * overwritten.
         *
         * @param predictors the predictors to add to the regression model specification.
         * @return this builder.
         */
        public Builder externalRegressors(@NonNull Matrix predictors) {
            externalRegressors(predictors.data2D(Matrix.StorageMode.BY_COLUMM));
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
        public Builder seasonal(Seasonal seasonal) {
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
        public Builder seasonalCycle(TimePeriod seasonalCycle) {
            this.seasonalCycle = seasonalCycle;
            return this;
        }

        public TimeSeriesLinearRegressionModel build() {
            if (this.timeTrend.include()) {
                this.externalRegressors(Range.inclusiveRange(1, response.size()).asArray());
            }
            if (this.seasonal.include()) {
                int seasonalFrequency = (int) this.response.timePeriod().frequencyPer(this.seasonalCycle);
                int periodOffset = 0;
                double[][] seasonalRegressors = getSeasonalRegressors(this.response.size(), seasonalFrequency,
                                                                      periodOffset);
                this.externalRegressors(seasonalRegressors);
            }
            return new TimeSeriesLinearRegressionModel(this);
        }
    }
}
