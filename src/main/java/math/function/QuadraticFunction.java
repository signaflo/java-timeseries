/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import math.Complex;
import math.Real;

/**
 * @author Jacob Rachiele
 * Represents a univariate polynomial function of degree 2.
 *
 */
public class QuadraticFunction {

  private final Real a;
  private final Real b;
  private final Real c;

  public QuadraticFunction(final Real a, final Real b, final Real c) {
    if (a.value() == 0) {
      throw new IllegalArgumentException("The first coefficient, a, cannot be zero.");
    }
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public QuadraticFunction(final double a, final double b, final double c) {
    if (a == 0) {
      throw new IllegalArgumentException("The first coefficient, a, cannot be zero.");
    }
    this.a = Real.from(a);
    this.b = Real.from(b);
    this.c = Real.from(c);
  }

  /**
   * Compute and return the zeros, or roots, of this function.
   * @return the zeros, or roots, of this function.
   */
  public Complex[] zeros() {
    final Real fourAC = a.times(c).times(4.0);
    final Real bSquared = b.times(b);
    final Complex root1 = b.negative().plus(bSquared.minus(fourAC).sqrt()).dividedBy(a.times(2).value());
    final Complex root2 = b.negative().minus(bSquared.minus(fourAC).sqrt()).dividedBy(a.times(2).value());
    return new Complex[] {root1, root2};
  }
}
