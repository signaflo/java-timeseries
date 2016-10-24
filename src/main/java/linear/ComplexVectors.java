/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package linear;

import java.util.ArrayList;
import java.util.List;

import math.Complex;

/**
 * Static methods for creating complex-valued vectors.
 * 
 *
 */
public final class ComplexVectors {

  private ComplexVectors() {}

  public static FieldVector<Complex> linearCombination(List<FieldVector<Complex>> vectors, List<Complex> scalars) {
    FieldVector<Complex> result = zeroVector(scalars.size());
    for (int i = 0; i < vectors.size(); i++) {
      result = result.plus(vectors.get(i).scaledBy(scalars.get(i)));
    }
    return result;
  }

  public static FieldVector<Complex> axpy(final FieldVector<Complex> x, final FieldVector<Complex> y,
      final Complex alpha) {
    return (x.scaledBy(alpha).plus(y));
  }

  public static FieldVector<Complex> zeroVector(final int size) {
    List<Complex> zeros = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      zeros.add(new Complex(0, 0));
    }
    return new FieldVector<>(zeros);
  }

}
