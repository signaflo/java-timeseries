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
package math.function;

import math.Complex;
import math.Real;

/**
 *
 * A univariate polynomial function of degree 3.
 *
 * @author Jacob Rachiele
 */
public final class CubicFunction {

  private final Real a;
  private final Real b;
  private final Real c;
  private final Real d;
  private final QuadraticFunction derivative;

  /**
   * Create a new cubic function using the given coefficients.
   *
   * @param a the coefficient of the leading term of the polynomial.
   * @param b the coefficient of the second degree term of the polynomial.
   * @param c the coefficient of the first degree term of the polynomial.
   * @param d the constant term of the polynomial.
   */
  public CubicFunction(final Real a, final Real b, final Real c, final Real d) {
    if (a.value() == 0) {
      throw new IllegalArgumentException("The first coefficient, a, cannot be zero.");
    }
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
    this.derivative = new QuadraticFunction(a.times(3), b.times(2), c);
  }

  /**
   * Create a new cubic function using the given coefficients.
   *
   * @param a the coefficient of the leading term of the polynomial.
   * @param b the coefficient of the second degree term of the polynomial.
   * @param c the coefficient of the first degree term of the polynomial.
   * @param d the constant term of the polynomial.
   */
  public CubicFunction(final double a, final double b, final double c, final double d) {
    if (a == 0) {
      throw new IllegalArgumentException("The first coefficient, a, cannot be zero.");
    }
    this.a = Real.from(a);
    this.b = Real.from(b);
    this.c = Real.from(c);
    this.d = Real.from(d);
    this.derivative = new QuadraticFunction(a * 3, b * 2, c);
  }

  /**
   * retrieve the coefficient of the leading term of the polynomial.
   * @return the coefficient of the leading term of the polynomial.
   */
  public Real a() {
    return this.a;
  }

  /**
   * retrieve the coefficient of the second degree term of the polynomial.
   * @return the coefficient of the second degree term of the polynomial.
   */
  public Real b() {
    return this.b;
  }

  /**
   * retrieve the coefficient of the first degree term of the polynomial.
   * @return the coefficient of the first degree term of the polynomial.
   */
  public Real c() {
    return this.c;
  }

  /**
   * retrieve the constant term of the polynomial.
   * @return the constant term of the polynomial.
   */
  public Real d() {
    return this.d;
  }

  /**
   * Evaluate this function at the given point and return the result.
   * @param point the point to evaluate the function at.
   * @return the value of this function at the given point.
   */
  public Real at(final Real point) {
    double x = point.value();
    return new Real(x*x*x*a.value() + x*x*b.value() + x*c.value() + d.value());
  }

  /**
   * retrieve the coefficients of the polynomial.
   * @return the coefficients of the polynomial.
   */
  public Real[] coefficients() {
    return new Real[] {this.a, this.b, this.c, this.d};
  }

  /**
   * retrieve the coefficients of the polynomial as primitives.
   * @return the coefficients of the polynomial as primitives.
   */
  public double[] coefficientsDbl() {
    return new double[] {a.value(), b.value(), c.value(), d.value()};
  }

  final Real[] criticalPoints() {
    Complex[] zeros = this.derivative.zeros();
    if (zeros[0].equals(zeros[1])) {
      return new Real[] {Real.from(zeros[0].real())};
    } else if (allReal(zeros)) {
      return toReal(zeros);
    } else {
      return new Real[] {};
    }
  }

  final Real localMinimum() {
    return this.at(localMinimumPoint());
  }

  final Real localMaximum() {
    return this.at(localMaximumPoint());
  }

  final Real localMinimumPoint() {
    if (!hasMinimum()) {
      throw new RuntimeException("This cubic function " + this.toString() + " has no local minimum.");
    }
    Real[] extremePoints = localExtremePoints();
    if ((extremePoints[0].value() * a.value()) > (b.value() / -3.0)) {
      return extremePoints[0];
    }
    return extremePoints[1];
  }


  final Real localMaximumPoint() {
    if (!hasMaximum()) {
      throw new RuntimeException("This cubic function " + this.toString() + " has no local maximum.");
    }
    Real[] extremePoints = localExtremePoints();
    if ((extremePoints[0].value() * a.value()) < (b.value() / -3.0)) {
      return extremePoints[0];
    }
    return extremePoints[1];
  }
  /**
   * retrieve the points at which the local extrema of this function occurs.
   * @return the points at which the local extrema of this function occurs.
   */
  public Real[] localExtremePoints() {
    Real[] points = toReal(criticalPoints());
    if (points.length < 2) {
      return new Real[]{};
    }
    return points;
  }

  /**
   * retrieve the local extrema of this function.
   * @return the local extrema of this function.
   */
  public Real[] localExtrema() {
    Real[] x = localExtremePoints();
    return evaluate(x);
  }

  /**
   * Indicates if this function has a minimum or not.
   * @return true if this function has a minimum, false otherwise.
   */
  public boolean hasMinimum() {
    return computeDiscriminant() > 0.0;
  }

  /**
   * Indicates if this function has a maximum or not.
   * @return true if this function has a maximum, false otherwise.
   */
  public boolean hasMaximum() {
    return computeDiscriminant() > 0.0;
  }

  private boolean allReal(final Complex[] numbers) {
    for (Complex number : numbers) {
      if (!number.isReal()) {
        return false;
      }
    }
    if (numbers.length == 0) {
      return false;
    }
    return true;
  }

  private Real[] toReal(final Complex[] numbers) {
    Real[] reals = new Real[numbers.length];
    for (int i = 0; i < numbers.length; i++) {
      reals[i] = Real.from(numbers[i].real());
    }
    return reals;
  }

  private double computeDiscriminant() {
    return b.value() * b.value() - 3 * a.value() * c.value();
  }

  private Real[] evaluate(final Real[] points) {
    Real[] values = new Real[points.length];
    for (int i = 0; i < points.length; i++) {
      double x= points[i].value();
      values[i] = Real.from(a.value() * x * x * x + b.value() * x * x + c.value() * x + d.value());
    }
    return values;
  }

//  private double[] toPrimitive(final Real[] points) {
//    double[] prim = new double[points.length];
//    for (int i = 0; i < prim.length; i++) {
//      prim[i] = points[i].value();
//    }
//    return prim;
//  }

  @Override
  public String toString() {
    return "f(x) = " + a.value() + "x^3 + " + b.value() + "x^2 + " + c.value() + "x + " + d.value();
  }
}
