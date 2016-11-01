/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial.interpolation;

/**
 * @author Jacob Rachiele
 */
public final class DividedDifference {

  private final double[] point;
  private final double[] value;
  private final double[] coefficients;

  public DividedDifference(final double[] point, final double[] value) {
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
    this.coefficients[0] = value[0];
    if (point.length > 1) {
      this.coefficients[1] = (value[1] - value[0]) / (point[1] - point[0]);
    }
    for (int i = 2; i < point.length; i++) {
      this.coefficients[i] = getDividedDifference(0, i);
    }
  }

//  public final double[] coefficients() {
//    return this.coefficients.clone();
//  }

  public final double getCoefficient(final int i) {
    return this.coefficients[i];
  }

  final double getDividedDifference(final int start, final int end) {
    int k = end - start;
    if (k < 0) {
      throw new IllegalArgumentException("start must be less than end.");
    } else if (k == 0) {
      return value[end];
    } else if (k == 1) {
      return (value[end] - value[start]) / (point[end] - point[start]);
    } else {
      return (getDividedDifference(start + 1, end) - getDividedDifference(start, end - 1)) / (point[end] - point[0]);
    }
  }
}
