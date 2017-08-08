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

package timeseries.models.regression;

import data.regression.LinearRegressionPrediction;
import data.regression.LinearRegressionPredictor;
import data.regression.MultipleLinearRegressionPredictor;
import math.linear.doubles.Matrix;
import timeseries.TimeSeries;
import timeseries.models.Forecaster;

import java.util.List;

public class TimeSeriesRegressionForecaster implements Forecaster {

    private final TimeSeriesLinearRegressionModel model;
    private final MultipleLinearRegressionPredictor predictor;

    TimeSeriesRegressionForecaster(TimeSeriesLinearRegressionModel regression) {
        this.model = regression;
        this.predictor = new MultipleLinearRegressionPredictor(this.model);
    }

    @Override
    public TimeSeries computePointForecasts(int steps) {
        return model.pointForecast(steps);
    }

    @Override
    public TimeSeriesRegressionForecast forecast(int steps, double alpha) {
        final TimeSeries forecast = computePointForecasts(steps);
        Matrix predictionMatrix = this.model.getPredictionMatrix(steps);
        List<LinearRegressionPrediction> predictions = this.predictor.predictWithIntercept(predictionMatrix, alpha);
        final TimeSeries lowerBounds = computeLowerPredictionBounds(predictions, forecast, steps);
        final TimeSeries upperBounds = computeUpperPredictionBounds(predictions, forecast, steps);
        return new TimeSeriesRegressionForecast(forecast, lowerBounds, upperBounds);
    }

    TimeSeries computeLowerPredictionBounds(List<LinearRegressionPrediction> predictions,
                                            TimeSeries forecast, int steps) {
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().first();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

    TimeSeries computeUpperPredictionBounds(List<LinearRegressionPrediction> predictions,
                                            TimeSeries forecast, int steps) {
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().second();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, int steps, double alpha) {
        Matrix predictionMatrix = this.model.getPredictionMatrix(steps);
        List<LinearRegressionPrediction> predictions = this.predictor.predictWithIntercept(predictionMatrix, alpha);
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().first();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(TimeSeries forecast, int steps, double alpha) {
        Matrix predictionMatrix = this.model.getPredictionMatrix(steps);
        List<LinearRegressionPrediction> predictions = this.predictor.predictWithIntercept(predictionMatrix, alpha);
        double[] bounds = new double[steps];
        for (int i = 0; i < steps; i++) {
            bounds[i] = predictions.get(i).predictionInterval().second();
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.startTime(), bounds);
    }

}
