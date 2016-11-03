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

  /**
   * Create a new quadratic function using the given coefficients.
   * @param a the coefficient of the leading term of the polynomial.
   * @param b the coefficient of the first degree term of the polynomial.
   * @param c the constant term of the polynomial.
   */
  public QuadraticFunction(final Real a, final Real b, final Real c) {
    if (a.value() == 0) {
      throw new IllegalArgumentException("The first coefficient, a, cannot be zero.");
    }
    this.a = a;
    this.b = b;
    this.c = c;
  }

  /**
   * Create a new quadratic function using the given coefficients.
   * @param a the coefficient of the leading term of the polynomial.
   * @param b the coefficient of the first degree term of the polynomial.
   * @param c the constant term of the polynomial.
   */
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

  /**
   * retrieve the coefficient of the leading term of the polynomial.
   * @return the coefficient of the leading term of the polynomial.
   */
  public Real a() {
    return this.a;
  }

  /**
   * retrieve the coefficient of the first degree term of the polynomial.
   * @return the coefficient of the first degree term of the polynomial.
   */
  public Real b() {
    return this.b;
  }

  /**
   * retrieve the constant term of the polynomial.
   * @return the constant term of the polynomial.
   */
  public Real c() {
    return this.c;
  }

  public Real at(final Real point) {
    double x = point.value();
    return new Real(x*x*a.value() + x*b.value() + c.value());
  }

  /**
   * retrieve the coefficients of the polynomial.
   * @return the coefficients of the polynomial.
   */
  public Real[] coefficients() {
    return new Real[] {this.a, this.b, this.c};
  }

  /**
   * retrieve the coefficients of the polynomial as primitives.
   * @return the coefficients of the polynomial as primitives.
   */
  public double[] coefficientsPrimitive() {
    return new double[] {a.value(), b.value(), c.value()};
  }

  /**
   * retrieve the point at which the extremum of this function occurs as a primitive.
   * @return the point at which the extremum of this function occurs as a primitive.
   */
  public double extremePointPrimitive() {
    return -b.value() / (2 * a.value());
  }

  /**
   * retrieve the point at which the extremum of this function occurs.
   * @return the point at which the extremum of this function occurs.
   */
  public Real extremePoint() {
    return b.dividedBy(a.times(2));
  }

  /**
   * retrieve the extremum of this function as a primitive.
   * @return the extremum of this function as a primitive.
   */
  public double extremumPrimitive() {
    double x = extremePointPrimitive();
    return a.value() * x * x + b.value() * x + c.value();
  }

  /**
   * retrieve the extremum of this function.
   * @return the extremum of this function.
   */
  public Real extremum() {
    Real x = extremePoint();
    return a.times(x).times(x).plus((b).times(x)).plus(c);
  }

  /**
   * Indicates if this function has a minimum or not.
   * @return true if this function has a minimum, false otherwise.
   */
  public boolean hasMinimum() {
    return a.value() > 0.0;
  }

  /**
   * Indicates if this function has a maximum or not.
   * @return true if this function has a maximum, false otherwise.
   */
  public boolean hasMaximum() {
    return a.value() < 0.0;
  }
}
