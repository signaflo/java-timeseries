package linear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import math.FieldElement;

public final class Vector<T extends FieldElement<T>> {
  
  public final List<T> elements;
  
  public Vector(List<T> elements) {
    this.elements = Collections.unmodifiableList(elements);
  }
  
  public final List<T> elements() {
    return this.elements;
  }
  
  public final T at(final int i) {
    return this.elements.get(i);
  }
  
  public final int size() {
    return this.elements.size();
  }
  
  public final Vector<T> plus(final Vector<T> other) {
    final List<T> summed = new ArrayList<T>(this.size());
    for (int i = 0; i < this.size(); i++) {
      summed.add(this.elements.get(i).plus(other.elements.get(i)));
    }
    return new Vector<T>(summed);
  }
  
  public final Vector<T> minus(final Vector<T> other) {
    final List<T> differenced = new ArrayList<T>(this.size());
    for (int i = 0; i < this.size(); i++) {
      differenced.add(this.elements.get(i).minus(other.elements.get(i)));
    }
    return new Vector<T>(differenced);
  }
  
  public final Vector<T> scaledBy(final T alpha) {
    final List<T> scaled = new ArrayList<T>(this.size());
    for (int i = 0; i < this.size(); i++) {
      scaled.add(this.elements.get(i).times(alpha));
    }
    return new Vector<T>(scaled);
  }

  public final Vector<T> axpy(final Vector<T> other, final T alpha) {
    final List<T> result = new ArrayList<T>(this.size());
    for (int i = 0; i < this.size(); i++) {
      result.add(this.elements.get(i).times(alpha).plus(other.elements.get(i)));
    }
    return new Vector<T>(result);
  }
  
  public final T dotProduct(final Vector<T> other) {
    if (this.size() > 0) {
      T product = this.elements.get(0).times(other.elements.get(0).conjugate());
      for (int t = 1; t < this.elements.size(); t++) {
        product = product.plus(this.elements.get(t).times(other.elements.get(t).conjugate()));
      }
      return product;
    }
    throw new IllegalStateException("The dot product is undefined for zero length vectors.");
  }
  
  public final double norm() {
    return Math.sqrt(sumOfSquares());
  }
  
  private final double sumOfSquares() {
    if (this.size() > 0) {
      double sum = Math.pow(this.elements.get(0).abs(), 2);
      for (int i = 1; i < this.elements.size(); i++) {
        sum += Math.pow(this.elements.get(i).abs(), 2);
      }
      return sum;
    }
    throw new IllegalStateException("sum of squares undefined for zero length vectors.");
  }
  
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("elements: ").append(elements);
    return builder.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elements == null) ? 0 : elements.hashCode());
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
    Vector<?> other = (Vector<?>) obj;
    if (elements == null) {
      if (other.elements != null)
        return false;
    } else if (!elements.equals(other.elements))
      return false;
    return true;
  }
}
