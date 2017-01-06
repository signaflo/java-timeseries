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
package timeseries.operators;

import timeseries.TimeSeries;

/**
 * Represents a moving average polynomial in the lag operator.
 * See <a target="_blank" href="https://goo.gl/1eLYnF"> Harvey's
 * Forecasting, structural time series models and the Kalman filter</a>, (1989, equation 2.1.3), or
 * <a target="_blank" href="https://en.wikipedia.org/wiki/Lag_operator#Lag_polynomials"> the wiki entry</a>. The
 * polynomial is taken in the lag operator, but is algebraically equivalent to a real or complex polynomial.
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
  public double fit(final TimeSeries residualSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value += parameters[i] * LagOperator.apply(residualSeries, index, i + 1);
    }
    return value;
  }
  
  @Override
  public double fit(final double[] residualSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value += parameters[i] * LagOperator.apply(residualSeries, index, i + 1);
    }
    return value;
  }
}
