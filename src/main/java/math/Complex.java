package math;

public final class Complex implements FieldElement<Complex> {
  
  private final double real;
  private final double im;
  
  public Complex(final double real, final double im) {
    this.real = real;
    this.im = im;
  }

  @Override
  public final Complex plus(final Complex other) {
    return new Complex(this.real + other.real, this.im + other.im);
  }

  @Override
  public final Complex times(final Complex other) {
    final double realPart = this.real * other.real - this.im * other.im;
    final double imPart = this.real * other.im + other.real * this.im;
    return new Complex(realPart, imPart);
  }
  
  public final double abs() {
    return Math.sqrt(real * real + im * im);
  }
  
  public final double real() {
    return this.real;
  }
  
  public final double im() {
    return this.im;
  }
}
