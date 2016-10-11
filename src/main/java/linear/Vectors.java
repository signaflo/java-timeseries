package linear;

import java.util.ArrayList;
import java.util.List;

import math.Complex;

/**
 * Static methods for creating Vector objects.
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
public final class Vectors {

  private Vectors() {}

   public static final FieldVector<Complex> linearCombination(FieldVector<Complex>[] vectors,
   Complex[] scalars) {
     FieldVector<Complex> result = zeroVector(scalars.length);
     for (int i = 0; i < vectors.length; i++) {
       result = result.plus(vectors[i].scaledBy(scalars[i]));
     }
     return result;
   }

  public static final FieldVector<Complex> axpy(final FieldVector<Complex> x,
      final FieldVector<Complex> y, final Complex alpha) {
    return (x.scaledBy(alpha).plus(y));
  }
  
  public static final FieldVector<Complex> zeroVector(final int size) {
    List<Complex> zeros = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      zeros.add(new Complex(0, 0));
    }
    return new FieldVector<>(zeros);
  }

}
