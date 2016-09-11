package linear.doubles;

import java.util.Arrays;

public final class Vector {

  private final double[] elements;
  
  public Vector(double... elements) {
    this.elements = elements.clone();
  }
  
  public final double[] elements() {
    return this.elements.clone();
  }
  
  public final double at(final int i) {
    return this.elements[i];
  }
  
  public final int size() {
    return this.elements.length;
  }
  
  public final Vector plus(final Vector other) {
    final double[] summed = new double[this.size()];
    for (int i = 0; i < summed.length; i++) {
      summed[i] = this.elements[i] + other.elements[i];
    }
    return new Vector(summed);
  }
  
  public final Vector minus(final Vector other) {
    final double[] differenced = new double[this.size()];
    for (int i = 0; i < differenced.length; i++) {
      differenced[i] = this.elements[i] - other.elements[i];
    }
    return new Vector(differenced);
  }
  
  public final Vector scaledBy(final double alpha) {
    final double[] scaled = new double[this.size()];
    for (int i = 0; i < scaled.length; i++) {
      scaled[i] = alpha * this.elements[i];
    }
    return new Vector(scaled);
  }
  
  public final double dotProduct(final Vector other) {
    if (other.elements.length > 0) {
      double product = this.elements[0] * other.elements[0];
      for (int i = 0; i < elements.length; i++) {
        product += this.elements[i] * other.elements[i];
      }
      return product;
    }
    throw new IllegalArgumentException("The dot product is undefined for zero length vectors");
  }
  
  public final Vector axpy(final Vector other, final double alpha) {
    final double[] result = new double[this.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = alpha * this.elements[i] + other.elements[i];
    }
    return new Vector(result);
  }
  
  public final double norm() {
    return Math.sqrt(dotProduct(this));
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(elements);
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
    Vector other = (Vector) obj;
    if (!Arrays.equals(elements, other.elements))
      return false;
    return true;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("elements: ").append(Arrays.toString(elements));
    return builder.toString();
  }
}
