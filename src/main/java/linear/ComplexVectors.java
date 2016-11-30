/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package linear;

import math.Complex;

import java.util.ArrayList;
import java.util.List;

/**
 * Static methods for creating complex-valued vectors.
 */
public final class ComplexVectors {

  private ComplexVectors() {}

  /**
   * Produce a linear combination from the given vectors and scalars.
   *
   * @param vectors the vector components of the linear combination.
   * @param scalars the scalar components of the linear combination.
   * @return a linear combination of the given vectors and scalars.
   */
  public static FieldVector<Complex> linearCombination(List<FieldVector<Complex>> vectors, List<Complex> scalars) {
    FieldVector<Complex> result = zeroVector(scalars.size());
    for (int i = 0; i < vectors.size(); i++) {
      result = result.plus(vectors.get(i).scaledBy(scalars.get(i)));
    }
    return result;
  }

  /**
   * Scale vector x by &alpha; then add y and return the result in a new vector.
   *
   * @param x     the vector to be scaled.
   * @param y     the vector to be added to scaled x.
   * @param alpha the scalar to scale x by.
   * @return x scaled by &alpha; then added to y.
   */
  public static FieldVector<Complex> axpy(final FieldVector<Complex> x, final FieldVector<Complex> y,
                                          final Complex alpha) {
    return (x.scaledBy(alpha).plus(y));
  }

  /**
   * Create a new vector filled with zeros.
   *
   * @param size the size of the new vector.
   * @return a new vector filled with zeros.
   */
  public static FieldVector<Complex> zeroVector(final int size) {
    List<Complex> zeros = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      zeros.add(new Complex(0, 0));
    }
    return new FieldVector<>(zeros);
  }

}
