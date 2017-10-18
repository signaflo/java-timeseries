package com.github.signaflo.timeseries.model.regression;

import com.github.signaflo.data.Range;
import lombok.NonNull;
import com.github.signaflo.math.linear.doubles.Matrix;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;

import static com.github.signaflo.math.operations.DoubleFunctions.copy;

/**
 * A builder for a time series linear regression model.
 */
public final class TimeSeriesLinearRegressionBuilder {

    private double[][] timeBasedPredictors = new double[0][0];
    private double[][] externalRegressors = new double[0][0];
    private TimeSeries response;
    private TimeSeriesLinearRegression.Intercept intercept = TimeSeriesLinearRegression.Intercept.INCLUDE;
    private TimeSeriesLinearRegression.TimeTrend timeTrend = TimeSeriesLinearRegression.TimeTrend.INCLUDE;
    private TimeSeriesLinearRegression.Seasonal seasonal = TimeSeriesLinearRegression.Seasonal.EXCLUDE;
    private TimePeriod seasonalCycle = TimePeriod.oneYear();

    /**
     * Copy the attributes of the given regression object to this builder and return this builder.
     *
     * @param regression the object to copy the attributes from.
     * @return this builder.
     */
    public final TimeSeriesLinearRegressionBuilder from(TimeSeriesLinearRegression regression) {
        this.externalRegressors = copy(regression.predictors());
        this.response = regression.timeSeriesResponse();
        this.intercept = regression.intercept();
        this.timeTrend = regression.timeTrend();
        this.seasonal = regression.seasonal();
        this.seasonalCycle = regression.seasonalCycle();
        return this;
    }

    /**
     * Specify prediction variable com.github.signaflo.data for the linear regression model. Note that if this method has already been
     * called on this object, then the array of prediction variables will be <i>appended to</i> rather than
     * overwritten. Each element of the two dimensional external regressors outer array is interpreted as a
     * column vector of com.github.signaflo.data for a single prediction variable.
     *
     * @param regressors the external regressors to add to the regression model specification.
     * @return this builder.
     */
    public TimeSeriesLinearRegressionBuilder externalRegressors(@NonNull double[]... regressors) {
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
     * Specify prediction variable com.github.signaflo.data for the linear regression model. Note that if this method has already been
     * called on this object, then the array of prediction variables will be <i>appended to</i> rather than
     * overwritten. Each element of the two dimensional external predictors outer array is interpreted as a
     * column vector of com.github.signaflo.data for a single prediction variable.
     *
     * @param predictors the external predictors to add to the regression model specification.
     * @return this builder.
     */
    private TimeSeriesLinearRegressionBuilder timeBasedPredictors(@NonNull double[]... predictors) {
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
     * Specify prediction variable com.github.signaflo.data for the linear regression model. Note that if this method has already been
     * called on this object, then the matrix of prediction variables will be <i>appended to</i> rather than
     * overwritten.
     *
     * @param regressors the external regressors to add to the regression model specification.
     * @return this builder.
     */
    public TimeSeriesLinearRegressionBuilder externalRegressors(@NonNull Matrix regressors) {
        externalRegressors(regressors.data2D(Matrix.Layout.BY_COLUMN));
        return this;
    }

    /**
     * Specify the response, or dependent variable, in the form of a time series.
     *
     * @param response the response, or dependent variable, in the form of a time series.
     * @return this builder.
     */
    public TimeSeriesLinearRegressionBuilder response(@NonNull TimeSeries response) {
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
    public TimeSeriesLinearRegressionBuilder hasIntercept(@NonNull TimeSeriesLinearRegression.Intercept intercept) {
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
    public TimeSeriesLinearRegressionBuilder timeTrend(@NonNull TimeSeriesLinearRegression.TimeTrend timeTrend) {
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
    public TimeSeriesLinearRegressionBuilder seasonal(@NonNull TimeSeriesLinearRegression.Seasonal seasonal) {
        this.seasonal = seasonal;
        return this;
    }

    /**
     * Specify the length of time it takes for the seasonal pattern to complete one cycle.
     *
     * @param seasonalCycle the length of time it takes for the seasonal pattern to complete one cycle.
     *                      The default value for this property is one year.
     * @return this builder.
     */
    public TimeSeriesLinearRegressionBuilder seasonalCycle(@NonNull TimePeriod seasonalCycle) {
        this.seasonalCycle = seasonalCycle;
        return this;
    }

    public TimeSeriesLinearRegression build() {
        if (response == null) {
            throw new IllegalStateException("A time series linear regression model " +
                                            "must have a non-null response variable.");
        }
        if (this.timeTrend.include()) {
            this.timeBasedPredictors(Range.inclusiveRange(1, response.size()).asArray());
        }
        if (this.seasonal.include()) {
            int seasonalFrequency = (int) this.response.timePeriod().frequencyPer(this.seasonalCycle);
            int periodOffset = 0;
            double[][] seasonalRegressors = TimeSeriesLinearRegressionModel.getSeasonalRegressors(this.response.size(), seasonalFrequency,
                                                                                                  periodOffset);
            this.timeBasedPredictors(seasonalRegressors);
        }
        return new TimeSeriesLinearRegressionModel(this);
    }


    double[][] timeBasedPredictors() {
        return timeBasedPredictors;
    }

    double[][] externalRegressors() {
        return externalRegressors;
    }

    TimeSeries response() {
        return response;
    }

    TimeSeriesLinearRegression.Intercept intercept() {
        return intercept;
    }

    TimeSeriesLinearRegression.TimeTrend timeTrend() {
        return timeTrend;
    }

    TimeSeriesLinearRegression.Seasonal seasonal() {
        return seasonal;
    }

    TimePeriod seasonalCycle() {
        return seasonalCycle;
    }
}
