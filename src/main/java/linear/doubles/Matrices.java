/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package linear.doubles;

/**
 * Static methods for creating Matrix objects.
 * @author jrachiele
 *
 */
public final class Matrices {
  
  private Matrices(){}
  
  /**
   * Create a new identity matrix of the given dimension.
   * @param n the dimension of the identity matrix.
   * @return a new identity matrix of the given dimension.
   */
  public static Matrix identity(final int n) {
    final double[] data = new double[n * n];
    for (int i = 0; i < n; i++) {
      data[i * n + i] = 1.0;
    }
    return new Matrix(n, n , data);
  }

}
