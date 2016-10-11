package timeseries.operators;

import timeseries.TimeSeries;

/**
 * Represents a moving average polynomial in the lag operator. 
 * See <a target="_blank" href="https://goo.gl/1eLYnF"> Harvey's
 * Forecasting, structural time series models and the Kalman filter</a>, (1989, equation 2.1.3), or
 * <a target="_blank" href="https://en.wikipedia.org/wiki/Lag_operator#Lag_polynomials"> the wiki entry</a>. The
 * polynomial is taken in the lag operator, but is algebraically equivalent to a real or complex polynomial.
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
public final class MovingAveragePolynomial extends LagPolynomial {
  
  /**
   * Create a new moving average polynomial with the given parameters.
   * @param parameters the moving average parameters of the polynomial. 
   */
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
