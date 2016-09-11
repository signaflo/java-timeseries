package math;

public final class Complex implements FieldElement<Complex> {
  
  private final double real;
  private final double im;
  
  public Complex() {
    this(0.0, 0.0);
  }
  
  public Complex(final double real, final double im) {
    this.real = real;
    this.im = im;
  }

  @Override
  public final Complex plus(final Complex other) {
    return new Complex(this.real + other.real, this.im + other.im);
  }
  
  public final Complex plus(final double other) {
    return new Complex(this.real + other, this.im);
  }
  
  public final Complex minus(final Complex other) {
    return new Complex(this.real - other.real, this.im - other.im);
  }

  @Override
  public final Complex times(final Complex other) {
    final double realPart = this.real * other.real - this.im * other.im;
    final double imPart = this.real * other.im + other.real * this.im;
    return new Complex(realPart, imPart);
  }
  
  public final Complex times(final double other) {
    return new Complex(this.real * other, this.im * other);
  }
  
  public final Complex dividedBy(final double other) {
    return new Complex(this.real/other, this.im/other);
  }
  
  public final Complex conjugate() {
    return new Complex(this.real, -this.im);
  }
  
  @Override
  public final double abs() {
    return Math.sqrt(real * real + im * im);
  }
  
  @Override
  public final Complex sqrt() {
    final double r = abs();
    final Complex zr = this.plus(r);
    return zr.dividedBy(zr.abs()).times(Math.sqrt(r));
  }
  
  public final double real() {
    return this.real;
  }
  
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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Complex other = (Complex) obj;
    if (Math.abs(im - other.im) > Math.ulp(1.0))
      return false;
    if (Math.abs(real - other.real) > Math.ulp(1.0))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("(real: ").append(real).append(" im: ").append(im + ")");
    return builder.toString();
  }
}
