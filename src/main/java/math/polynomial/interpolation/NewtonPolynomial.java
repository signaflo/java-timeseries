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
package math.polynomial.interpolation;

import math.function.CubicFunction;
import math.function.QuadraticFunction;

import java.util.Arrays;

/**
 * @author Jacob Rachiele
 * Represents the <a target="_blank" href="https://en.wikipedia.org/wiki/Newton_polynomial">Newton form</a>
 * of an interpolating polynomial.
 */
public final class NewtonPolynomial {

  private final double[] point;
  private final double[] value;
  private final double[] coefficients;

  /**
   * Create a new NewtonPolynomial with the given points and function values.
   * @param point the array of sample points.
   * @param value the array of sample function values at each corresponding point.
   */
  public NewtonPolynomial(final double[] point, final double[] value) {
    if (point.length != value.length) {
      throw new IllegalArgumentException("There must be one function value for each point, "
          + "but there were " + point.length + " points and " + value.length + " values.");
    }
    if (point.length == 0) {
      throw new IllegalArgumentException("A divided difference requires at least one point, "
          + "but no points were given.");
    }
    this.point = point.clone();
    this.value = value.clone();
    this.coefficients = new double[value.length];
    for (int i = 0; i < point.length; i++) {
      this.coefficients[i] = getDividedDifference(0, i);
    }
  }

  /**
   * Get the ith coefficient of this Newton Polynomial.
   *
   * @param i the index of the coefficient.
   * @return the ith coefficient of this Newton Polynomial.
   */
  double getCoefficient(final int i) {
    return this.coefficients[i];
  }

  private double getDividedDifference(final int start, final int end) {
    int k = end - start;
    if (k < 0) {
      throw new IllegalArgumentException("start must be less than end, but start was " + start +
          " and end was " + end);
    } else if (k == 0) {
      return value[end];
    } else if (k == 1) {
      return (value[end] - value[start]) / (point[end] - point[start]);
    } else {
      return (getDividedDifference(start + 1, end) - getDividedDifference(start, end - 1)) /
          (point[end] - point[start]);
    }
  }

  /**
   * Evaluate this Newton Polynomial at the given point.
   *
   * @param x the point at which to evaluate this Newton Polynomial.
   * @return the value of the polynomial at the given point.
   */
  public double evaluateAt(double x) {
    double product = 1.0;
    double result = coefficients[0];
    for (int i = 1; i < coefficients.length; i++) {
      product *= x - point[i - 1];
      result += coefficients[i] * product;
    }
    return result;
  }

  /**
   *

   * Convert this NewtonPolynomial to the standard form of a quadratic function, <i>f(x)</i>
   * = a<i>x</i><sup>2</sup> + b<i>x</i> + c.
   * @return this Newton Polynomial converted to standard quadratic form.
   * @throws IllegalStateException if the degree of this NewtonPolynomial is lower than 2.
   */
  public QuadraticFunction toQuadratic() throws IllegalStateException {
    if (coefficients.length < 3) {
      throw new IllegalStateException("The function is of degree " + (coefficients.length - 1) +
          " and thus not quadratic.");
    }
    double a = coefficients[2];
    double b = coefficients[1] - coefficients[2] * (point[0] + point[1]);
    double c = coefficients[0] - coefficients[1] * point[0] + coefficients[2] * point[0] * point[1];
    return new QuadraticFunction(a, b, c);
  }

  /**
   * Convert this NewtonPolynomial to the standard form of a cubic function, <i>f(x)</i>
   * = a<i>x</i><sup>3</sup> + b<i>x</i><sup>2</sup> + c<i>x</i> + d.
   *
   * @return this Newton Polynomial converted to standard cubic form.
   * @throws IllegalStateException if the degree of this NewtonPolynomial is lower than 3.
   */
  public CubicFunction toCubic() throws IllegalStateException {
    if (coefficients.length < 4) {
      throw new IllegalStateException("The function is of degree " + (coefficients.length - 1) +
          " and thus not cubic.");
    }
    double a = coefficients[3];
    double b = coefficients[2] - coefficients[3] * (point[0] + point[1] + point[2]);
    double c = coefficients[1] - coefficients[2] * (point[0] + point[1]) +
        coefficients[3] * (point[0] * point[1] + point[0] * point[2] + point[1] * point[2]);
    double d = coefficients[0] - coefficients[1] * point[0] + coefficients[2] * point[0] * point[1] -
        coefficients[3] * point[0] * point[1] * point[2];
    return new CubicFunction(a, b, c, d);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NewtonPolynomial that = (NewtonPolynomial) o;

    return Arrays.equals(coefficients, that.coefficients);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(coefficients);
  }
}
