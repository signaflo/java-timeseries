/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math;

/**
 * @author Jacob Rachiele
 * A numerical approximation of a <a target="_blank" href=https://en.wikipedia.org/wiki/Real_number>
 *   real number</a>
 */
public final class Real extends Complex {

  private final double value;

  /**
   * Create a new real number using the given primitive double.
   * @param value the primitive double approximating the real number.
   */
  public Real(final double value) {
    super(value);
    this.value = value;
  }

  /**
   * Create a new real number using the given primitive double.
   * @param value the primitive double approximating the real number.
   */
  public static Real from(final double value) {
    return new Real(value);
  }

  /**
   * Add this real number to the given real number and return the result as a new real number.
   * @param other the real number to add to this one.
   * @return the sum of this real number and the given real number.
   */
  public final Real plus(final Real other) {
    return new Real(this.value + other.value);
  }

  public final Real minus(final Real other) {
    return new Real(this.value - other.value);
  }

  public final Real times(Real other) {
    return new Real(this.value * other.value);
  }

  @Override
  public final Real times(double other) {
    return new Real(this.value * other);
  }

  public final Real negative() {
    return new Real(-this.value);
  }

  public final double value() {
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
}
