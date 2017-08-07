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
import data.regression.MultiValuePrediction;
import math.operations.DoubleFunctions;
import data.Range;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.Forecast;

import java.time.OffsetDateTime;
import java.util.List;

@EqualsAndHashCode @ToString
public class TimeSeriesRegressionForecast implements Forecast {

    private final TimeSeriesLinearRegression model;
    private final TimeSeries observations;
    private final OffsetDateTime startTime;
    private final List<LinearRegressionPrediction> predictions;

    private TimeSeriesRegressionForecast(TimeSeriesLinearRegression model, int steps) {
        this.model = model;
        this.observations = model.observations();
        this.startTime = observations.startTime();
    }

    public static TimeSeriesRegressionForecast forecast(TimeSeriesLinearRegression model, int steps) {
        return new TimeSeriesRegressionForecast(model, steps);
    }

    @Override
    public TimeSeries upperPredictionInterval() {
        return null;
    }

    @Override
    public TimeSeries lowerPredictionInterval() {
        return null;
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(int steps, double alpha) {
        return null;
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(int steps, double alpha) {
        return null;
    }

    @Override
    public TimeSeries forecast() {
        return null;
    }

    @Override
    public void plot() {

    }

    @Override
    public void plotForecast() {

    }
}
