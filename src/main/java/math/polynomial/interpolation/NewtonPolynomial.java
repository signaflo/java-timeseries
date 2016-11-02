/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial.interpolation;

import math.function.QuadraticFunction;

/**
 * @author Jacob Rachiele
 */
final class NewtonPolynomial {

  private final double[] point;
  private final double[] value;
  private final double[] coefficients;

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

  public final double evaluateAt(double x) {
    double product = 1.0;
    double result = coefficients[0];
    for (int i = 1; i < coefficients.length; i++) {
      product *= x - point[i - 1];
      result += coefficients[i] * product;
    }
    return result;
  }

  public final QuadraticFunction simplify() {
    double a = coefficients[2];
    double b = coefficients[1] - coefficients[2] * (point[0] + point[1]);
    double c = coefficients[0] - coefficients[1] * point[0] + coefficients[2] * point[0] * point[1];
    return new QuadraticFunction(a, b, c);
  }
}
