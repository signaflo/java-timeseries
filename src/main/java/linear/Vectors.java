package linear;

import java.util.ArrayList;
import java.util.List;

import math.Complex;

public final class Vectors {

  private Vectors() {}

   public static final Vector<Complex> linearCombination(Vector<Complex>[] vectors,
   Complex[] scalars) {
     Vector<Complex> result = zeroVector(scalars.length);
     for (int i = 0; i < vectors.length; i++) {
       result = result.plus(vectors[i].scaledBy(scalars[i]));
     }
     return result;
   }

  public static final Vector<Complex> axpy(final Vector<Complex> x,
      final Vector<Complex> y, final Complex alpha) {
    return (x.scaledBy(alpha).plus(y));
  }
  
  public static final Vector<Complex> zeroVector(final int size) {
    List<Complex> zeros = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      zeros.add(new Complex(0, 0));
    }
    return new Vector<>(zeros);
  }

}
