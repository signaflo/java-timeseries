/*
 * Copyright (c) 2016 Jacob Rachiele
 */

package timeseries.models;

import java.time.OffsetDateTime;

import data.DoubleFunctions;
import timeseries.TimeScale;
import timeseries.TimeSeries;

/**
 * This time series model assumes that the series contains no underlying trend or seasonality, and that early values of
 * the series provide as much information as recent values.
 * 
 * @author Jacob Rachiele
 *
 */
public final class MeanModel {

  private final TimeSeries timeSeries;
  private final TimeSeries fittedSeries;
  private final double mean;

  public MeanModel(final TimeSeries observed) {
    this.timeSeries = observed.copy();
    this.mean = this.timeSeries.mean();
    this.fittedSeries = new TimeSeries(observed.timeScale(), observed.observationTimes().get(0),
        observed.periodLength(), DoubleFunctions.fill(observed.n(), this.mean));
  }

  public final TimeSeries forecast(final int steps) {
    int n = timeSeries.n();
    long periodLength = timeSeries.periodLength();
    TimeScale timeScale = timeSeries.timeScale();

    final double[] forecasted = DoubleFunctions.fill(steps, this.mean);
    final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
        .plus(periodLength * timeScale.periodLength(), timeScale.timeUnit());
    return new TimeSeries(timeScale, startTime, periodLength, forecasted);
  }

  public final TimeSeries fittedSeries() {
    return this.fittedSeries;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("timeSeries: ").append(timeSeries).append("\nfittedSeries: ").append(fittedSeries).append("\nmean: ")
        .append(mean);
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fittedSeries == null) ? 0 : fittedSeries.hashCode());
    long temp;
    temp = Double.doubleToLongBits(mean);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((timeSeries == null) ? 0 : timeSeries.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    MeanModel other = (MeanModel) obj;
    if (fittedSeries == null) {
      if (other.fittedSeries != null) {
        return false;
      }
    } else if (!fittedSeries.equals(other.fittedSeries)) {
      return false;
    }
    if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean)) {
      return false;
    }
    if (timeSeries == null) {
      if (other.timeSeries != null) {
        return false;
      }
    } else if (!timeSeries.equals(other.timeSeries)) {
      return false;
    }
    return true;
  }

}
