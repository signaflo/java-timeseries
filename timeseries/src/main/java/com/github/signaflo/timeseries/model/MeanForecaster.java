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

import com.github.signaflo.math.operations.DoubleFunctions;
import com.github.signaflo.math.stats.distributions.StudentsT;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecaster;

import java.time.OffsetDateTime;

class MeanForecaster implements Forecaster {

    private final TimeSeries timeSeries;

    MeanForecaster(TimeSeries timeSeries) {
        this.timeSeries = timeSeries;
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        if (steps < 1) {
            throw new IllegalArgumentException("The number of steps ahead to forecast must be greater" +
                                               " than or equal to 1, but was " + steps);
        }
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("The value of alpha must be between 0 and 1, but was " + alpha);
        }
        TimeSeries fcstStdError = getFcstErrors(forecast, steps, alpha);
        double[] upperPredictionValues = new double[steps];
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) + fcstStdError.at(t);
        }
        return TimeSeries.from(forecast.samplingInterval(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        if (steps < 1) {
            throw new IllegalArgumentException("The number of steps ahead to forecast must be greater" +
                                               " than or equal to 1, but was " + steps);
        }
        if (alpha < 0 || alpha > 1) {
            throw new IllegalArgumentException("The value of alpha must be between 0 and 1, but was " + alpha);
        }
        double[] lowerPredictionValues = new double[steps];
        TimeSeries fcstStdError = getFcstErrors(forecast, steps, alpha);
        for (int t = 0; t < steps; t++) {
            lowerPredictionValues[t] = forecast.at(t) - fcstStdError.at(t);
        }
        return TimeSeries.from(forecast.samplingInterval(), forecast.observationTimes().get(0), lowerPredictionValues);
    }

    private TimeSeries getFcstErrors(TimeSeries forecast, int steps, double alpha) {
        double[] errors = new double[steps];
        double criticalValue = new StudentsT(timeSeries.size() - 1).quantile(1 - alpha / 2);
        double variance = timeSeries.variance();
        double meanStdError = variance / timeSeries.size();
        double fcstStdError = Math.sqrt(variance + meanStdError);
        for (int t = 0; t < errors.length; t++) {
            errors[t] = criticalValue * fcstStdError;
        }
        return TimeSeries.from(forecast.samplingInterval(), forecast.observationTimes().get(0), errors);
    }

    @Override
    public TimeSeries computePointForecasts(int steps) {
        int n = timeSeries.size();
        TimePeriod timePeriod = timeSeries.samplingInterval();

        final double[] forecasted = DoubleFunctions.fill(steps, timeSeries.mean());
        final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
                                                   .plus(timePeriod.periodLength() * timePeriod.timeUnit().unitLength(),
                                                         timePeriod.timeUnit().temporalUnit());
        return TimeSeries.from(timePeriod, startTime, forecasted);
    }

    @Override
    public MeanForecast forecast(int steps, double alpha) {
        TimeSeries forecast = computePointForecasts(steps);
        TimeSeries lowerBounds = computeLowerPredictionBounds(forecast, steps, alpha);
        TimeSeries upperBounds = computeUpperPredictionBounds(forecast, steps, alpha);
        return new MeanForecast(forecast, lowerBounds, upperBounds);
    }
}
