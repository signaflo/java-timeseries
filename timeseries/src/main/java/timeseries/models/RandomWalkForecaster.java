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

package timeseries.models;

import math.stats.distributions.Normal;
import timeseries.TimeSeries;

public class RandomWalkForecaster implements Forecaster {

    private final Model model;

    public RandomWalkForecaster(final Model model) {
        this.model = model;
    }

    public RandomWalkForecaster(final TimeSeries series) {
        this.model = new RandomWalk(series);
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        double[] upperPredictionValues = new double[steps];
        double criticalValue = new Normal(0, model.predictionErrors().stdDeviation()).quantile(1 - alpha / 2);
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) + criticalValue * Math.sqrt(t + 1);
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        double[] upperPredictionValues = new double[steps];
        double criticalValue = new Normal(0, model.predictionErrors().stdDeviation()).quantile(1 - alpha / 2);
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) - criticalValue * Math.sqrt(t + 1);
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computePointForecasts(int steps) {
        return this.model.pointForecast(steps);
    }

    @Override
    public RandomWalkForecast forecast(int steps, double alpha) {
        TimeSeries forecast = computePointForecasts(steps);
        TimeSeries lowerBounds = computeLowerPredictionBounds(forecast, steps, alpha);
        TimeSeries upperBounds = computeUpperPredictionBounds(forecast, steps, alpha);
        return new RandomWalkForecast(forecast, lowerBounds, upperBounds);
    }
}
