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
package com.github.signaflo.timeseries.model;

import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;

/**
 * A forecast from a random walk model.
 *
 * @author Jacob Rachiele
 */
final class RandomWalkForecast implements Forecast {

    private final TimeSeries forecast;
    private final TimeSeries upperValues;
    private final TimeSeries lowerValues;

    RandomWalkForecast(TimeSeries forecast, TimeSeries lowerValues, TimeSeries upperValues) {
        this.forecast = forecast;
        this.upperValues = upperValues;
        this.lowerValues = lowerValues;
    }

    @Override
    public TimeSeries pointEstimates() {
        return this.forecast;
    }

    @Override
    public TimeSeries upperPredictionInterval() {
        return this.upperValues;
    }

    @Override
    public TimeSeries lowerPredictionInterval() {
        return this.lowerValues;
    }

}
