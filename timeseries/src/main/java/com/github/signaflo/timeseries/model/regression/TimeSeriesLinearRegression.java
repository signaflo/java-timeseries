package com.github.signaflo.timeseries.model.regression;

import com.github.signaflo.data.regression.MultipleLinearRegression;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.model.Model;

/**
 * A linear regression model for time series com.github.signaflo.data.
 *
 * @author Jacob Rachiele
 * Aug. 01, 2017
 */
public interface TimeSeriesLinearRegression extends MultipleLinearRegression, Model {

    /**
     * Return a new builder for a time series linear regression model.
     *
     * @return a new builder for a time series linear regression model.
     */
    static TimeSeriesLinearRegressionBuilder builder() {
        return new TimeSeriesLinearRegressionBuilder();
    }

    TimeSeries timeSeriesResponse();

    Intercept intercept();

    TimeTrend timeTrend();

    Seasonal seasonal();

    TimePeriod seasonalCycle();

    int seasonalFrequency();

    /**
     * Specifies whether a time series regression model has an intercept.
     */
    enum Intercept {
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
     * Specifies whether a time series regression model has a time trend.
     */
    enum TimeTrend {
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
     * Specifies whether a time series regression model has a seasonal component.
     */
    enum Seasonal {
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
}
