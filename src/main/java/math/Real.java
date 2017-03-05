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
package math;

/**
 * A numerical approximation of a <a target="_blank" href=https://en.wikipedia.org/wiki/Real_number>
 * real number</a>.
 *
 * @author Jacob Rachiele
 */
public final class Real extends Complex {

  private static final double EPSILON = Math.ulp(1.0);
  
  private final double value;

  /**
   * Create a new real number using the given double.
   *
   * @param value the primitive double approximating the real number.
   */
  public Real(final double value) {
    super(value);
    this.value = value;
  }

  /**
   * Create a new real number using the given double.
   *
   * @param value the primitive double approximating the real number.
   * @return a new real number from the given double.
   */
  public static Real from(final double value) {
    return new Real(value);
  }

  /**
   * Add this real number to the given real number and return the result.
   * @param other the real number to add to this one.
   * @return the sum of this real number and the given real number.
   */
  public Real plus(final Real other) {
    return new Real(this.value + other.value);
  }

  /**
   * Subtract the given real number from this real number and return the result.
   * @param other the real number to subtract from this one.
   * @return the difference of the given real number from this real number.
   */
  public Real minus(final Real other) {
    return new Real(this.value - other.value);
  }

  /**
   * Multiply this real number by the given real number and return the result.
   * @param other the real number to multiply this one by.
   * @return this real number multiplied by the given real number.
   */
  public Real times(Real other) {
    return new Real(this.value * other.value);
  }

  @Override
  public Real times(double other) {
    return new Real(this.value * other);
  }

  /**
   * Square this real number and return the result.
   * @return the square of this real number.
   */
  public Real squared() {
    return new Real(this.value * this.value);
  }

  /**
   * Cube this real number and return the result.
   * @return the cube of this real number.
   */
  public Real cubed() {
    return new Real(this.value * this.value * this.value);
  }

  /**
   * Divide this real number by the given real number and return the result.
   * @param other the real number to divide this one by.
   * @return this real number divided by the given real number.
   */
  public Real dividedBy(Real other) {
    return new Real(this.value / other.value);
  }

  /**
   * Take the additive inverse, or negative, of this real number and return the result.
   * @return the additive inverse, or negative, of this real number.
   */
  public Real negative() {
    return new Real(-this.value);
  }

  public double value() {
    return this.value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    Real real = (Real) o;
    return Double.compare(real.value, value) == 0;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(value);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public String toString() {
    return Double.toString(this.value);
  }

  public static final class Interval {

    private final Real lower;
    private final Real upper;

    Interval(final Real lower, final Real upper) {
      this.lower = lower;
      this.upper = upper;
    }

    public Interval(final double lower, final double upper) {
      this.lower = Real.from(lower);
      this.upper = Real.from(upper);
    }

    public double lowerDbl() {
      return this.lower.value();
    }

    public double upperDbl() {
      return this.upper.value();
    }

    public Real lower() {
      return this.lower;
    }

    public Real upper() {
      return this.upper;
    }

    public boolean endpointsEqual() {
      return Math.abs(this.lower.value - this.upper.value) < EPSILON;
    }

    public boolean contains(final double value) {
      if (lower.value < upper.value) {
        return value >= lower.value && value <= upper.value;
      }
      return value <= lower.value && value >= upper.value;
    }

    public boolean doesntContain(final double value) {
      return !contains(value);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Interval interval = (Interval) o;

      if (!lower.equals(interval.lower)) return false;
      return upper.equals(interval.upper);
    }

    @Override
    public int hashCode() {
      int result = lower.hashCode();
      result = 31 * result + upper.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return "(" + this.lower.value() + ", " + this.upper.value() + ")";
    }
  }
}
