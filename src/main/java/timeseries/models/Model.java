/*
 * Copyright (c) 2016 Jacob Rachiele
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
package timeseries.models;

import timeseries.TimeSeries;

/**
 * A time series model. An attempt to capture the most important characteristics of the underlying process(es).
 *
 * @author Jacob Rachiele
 */
public interface Model {

    /**
     * Produce a time series of point forecasts from this model up to the given number of steps ahead.
     * <p>
     * To obtain additional information about the forecast, such as prediction intervals, use the {@link #forecast}
     * method and the resulting {@link Forecast} object.
     * </p>
     *
     * @param steps the number of time periods ahead to forecast.
     * @return a time series of point forecasts from this model up to the given number of steps ahead.
     */
    TimeSeries pointForecast(int steps);

    /**
     * Produce a new forecast up to the given number of steps with the given &alpha; significance level for
     * computing prediction intervals.
     *
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the probability that a future observation will fall outside the associated (1 - &alpha;)100%
     *              prediction interval, given that the model is "correct". Note that the correctness of the model
     *              often comes with a high degree of uncertainty and this should be taken into account when making
     *              decisions. In other words, the provided prediction intervals will often be overly optimistic.
     * @return a new forecast up to the given number of steps ahead with the given significance level.
     */
    Forecast forecast(int steps, double alpha);

    /**
     * Produce a new forecast up to the given number of steps with an &alpha; significance level of 0.05 for
     * computing prediction intervals.
     *
     * @param steps the number of time periods ahead to forecast.
     * @return a new forecast up to the given number of steps ahead with a 0.05 &alpha; significance level.
     */
    default Forecast forecast(int steps) {
        return forecast(steps, 0.05);
    }

    /**
     * Get the series of observations.
     *
     * @return the series of observations.
     */
    TimeSeries timeSeries();

    /**
     * Get the model fitted values, which are in-sample one-step ahead forecasts.
     *
     * @return the model fitted values.
     */
    TimeSeries fittedSeries();

    /**
     * Get the model residuals, the difference between the observed values and the model fitted values.
     *
     * @return the model residuals.
     */
    TimeSeries residuals();

    /**
     * Plot the model fit, which often displays the model fitted values and the observations in the same plot area.
     */
    void plotFit();

    /**
     * Plot the model residuals.
     */
    void plotResiduals();

}