package math;

/**
 * An immutable and thread-safe implementation of a complex number. Subclasses must maintain immutability.
 * @author Jacob Rachiele
 *
 */
public class Complex implements FieldElement<Complex> {
  
  private final double real;
  private final double im;
  
  /**
   * Construct a new complex number with real and imaginary parts both equal to 0.
   */
  public Complex() {
    this(0.0, 0.0);
  }

  /**
   * Construct a new complex number with zero imaginary part, i.e, a real number.
   */
  public Complex(final double real) {
    this(real, 0.0);
  }

  /**
   * Construct a new complex number with the given real and imaginary parts.
   * @param real the real part of the new complex number.
   * @param im the imaginary part of the new complex number.
   */
  public Complex(final double real, final double im) {
    this.real = real;
    this.im = im;
  }

  @Override
  public final Complex plus(final Complex other) {
    return new Complex(this.real + other.real, this.im + other.im);
  }
  
  /**
   * Add this element to the given double.
   * @param other the double to add to this element.
   * @return this element added to the given double.
   */
  public final Complex plus(final double other) {
    return new Complex(this.real + other, this.im);
  }
  
  @Override
  public final Complex minus(final Complex other) {
    return new Complex(this.real - other.real, this.im - other.im);
  }

  @Override
  public final Complex times(final Complex other) {
    final double realPart = this.real * other.real - this.im * other.im;
    final double imPart = this.real * other.im + other.real * this.im;
    return new Complex(realPart, imPart);
  }
  
  /**
   * Multiply this element by the given double.
   * @param other the double to multiply this element by.
   * @return this element multiplied by the given double.
   */
  public Complex times(final double other) {
    return new Complex(this.real * other, this.im * other);
  }
  
  /**
   * Divide this element by the given double.
   * @param other the double to divide this element by.
   * @return this element divided by the given double.
   */
  public final Complex dividedBy(final double other) {
    return new Complex(this.real/other, this.im/other);
  }
  
  @Override
  public final Complex conjugate() {
    return new Complex(this.real, -this.im);
  }
  
  @Override
  public final double abs() {
    return Math.sqrt(real * real + im * im);
  }
  
  @Override
  public Complex sqrt() {
    // TODO: Replace zero equality check with more sophisticated method.
    if (this.real < 0 && this.im == 0) {
      return new Complex(0.0, Math.sqrt(abs()));
    }
    // The following algorithm fails only in the case where this complex number is
    // a negative real number, but that case was taken care of in the preceding if branch.
    // http://math.stackexchange.com/questions/44406/how-do-i-get-the-square-root-of-a-complex-number
    final double r = abs();
    final Complex zr = this.plus(r);
    return zr.dividedBy(zr.abs()).times(Math.sqrt(r));
  }
  
  /**
   * The real part of this complex number.
   * @return the real part of this complex number.
   */
  public final double real() {
    return this.real;
  }
  
  /**
   * The imaginary part of this complex number.
   * @return the imaginary part of this complex number.
   */
  public final double im() {
    return this.im;
  }
  
  

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(im);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(real);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Complex other = (Complex) obj;
    return Math.abs(im - other.im) <= Math.ulp(1.0) && Math.abs(real - other.real) <= Math.ulp(1.0);
  }

  @Override
  public String toString() {
    return "(real: " + real + " im: " + im + ")";
  }
}
