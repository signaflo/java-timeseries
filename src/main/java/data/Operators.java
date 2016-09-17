/*
 * Copyright (c) 2016 Jacob Rachiele
 */

package data;

/**
 * Static methods for performing vector operations on arrays.
 * 
 * @author Jacob Rachiele
 *
 */
public final class Operators {

  private Operators() {
  }

  /**
   * Return the element-by-element product of the two arrays.
   * 
   * @param left the first array to take the product with.
   * @param right the second array to take the product with.
   * @return the element-by-element product of the two arrays.
   */
  public static final double[] productOf(final double[] left, final double[] right) {
    if (left.length != right.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] product = new double[left.length];
    for (int i = 0; i < left.length; i++) {
      product[i] = left[i] * right[i];
    }
    return product;
  }

  public static final double[] sumOf(final double[] left, final double[] right) {
    if (left.length != right.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] sum = new double[left.length];
    for (int i = 0; i < left.length; i++) {
      sum[i] = left[i] + right[i];
    }
    return sum;
  }

  public static final double[] differenceOf(final double[] left, final double[] right) {
    if (left.length != right.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] difference = new double[left.length];
    for (int i = 0; i < left.length; i++) {
      difference[i] = left[i] - right[i];
    }
    return difference;
  }

  public static final double[] quotientOf(final double[] numerator, final double[] denominator) {
    if (numerator.length != denominator.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] quotient = new double[numerator.length];
    for (int i = 0; i < numerator.length; i++) {
      quotient[i] = numerator[i] / denominator[i];
    }
    return quotient;
  }
}
