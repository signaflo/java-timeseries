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
    return new Vector<T>(Collections.unmodifiableList(summed));
  }
  
  public final Vector<T> scaledBy(final T alpha) {
    final List<T> scaled = new ArrayList<T>(this.size());
    for (int i = 0; i < scaled.size(); i++) {
      scaled.add(this.elements.get(i).times(alpha));
    }
    return new Vector<T>(Collections.unmodifiableList(scaled));
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
