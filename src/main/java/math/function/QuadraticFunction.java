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
 * A univariate polynomial function of degree 2.
 *
 * @author Jacob Rachiele
 */
public class QuadraticFunction extends AbstractFunction {

  private final Real a;
  private final Real b;
  private final Real c;

  /**
   * Create a new quadratic function using the given coefficients.
   *
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
   *
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
   *
   * @return the zeros, or roots, of this function.
   */
  public Complex[] zeros() {
    final Real fourAC = a.times(c).times(4.0);
    final Real bSquared = b.times(b);
    final Complex root1 = b.negative().plus(bSquared.minus(fourAC).sqrt()).dividedBy(a.times(2).value());
    final Complex root2 = b.negative().minus(bSquared.minus(fourAC).sqrt()).dividedBy(a.times(2).value());
    return new Complex[]{root1, root2};
  }

  /**
   * retrieve the coefficient of the leading term of the polynomial.
   *
   * @return the coefficient of the leading term of the polynomial.
   */
  public Real a() {
    return this.a;
  }

  /**
   * retrieve the coefficient of the first degree term of the polynomial.
   *
   * @return the coefficient of the first degree term of the polynomial.
   */
  public Real b() {
    return this.b;
  }

  /**
   * retrieve the constant term of the polynomial.
   *
   * @return the constant term of the polynomial.
   */
  public Real c() {
    return this.c;
  }

  public Real at(final Real point) {
    return new Real(this.at(point.value()));
  }

  public double at(final double x) {
    return x * x * a.value() + x * b.value() + c.value();
  }

  /**
   * retrieve the coefficients of the polynomial.
   *
   * @return the coefficients of the polynomial.
   */
  public Real[] coefficients() {
    return new Real[]{this.a, this.b, this.c};
  }

  /**
   * retrieve the coefficients of the polynomial as primitives.
   *
   * @return the coefficients of the polynomial as primitives.
   */
  public double[] coefficientsDbl() {
    return new double[]{a.value(), b.value(), c.value()};
  }

  /**
   * retrieve the point at which the localExtrema of this function occurs as a primitive.
   *
   * @return the point at which the localExtrema of this function occurs as a primitive.
   */
  public double extremePointDbl() {
    return -b.value() / (2 * a.value());
  }

  /**
   * retrieve the point at which the localExtrema of this function occurs.
   *
   * @return the point at which the localExtrema of this function occurs.
   */
  public Real extremePoint() {
    return new Real(-b.value() / (2 * a.value()));
  }

  /**
   * retrieve the localExtrema of this function as a primitive.
   *
   * @return the localExtrema of this function as a primitive.
   */
  public double extremumDbl() {
    double x = extremePointDbl();
    return a.value() * x * x + b.value() * x + c.value();
  }

  /**
   * retrieve the localExtrema of this function.
   *
   * @return the localExtrema of this function.
   */
  public Real extremum() {
    double x = extremePoint().value();
    return new Real(a.value() * x * x + b.value() * x + c.value());
  }

  /**
   * Indicates if this function has a minimum or not.
   *
   * @return true if this function has a minimum, false otherwise.
   */
  public boolean hasMinimum() {
    return a.value() > 0.0;
  }

  /**
   * Indicates if this function has a maximum or not.
   *
   * @return true if this function has a maximum, false otherwise.
   */
  public boolean hasMaximum() {
    return a.value() < 0.0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    QuadraticFunction that = (QuadraticFunction) o;

    if (!a.equals(that.a)) return false;
    if (!b.equals(that.b)) return false;
    return c.equals(that.c);
  }

  @Override
  public int hashCode() {
    int result = a.hashCode();
    result = 31 * result + b.hashCode();
    result = 31 * result + c.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "f(x) = " + a.value() + "x^2 + " + b.value() + "x + " + c.value();
  }
}
