/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial;

import math.function.QuadraticFunction;

/**
 * @author Jacob Rachiele
 * Represents the <a target="_blank" href="https://en.wikipedia.org/wiki/Newton_polynomial">Newton form</a>
 * of an interpolating polynomial.
 */
final class NewtonPolynomial {

  private final double[] point;
  private final double[] value;
  private final double[] coefficients;

  /**
   * Create a new NewtonPolynomial with the given points and function values.
   * @param point the array of sample points.
   * @param value the array of sample function values at each corresponding point.
   */
  NewtonPolynomial(final double[] point, final double[] value) {
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

  final double[] coefficients() {
    return this.coefficients.clone();
  }

  /**
   * Get the ith coefficient of this Newton Polynomial.
   * @param i the index of the coefficient.
   * @return the ith coefficient of this Newton Polynomial.
   */
  final double getCoefficient(final int i) {
    return this.coefficients[i];
  }

  private double getDividedDifference(final int start, final int end) {
    int k = end - start;
    if (k < 0) {
      throw new IllegalArgumentException("start must be less than end.");
    } else if (k == 0) {
      return value[end];
    } else if (k == 1) {
      return (value[end] - value[start]) / (point[end] - point[start]);
    } else {
      return (getDividedDifference(start + 1, end) - getDividedDifference(start, end - 1)) /
          (point[end] - point[0]);
    }
  }

  /**
   * Evaluate this Newton Polynomial at the given point.
   * @param x the point at which to evaluate this Newton Polynomial.
   * @return the value of the polynomial at the given point.
   */
  public final double evaluateAt(double x) {
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
   * @return this Newton Polynomial converted to standard quadratic form.
   * @throws IllegalStateException if this polynomial is not quadratic.
   */
  public final QuadraticFunction toQuadratic() throws IllegalStateException {
    if (coefficients.length != 3) {
      throw new IllegalStateException("The function is of degree " + (coefficients.length - 1) +
          " and thus not quadratic.");
    }
    double a = coefficients[2];
    double b = coefficients[1] - coefficients[2] * (point[0] + point[1]);
    double c = coefficients[0] - coefficients[1] * point[0] + coefficients[2] * point[0] * point[1];
    return new QuadraticFunction(a, b, c);
  }
}
