/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial.interpolation;

public final class LinearInterpolation {
  
  private final double a0;
  private final double a1;
  
  public LinearInterpolation(final double x0, final double x1, final double f0, final double f1) {
    if (x0 == x1) {
      throw new IllegalArgumentException("x0 and x1 must be distinct points, but both were equal to: " + x1);
    }
    this.a0 = (x1 * f0 - f1 * x0) / (x1 - x0);
    this.a1 = (f1 - a0) / x1;
  }
  
  public double a0() {
    return a0;
  }
  
  public double a1() {
    return a1;
  }

}
