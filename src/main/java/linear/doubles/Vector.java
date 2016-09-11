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
  
  public final Vector scaledBy(final double alpha) {
    final double[] scaled = new double[this.size()];
    for (int i = 0; i < scaled.length; i++) {
      scaled[i] = alpha * this.elements[i];
    }
    return new Vector(scaled);
    
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
