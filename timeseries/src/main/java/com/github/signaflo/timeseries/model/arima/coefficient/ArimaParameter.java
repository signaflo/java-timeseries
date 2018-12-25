package com.github.signaflo.timeseries.model.arima.coefficient;

import com.github.signaflo.timeseries.model.Parameter;

public class ArimaParameter implements Parameter {

  private double value = 0.0;
  private double uncertainty = Double.POSITIVE_INFINITY;

  ArimaParameter() {}

  ArimaParameter(double value) {
    this.value = value;
  }

  ArimaParameter(double value, double uncertainty) {
    this.value = value;
    this.uncertainty = uncertainty;
  }

  @Override
  public double getValue() {
    return this.value;
  }

  @Override
  public double getUncertainty() {
    return this.uncertainty;
  }
}
