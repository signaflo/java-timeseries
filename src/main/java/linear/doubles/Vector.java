/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package linear.doubles;

import java.util.Arrays;

import stats.Statistics;

/**
 * An immutable and thread-safe implementation of a real-valued vector backed by an array of primitive doubles.
 * @author Jacob Rachiele
 *
 */
public final class Vector {

  private final double[] elements;
  
  public static Vector newVector(double... elements) {
    return new Vector(elements);
  }
  
  /**
   * Create a new vector using the provided elements.
   * @param elements the array of primitive doubles constituting the elements of the new vector.
   */
  public Vector(double... elements) {
    this.elements = elements.clone();
  }
  
  /**
   * The elements of the vector as an array of primitive doubles.
   * @return the elements of the vector as an array of primitive doubles.
   */
  public final double[] elements() {
    return this.elements.clone();
  }
  
  /**
   * Return the element at index i, where indexing begins at 0.
   * @param i the index of the element.
   * @return the element at index i, where indexing begins at 0.
   */
  public final double at(final int i) {
    return this.elements[i];
  }
  
  /**
   * The number of elements in this vector.
   * @return the number of elements in this vector.
   */
  public final int size() {
    return this.elements.length;
  }
  
  /**
   * Add this vector to the given vector and return the result in a new vector.
   * @param other the vector to add to this vector.
   * @return this vector added to the given vector.
   */
  public final Vector plus(final Vector other) {
    final double[] summed = new double[this.size()];
    for (int i = 0; i < summed.length; i++) {
      summed[i] = this.elements[i] + other.elements[i];
    }
    return new Vector(summed);
  }
  
  /**
   * Subtract the given vector from this vector and return the result in a new vector.
   * @param other the vector to subtract from this vector.
   * @return this vector subtracted by the given vector.
   */
  public final Vector minus(final Vector other) {
    final double[] differenced = new double[this.size()];
    for (int i = 0; i < differenced.length; i++) {
      differenced[i] = this.elements[i] - other.elements[i];
    }
    return new Vector(differenced);
  }
  
  /**
   * Subtract the given scalar from this vector and return the result in a new vector.
   * @param scalar the scalar to subtract from this vector.
   * @return this vector subtracted by the given scalar.
   */
  public final Vector minus(final double scalar) {
    final double[] differenced = new double[this.size()];
    for (int i = 0; i < differenced.length; i++) {
      differenced[i] = this.elements[i] - scalar;
    }
    return new Vector(differenced);
  }
  
  /**
   * Scale this vector by the given scalar and return the result in a new vector.
   * @param alpha the scalar to scale this vector by.
   * @return this vector scaled by the given scalar.
   */
  public final Vector scaledBy(final double alpha) {
    final double[] scaled = new double[this.size()];
    for (int i = 0; i < scaled.length; i++) {
      scaled[i] = alpha * this.elements[i];
    }
    return new Vector(scaled);
  }
  
  /**
   * Compute the dot product of this vector with the given vector.
   * @param other the vector to take the dot product with.
   * @return the dot product of this vector with the given vector.
   */
  public final double dotProduct(final Vector other) {
    if (other.elements.length > 0) {
      double product = 0.0;
      for (int i = 0; i < elements.length; i++) {
        product += this.elements[i] * other.elements[i];
      }
      return product;
    }
    throw new IllegalArgumentException("The dot product is undefined for zero length vectors");
  }

  public final Matrix outerProduct(final Vector other) {
    double[] product = new double[elements.length * other.elements.length];
    for (int i = 0; i < elements.length; i++) {
      for (int j = 0; j < other.elements.length; j++) {
        product[i * other.elements.length + j] = elements[i] * other.elements[j];
      }
    }
    return new Matrix(elements.length, other.elements.length, product);
    
  }

  final Vector axpy(final Vector other, final double alpha) {
    final double[] result = new double[this.size()];
    for (int i = 0; i < result.length; i++) {
      result[i] = alpha * this.elements[i] + other.elements[i];
    }
    return new Vector(result);
  }
  
  /**
   * Compute the L2 length of this vector.
   * @return the L2 length of this vector.
   */
  public final double norm() {
    return Math.sqrt(dotProduct(this));
  }

  public final double sum() {
    return Statistics.sumOf(elements);
  }

  public final double sumOfSquares() {
    return Statistics.sumOfSquared(elements);
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
    return Arrays.equals(elements, other.elements);
  }

  @Override
  public String toString() {
    return "elements: " + Arrays.toString(elements);
  }
}
