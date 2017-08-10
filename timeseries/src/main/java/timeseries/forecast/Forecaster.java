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

package timeseries.forecast;

import timeseries.TimeSeries;

/**
 * A time series forecaster.
 */
public interface Forecaster {

    /**
     * Compute the upper end points of a prediction interval using the given forecast, forecast steps,
     * and &alpha; significance level.
     *
     * @param forecast the forecasted values.
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction interval.
     * @return the upper end points of a prediction interval using the given forecast, forecast steps,
     * and &alpha; significance level.
     */
    TimeSeries computeUpperPredictionBounds(TimeSeries forecast, int steps, double alpha);

    /**
     * Compute the lower end points of a prediction interval using the given forecast, forecast steps,
     * and &alpha; significance level.
     *
     *
     * @param forecast the forecasted values.
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction interval.
     * @return the lower end points of a prediction interval using the given forecast, forecast steps,
     * and &alpha; significance level.
     */
    TimeSeries computeLowerPredictionBounds(TimeSeries forecast, int steps, double alpha);
    /**
     * Compute the lower end points of a prediction interval with the given number of forecast steps and
     * &alpha; significance level.
     *
     *
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction interval.
     * @return the lower end points of a prediction interval with the given number of forecast steps and
     * &alpha; significance level.
     */
    default TimeSeries computeLowerPredictionBounds(int steps, double alpha) {
        TimeSeries forecast = this.computePointForecasts(steps);
        return computeLowerPredictionBounds(forecast, steps, alpha);
    }

    /**
     * Compute the upper end points of a prediction interval with the given number of forecast steps and
     * &alpha; significance level.
     *
     *
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction interval.
     * @return the upper end points of a prediction interval with the given number of forecast steps and
     * &alpha; significance level.
     */
    default TimeSeries computeUpperPredictionBounds(int steps, double alpha) {
        TimeSeries forecast = this.computePointForecasts(steps);
        return computeUpperPredictionBounds(forecast, steps, alpha);
    }

    /**
     * Create a new point forecast for the given number of steps ahead.
     *
     * @param steps the number of time periods ahead to forecast.
     * @return a new point forecast for the given number of steps ahead.
     */
    TimeSeries computePointForecasts(int steps);

    /**
     * Create a new forecast for the given number of steps ahead with the given &alpha; significance level.
     *
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction interval.
     * @return a new forecast for the given number of steps ahead with the given &alpha; significance level.
     */
    Forecast forecast(int steps, double alpha);

    /**
     * Create a new forecast for the given number of steps ahead with a default &alpha; significance level of 0.05.
     *
     * @param steps the number of time periods ahead to forecast.
     * @return a new forecast for the given number of steps ahead with a default &alpha; significance level of 0.05.
     */
    default Forecast forecast(int steps) {
        return forecast(steps, 0.05);
    }
}
