package timeseries.model.regression;

import data.regression.MultipleLinearRegression;
import timeseries.TimePeriod;
import timeseries.TimeSeries;

/**
 * A linear regression model for time series data.
 *
 * @author Jacob Rachiele
 * Aug. 01, 2017
 */
public interface TimeSeriesLinearRegression extends MultipleLinearRegression {

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
