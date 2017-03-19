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
 * A forecast produced by a time series {@link Model}.
 *
 * @author Jacob Rachiele
 */
public interface Forecast {

    /**
     * Get the upper end points of the prediction interval.
     *
     * @return the upper end points of the prediction interval.
     */
    TimeSeries upperPredictionValues();

    /**
     * Get the lower end points of the prediction interval.
     *
     * @return the lower end points of the prediction interval.
     */
    TimeSeries lowerPredictionValues();

    /**
     * Compute the upper end points of a prediction interval with the given number of forecast steps and the provided
     * &alpha; significance level.
     *
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction intervals.
     * @return the upper end points of a prediction interval with the given number of forecast steps and the provided
     * &alpha; significance level.
     */
    TimeSeries computeUpperPredictionBounds(int steps, double alpha);

    /**
     * Compute the lower end points of a prediction interval with the given number of forecast steps and the provided
     * &alpha; significance level.
     *
     * @param steps the number of time periods ahead to forecast.
     * @param alpha the significance level for the prediction intervals.
     * @return the lower end points of a prediction interval with the given number of forecast steps and the provided
     * &alpha; significance level.
     */
    TimeSeries computeLowerPredictionBounds(int steps, double alpha);

    /**
     * Get the point forecasts.
     *
     * @return the point forecasts.
     */
    TimeSeries forecast();

    /**
     * Plot the forecast values along with the historical data and prediction interval.
     */
    void plot();

    /**
     * Plot only the forecast values and the corresponding prediction interval.
     */
    void plotForecast();

}