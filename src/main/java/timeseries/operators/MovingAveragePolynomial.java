package timeseries.operators;

import timeseries.TimeSeries;

public final class MovingAveragePolynomial extends LagPolynomial {
  
  public MovingAveragePolynomial(final double... parameters) {
    super(parameters);
  }

  @Override
  public double applyInverse(final TimeSeries timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value += parameters[i] * LagOperator.apply(timeSeries, index, i + 1);
    }
    return value;
  }
  
  @Override
  public double applyInverse(final double[] timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value += parameters[i] * LagOperator.apply(timeSeries, index, i + 1);
    }
    return value;
  }
}
