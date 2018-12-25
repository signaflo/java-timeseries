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
package com.github.signaflo.timeseries.model.arima;

import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;

/**
 * A pointForecast for an ARIMA model. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
final class ArimaForecast implements Forecast {

    private final TimeSeries pointForecast;
    private final TimeSeries lowerValues;
    private final TimeSeries upperValues;
    private final double alpha;

    ArimaForecast(TimeSeries pointForecast, TimeSeries lowerValues, TimeSeries upperValues, double alpha) {
        this.pointForecast = pointForecast;
        this.lowerValues = lowerValues;
        this.upperValues = upperValues;
        this.alpha = alpha;
    }

    @Override
    public TimeSeries pointEstimates() {
        return this.pointForecast;
    }

    @Override
    public TimeSeries upperPredictionInterval() {
        return this.upperValues;
    }

    @Override
    public TimeSeries lowerPredictionInterval() {
        return this.lowerValues;
    }

    @Override
    public String toString() {
        final String newLine = System.lineSeparator();
        StringBuilder builder = new StringBuilder(newLine);
        builder.append(String.format("%-18.18s", "| Date "))
               .append("  ")
               .append(String.format("%-13.13s", "| Forecast "))
               .append("  ")
               .append(String.format("%-13.13s", "| Lower " + String.format("%.1f", (1 - alpha) * 100) + "%"))
               .append("  ")
               .append(String.format("%-13.13s", "| Upper " + String.format("%.1f", (1 - alpha) * 100) + "%"))
               .append(" |")
               .append(newLine)
               .append(String.format("%-70.70s", " -------------------------------------------------------------- "))
               .append(newLine);
        for (int i = 0; i < this.pointForecast.size(); i++) {
            builder.append(String.format("%-18.18s", "| " + pointForecast.observationTimes().get(i)))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " +  Double.toString(pointForecast.at(i))))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " + Double.toString(this.lowerValues.at(i))))
                   .append("  ")
                   .append(String.format("%-13.13s", "| " + Double.toString(this.upperValues.at(i))))
                   .append(" |")
                   .append(newLine);
        }
        return builder.toString();
    }
}
