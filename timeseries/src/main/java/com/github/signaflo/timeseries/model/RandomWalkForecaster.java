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

package com.github.signaflo.timeseries.model;

import com.github.signaflo.math.stats.distributions.Normal;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.forecast.Forecaster;

import java.time.OffsetDateTime;

class RandomWalkForecaster implements Forecaster {

    private final TimeSeries timeSeries;
    private final TimeSeries predictionErrors;

    RandomWalkForecaster(TimeSeries observations, TimeSeries predictionErrors) {
        this.timeSeries = observations;
        this.predictionErrors = predictionErrors;
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        double[] upperPredictionValues = new double[steps];
        double criticalValue = new Normal(0, this.predictionErrors.stdDeviation()).quantile(1 - alpha / 2);
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) + criticalValue * Math.sqrt(t + 1);
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        double[] upperPredictionValues = new double[steps];
        double criticalValue = new Normal(0, this.predictionErrors.stdDeviation())
                .quantile(1 - alpha / 2);
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) - criticalValue * Math.sqrt(t + 1);
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computePointForecasts(int steps) {
        int n = timeSeries.size();
        TimePeriod timePeriod = timeSeries.timePeriod();
        long amountToAdd = timePeriod.periodLength() * timePeriod.timeUnit().unitLength();
        final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
                                                   .plus(amountToAdd, timePeriod.timeUnit().temporalUnit());
        double[] forecast = new double[steps];
        for (int t = 0; t < steps; t++) {
            forecast[t] = timeSeries.at(n - 1);
        }
        return TimeSeries.from(timePeriod, startTime, forecast);
    }

//    @Override
//    public Forecast forecast(int steps) {
//        return forecast(steps, 0.05);
//    }

    @Override
    public Forecast forecast(int steps, double alpha) {
        TimeSeries forecast = computePointForecasts(steps);
        TimeSeries lowerBounds = computeLowerPredictionBounds(forecast, steps, alpha);
        TimeSeries upperBounds = computeUpperPredictionBounds(forecast, steps, alpha);
        return new RandomWalkForecast(forecast, lowerBounds, upperBounds);
    }
}
