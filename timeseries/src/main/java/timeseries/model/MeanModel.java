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
package timeseries.model;

import lombok.NonNull;
import math.operations.DoubleFunctions;
import timeseries.TimeSeries;
import timeseries.forecast.Forecast;

/**
 * A time series model that assumes no trend or seasonal factors are present, and that puts as much weight
 * on early values of the series as it does on recent values.
 *
 * @author Jacob Rachiele
 */
public final class MeanModel implements Model {

    private final TimeSeries timeSeries;
    private final TimeSeries fittedSeries;
    private final double mean;

    public MeanModel(@NonNull final TimeSeries observed) {
        this.timeSeries = observed;
        this.mean = this.timeSeries.mean();
        this.fittedSeries = TimeSeries.from(observed.timePeriod(), observed.observationTimes().get(0),
                                            DoubleFunctions.fill(observed.size(), this.mean));
    }

    @Override
    public Forecast forecast(final int steps, final double alpha) {
        MeanForecaster forecaster = new MeanForecaster(this.timeSeries);
        return forecaster.forecast(steps, alpha);
    }

    @Override
    public TimeSeries observations() {
        return this.timeSeries;
    }

    @Override
    public TimeSeries fittedSeries() {
        return this.fittedSeries;
    }

    @Override
    public TimeSeries predictionErrors() {
        return this.timeSeries.minus(this.fittedSeries);
    }

    @Override
    public String toString() {
        return "Mean model with estimated mean of " + mean;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeanModel meanModel = (MeanModel) o;

        if (Double.compare(meanModel.mean, mean) != 0) return false;
        if (!timeSeries.equals(meanModel.timeSeries)) return false;
        return fittedSeries.equals(meanModel.fittedSeries);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = timeSeries.hashCode();
        result = 31 * result + fittedSeries.hashCode();
        temp = Double.doubleToLongBits(mean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
