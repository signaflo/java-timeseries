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

package com.github.signaflo.timeseries.model.regression;

import com.github.signaflo.data.regression.LinearRegressionPrediction;
import com.github.signaflo.data.regression.MultipleLinearRegressionPredictor;
import com.github.signaflo.math.linear.doubles.Matrix;
import com.github.signaflo.math.linear.doubles.Vector;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecaster;

import java.time.OffsetDateTime;
import java.util.List;

class TimeSeriesRegressionForecaster implements Forecaster {

    private final TimeSeries timeSeries;
    private final MultipleLinearRegressionPredictor predictor;
    private final Vector beta;
    private final Matrix predictionMatrix;

    TimeSeriesRegressionForecaster(TimeSeries timeSeries, MultipleLinearRegressionPredictor predictor,
                                   Vector beta, Matrix predictionMatrix) {
        this.timeSeries = timeSeries;
        this.predictor = predictor;
        this.beta = beta;
        this.predictionMatrix = predictionMatrix;
    }

    @Override
    public TimeSeries computePointForecasts(int steps) {
        double[] forecasts = predictionMatrix.times(beta).elements();
        TimePeriod timePeriod = timeSeries.timePeriod();
        OffsetDateTime sampleEnd = this.timeSeries.observationTimes().get(timeSeries.size() - 1);
        OffsetDateTime startTime = sampleEnd.plus(timePeriod.unitLength(), timePeriod.timeUnit().temporalUnit());
        return TimeSeries.from(timePeriod, startTime, forecasts);
    }

    @Override
    public TimeSeriesRegressionForecast forecast(int steps, double alpha) {
        final TimeSeries forecast = computePointForecasts(steps);
        List<LinearRegressionPrediction> predictions = this.predictor.predictDesignMatrix(predictionMatrix, alpha);
        final TimeSeries lowerBounds = computeLowerPredictionBounds(predictions, forecast, steps);
        final TimeSeries upperBounds = computeUpperPredictionBounds(predictions, forecast, steps);
        return new TimeSeriesRegressionForecast(forecast, lowerBounds, upperBounds);
    }

    private TimeSeries computeLowerPredictionBounds(List<LinearRegressionPrediction> predictions,
                                            TimeSeries forecast, int steps) {
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().first();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

    private TimeSeries computeUpperPredictionBounds(List<LinearRegressionPrediction> predictions,
                                            TimeSeries forecast, int steps) {
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().second();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, int steps, double alpha) {
        List<LinearRegressionPrediction> predictions = this.predictor.predictDesignMatrix(predictionMatrix, alpha);
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().first();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(TimeSeries forecast, int steps, double alpha) {
        List<LinearRegressionPrediction> predictions = this.predictor.predictDesignMatrix(predictionMatrix, alpha);
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().second();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

}
