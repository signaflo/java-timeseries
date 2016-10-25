/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package data;

/**
 * Static methods for performing vector operations on arrays.
 * 
 * @author Jacob Rachiele
 *
 */
public final class Operators {

  private Operators() {}

  /**
   * Take the element-by-element product of the two arrays and return the result in a new array.
   * 
   * @param left the first array to take the product with.
   * @param right the second array to take the product with.
   * @return the element-by-element product of the two arrays.
   */
  public static double[] productOf(final double[] left, final double[] right) {
    if (left.length != right.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] product = new double[left.length];
    for (int i = 0; i < left.length; i++) {
      product[i] = left[i] * right[i];
    }
    return product;
  }

  /**
   * Take the element-by-element sum of the two arrays and return the result in a new array.
   *
   * @param left the first array to take the sum with.
   * @param right the second array to take the sum with.
   * @return the element-by-element sum of the two arrays.
   */
  public static double[] sumOf(final double[] left, final double[] right) {
    if (left.length != right.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] sum = new double[left.length];
    for (int i = 0; i < left.length; i++) {
      sum[i] = left[i] + right[i];
    }
    return sum;
  }

  /**
   * Take the element-by-element difference of the two arrays and return the result in a new array.
   *
   * @param left the first array to take the difference with.
   * @param right the second array to take the difference with.
   * @return the element-by-element difference of the two arrays.
   */
  public static double[] differenceOf(final double[] left, final double[] right) {
    if (left.length != right.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] difference = new double[left.length];
    for (int i = 0; i < left.length; i++) {
      difference[i] = left[i] - right[i];
    }
    return difference;
  }

  /**
   * Take the element-by-element quotient of the two arrays and return the result in a new array.
   *
   * @param numerator the array of numerators.
   * @param denominator the array of denominators.
   * @return the element-by-element quotient of the two arrays.
   */
  public static double[] quotientOf(final double[] numerator, final double[] denominator) {
    if (numerator.length != denominator.length) {
      throw new IllegalArgumentException("The data arrays must have the same length.");
    }
    final double[] quotient = new double[numerator.length];
    for (int i = 0; i < numerator.length; i++) {
      quotient[i] = numerator[i] / denominator[i];
    }
    return quotient;
  }

  /**
   * Scale the original data by the given scalar, &alpha;, and return the result in a new array.
   * @param original the data to be scaled.
   * @param alpha the scaling factor.
   * @return the original data scaled by the given scalar, &alpha;.
   */
  public static double[] scaled(final double[] original, final double alpha) {
    final double[] scaled = new double[original.length];
    for (int i = 0; i < original.length; i++) {
      scaled[i] = original[i] * alpha;
    }
    return scaled;
  }
}
