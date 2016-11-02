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

  public Real a() {
    return this.a;
  }

  public Real b() {
    return this.b;
  }

  public Real c() {
    return this.c;
  }

  public Real[] coefficients() {
    return new Real[] {this.a, this.b, this.c};
  }

  public double[] doubleCoefficients() {
    return new double[] {a.value(), b.value(), c.value()};
  }

  public double doubleExtremePoint() {
    return -b.value() / (2 * a.value());
  }

  public Real extremePoint() {
    return b.dividedBy(a.times(2));
  }

  public double doubleExtremum() {
    double x = doubleExtremePoint();
    return a.value() * x * x + b.value() * x + c.value();
  }

  public Real extremum() {
    Real x = extremePoint();
    return a.times(x).times(x).plus((b).times(x)).plus(c);
  }

  public boolean hasMinimum() {
    return a.value() > 0.0;
  }

  public boolean hasMaximum() {
    return a.value() < 0.0;
  }
}
