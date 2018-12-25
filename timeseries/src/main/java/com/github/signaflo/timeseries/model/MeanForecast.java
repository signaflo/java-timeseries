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
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A mean model forecast.
 *
 * @author Jacob Rachiele
 */
@EqualsAndHashCode
@ToString
final class MeanForecast implements Forecast {

  private final TimeSeries forecast;
  private final TimeSeries upperValues;
  private final TimeSeries lowerValues;

  MeanForecast(TimeSeries pointForecast, TimeSeries lowerValues, TimeSeries upperValues) {
    this.forecast = pointForecast;
    this.lowerValues = lowerValues;
    this.upperValues = upperValues;
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
