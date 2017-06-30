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
import math.linear.doubles.Matrix;
import timeseries.models.regression.primitive.LinearRegression;
import timeseries.models.regression.primitive.MultipleLinearRegression;
import lombok.NonNull;
import timeseries.TimeSeries;

public final class TimeSeriesLinearRegression implements LinearRegression{

    private final LinearRegression regression;

    TimeSeriesLinearRegression(Builder timeSeriesRegressionBuilder) {
        TimeSeries timeSeries = timeSeriesRegressionBuilder.response;
        MultipleLinearRegression.Builder regressionBuilder = MultipleLinearRegression.builder();
        regressionBuilder.hasIntercept(timeSeriesRegressionBuilder.intercept.include())
                         .predictors(timeSeriesRegressionBuilder.predictors)
                         .response(timeSeries.asArray());
        this.regression = regressionBuilder.build();
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

    public static TimeSeriesLinearRegression.Builder builder() {
        return new Builder();
    }

    public enum Intercept {
        INCLUDE, EXCLUDE;

        boolean include() {
            return this == INCLUDE;
        }
    }

    public enum TimeTrend {
        INCLUDE, EXCLUDE;

        boolean include() {
            return this == INCLUDE;
        }
    }

    enum Seasonal {
        INCLUDE, EXCLUDE;

        boolean include() {
            return this == INCLUDE;
        }
    }

    public static final class Builder {
        private double[][] predictors = new double[0][0];
        private @NonNull TimeSeries response;
        private Intercept intercept = Intercept.INCLUDE;
        private TimeTrend timeTrend = TimeTrend.INCLUDE;
        private Seasonal seasonal = Seasonal.EXCLUDE;

        public Builder externalRegressors(double[]... predictors) {
            this.predictors = new double[predictors.length][];
            for (int i = 0; i < predictors.length; i++) {
                this.predictors[i] = predictors[i].clone();
            }
            return this;
        }

        public Builder externalRegressors(Matrix predictors) {
            externalRegressors(predictors.data2D(Matrix.Order.COLUMN_MAJOR));
            return this;
        }

        public Builder externalRegressor(double... predictor) {
            int currentCols = this.predictors.length;
            int currentRows = 0;
            if (currentCols > 0) {
                currentRows = this.predictors[0].length;
            }
            double[][] newPredictors = new double[currentCols + 1][currentRows];
            for (int i = 0; i < currentCols; i++) {
                System.arraycopy(this.predictors[i], 0, newPredictors[i], 0, currentRows);
            }
            newPredictors[currentCols] = predictor;
            this.predictors = newPredictors;
            return this;
        }

        public Builder response(TimeSeries response) {
            this.response = response;
            return this;
        }

        public Builder hasIntercept(Intercept intercept) {
            this.intercept = intercept;
            return this;
        }

        public Builder timeTrend(TimeTrend timeTrend) {
            this.timeTrend = timeTrend;
            return this;
        }

        public Builder seasonal(Seasonal seasonal) {
            this.seasonal = seasonal;
            return this;
        }

        public TimeSeriesLinearRegression build() {
            if (this.timeTrend.include()) {
                this.externalRegressor(Range.inclusiveRange(1, response.size()).asArray());
            }
            return new TimeSeriesLinearRegression(this);
        }
    }
}
