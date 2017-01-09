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

import java.time.OffsetDateTime;
import java.util.Arrays;

import data.DoubleFunctions;
import timeseries.TimeSeries;

/**
 * Represents a polynomial in the lag operator. See <a target="_blank" href="https://goo.gl/1eLYnF"> Harvey's
 * Forecasting, structural time series models and the Kalman filter</a>, (1989, equation 2.1.3), or
 * <a target="_blank" href="https://en.wikipedia.org/wiki/Lag_operator#Lag_polynomials"> the wiki entry</a>. The
 * polynomial is taken in the lag operator, but is algebraically equivalent to a real or complex polynomial.
 * 
 * @author jrachiele
 *
 */
public class LagPolynomial {

  final double[] parameters;
  final double[] coefficients;
  final int degree;

  /**
   * Construct a new lag polynomial from the given parameters. Note that the parameters given here are not the same as
   * the coefficients of the polynomial since the coefficient at the zero-degreee term is always equal to 1.
   * 
   * @param parameters the parameters of the polynomial.
   */
  LagPolynomial(final double... parameters) {
    this.parameters = parameters.clone();
    this.coefficients = new double[parameters.length + 1];
    this.coefficients[0] = 1.0;
    System.arraycopy(parameters, 0, this.coefficients, 1, parameters.length);
    this.degree = parameters.length;
  }

  /**
   * Create and return a new lag polynomial representing the first difference operator.
   * 
   * @return a new lag polynomial representing the first difference operator.
   */
  public static LagPolynomial firstDifference() {
    return new LagPolynomial(-1.0);
  }

  /**
   * Create and return a new lag polynomial representing the first seasonal difference operator.
   * 
   * @param seasonalLag the period of seasonality, which is the lag to use for the operator.
   * @return a new lag polynomial representing the first seasonal difference operator.
   */
  public static LagPolynomial firstSeasonalDifference(final int seasonalLag) {
    double[] poly = new double[seasonalLag];
    poly[seasonalLag - 1] = -1.0;
    return new LagPolynomial(poly);
  }

  /**
   * Create and return a new lag polynomial representing an arbitrary number of seasonal differences.
   * 
   * @param seasonalLag the period of seasoanlit, which is the lag to use for the operator.
   * @param D the number of seasonal differences.
   * @return a new lag polynomial representing an arbitrary number of seasonal differences.
   */
  public static LagPolynomial seasonalDifferences(final int seasonalLag, final int D) {
    if (D < 0) {
      throw new RuntimeException("The degree of differencing must be greater than or equal to 0, but was " + D);
    }
    if (D > 0) {
      LagPolynomial diff = LagPolynomial.firstSeasonalDifference(seasonalLag);
      for (int i = 1; i < D; i++) {
        diff = diff.times(diff);
      }
      return diff;
    } else {
      return new LagPolynomial();
    }
  }

  /**
   * Create and return a new lag polynomial representing an arbitrary number of differences.
   * 
   * @param d the number of differences. An integer greater than or equal to 0.
   * @return a new lag polynomial representing an arbitrary number of differences.
   */
  public static LagPolynomial differences(final int d) {
    if (d < 0) {
      throw new RuntimeException("The degree of differencing must be greater than or equal to 0, but was " + d);
    }
    if (d > 0) {
      LagPolynomial diff = LagPolynomial.firstDifference();
      for (int i = 1; i < d; i++) {
        diff = diff.times(diff);
      }
      return diff;
    } else {
      return new LagPolynomial();
    }
  }

  /**
   * Create and return a new moving average lag polynomial.
   * 
   * @param parameters the moving average parameters of an ARIMA model.
   * @return a new moving average lag polynomial.
   */
  public static LagPolynomial movingAverage(double... parameters) {
    return new MovingAveragePolynomial(parameters);
  }

  /**
   * Create and return a new autoregressive lag polynomial.
   * 
   * @param parameters the autoregressive parameters of an ARIMA model.
   * @return a new autoregressive lag polynomial.
   */
  public static LagPolynomial autoRegressive(double... parameters) {
    final double[] inverseParams = new double[parameters.length];
    for (int i = 0; i < inverseParams.length; i++) {
      inverseParams[i] = -parameters[i];
    }
    return new LagPolynomial(inverseParams);
  }

  /**
   * Multiply this polynomial by another lag polynomial and return the result in a new lag polynomial.
   * 
   * @param other the polynomial to multiply this one with.
   * @return the product of this polynomial with the given polynomial.
   */
  public final LagPolynomial times(final LagPolynomial other) {
    final double[] newParams = new double[this.degree + other.degree + 1];
    for (int i = 0; i < coefficients.length; i++) {
      for (int j = 0; j < other.coefficients.length; j++) {
        newParams[i + j] += coefficients[i] * other.coefficients[j];
      }
    }
    return new LagPolynomial(DoubleFunctions.slice(newParams, 1, newParams.length));
  }

  /**
   * Apply this lag polynomial to a time series at the given index.
   * 
   * @param timeSeries the time series containing the index to apply this lag polynomial to.
   * @param index the index of the series to apply the lag polynomial at.
   * @return the result of applying this lag polynomial to the given time series at the given index.
   */
  public final double apply(final TimeSeries timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < coefficients.length; i++) {
      value += this.coefficients[i] * LagOperator.apply(timeSeries, index, i);
    }
    return value;
  }

  /**
   * Apply this lag polynomial to a time series at the given date and time.
   * 
   * @param timeSeries the time series containing the index to apply this lag polynomial to.
   * @param dateTime the date and time of the series to apply the lag polynomial at.
   * @return the result of applying this lag polynomial to the given time series at the given date and time.
   */
  public final double apply(final TimeSeries timeSeries, final OffsetDateTime dateTime) {
    double value = 0.0;
    for (int i = 0; i < coefficients.length; i++) {
      value += this.coefficients[i] * LagOperator.apply(timeSeries, dateTime, i);
    }
    return value;
  }

  /**
   * Use this lag polynomial to fit the implied ARIMA model at the given index by applying the polynomial, then
   * solving for the time series value at that index.
   *
   * @param timeSeries the time series containing the index to apply the lag polynomial to.
   * @param index the index of the series to apply the lag polynomial at.
   * @return the result of applying this lag polynomial to the time series at the given index, then solving
   * for the value of the series at that index.
   */
  public double fit(final TimeSeries timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value -= parameters[i] * LagOperator.apply(timeSeries, index, i + 1);
    }
    return value;
  }

  /**
   * Use this lag polynomial to fit the implied ARIMA model at the given date and time by applying the polynomial, then
   * solving for the value of the series at that date and time.
   *
   * @param timeSeries the time series containing the date and time to apply the lag polynomial to.
   * @param dateTime the date and time of the series to apply the lag polynomial at.
   * @return the result of applying this lag polynomial to the time series at the given date and time, then solving
   * for the value of the series at that date and time.
   */
  public double fit(final TimeSeries timeSeries, OffsetDateTime dateTime) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value -= parameters[i] * LagOperator.apply(timeSeries, dateTime, i + 1);
    }
    return value;
  }

  /**
   * Use this lag polynomial to fit the implied ARIMA model at the given index by applying the polynomial, then
   * solving for the time series value at that index.
   * 
   * @param timeSeries the time series containing the index to apply the lag polynomial to.
   * @param index the index of the series to apply the lag polynomial at.
   * @return the result of applying this lag polynomial to the time series at the given index, then solving
   * for the value of the series at that index.
   */
  public double fit(final double[] timeSeries, final int index) {
    double value = 0.0;
    for (int i = 0; i < parameters.length; i++) {
      value -= parameters[i] * LagOperator.apply(timeSeries, index, i + 1);
    }
    return value;
  }

  /**
   * Return the parameters of this lag polynomial.
   * @return the parameters of this lag polynomial.
   */
  public final double[] parameters() {
    return this.parameters.clone();
  }

  /**
   * Return the additive inverse of the parameters of this  lag polynomial.
   * @return the additive inverse of the parameters of this  lag polynomial.
   */
  public final double[] inverseParams() {
    final double[] invParams = new double[parameters.length];
    for (int i = 0; i < invParams.length; i++) {
      invParams[i] = -parameters[i];
    }
    return invParams;
  }

  /**
   * Return the coefficients of this lag polynomial, which includes the constant term 1.
   * @return the coefficients of this lag polynomial.
   */
  final double[] coefficients() {
    return this.coefficients.clone();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("1");
    for (int i = 1; i < coefficients.length - 1; i++) {
      if (Math.abs(coefficients[i]) > 1E-16) {

      if (coefficients[i] < 0) {
        builder.append(" - ");
      } else {
        builder.append(" + ");
      }
      if (Math.abs(coefficients[i]) - 1.0 > 1E-16) {
        builder.append(Math.abs(coefficients[i]));
      }
        builder.append("L");
        if (i > 1) {
          builder.append("^").append(i);
        }
      }
    }
    final int lastIndex = coefficients.length - 1;
    if (coefficients[lastIndex] < 0) {
      builder.append(" - ");
    } else {
      builder.append(" + ");
    }
    if (coefficients.length > 1) {
      if (coefficients[lastIndex] != 1.0) {
        builder.append(Math.abs(coefficients[lastIndex]));
      }
      builder.append("L");
    }
    if (coefficients.length > 2) {
      builder.append("^").append(lastIndex);
    }
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(coefficients);
    result = prime * result + degree;
    result = prime * result + Arrays.hashCode(parameters);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    LagPolynomial other = (LagPolynomial) obj;
    if (!Arrays.equals(coefficients, other.coefficients)) return false;
    return degree == other.degree && Arrays.equals(parameters, other.parameters);
  }
}
