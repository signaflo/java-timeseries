/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package linear.doubles;

import java.util.Arrays;

/**
 * An immutable and thread-safe Matrix implementation.
 * @author jrachiele
 *
 */
public final class Matrix {
  
  private final int nrow;
  private final int ncol;
  private final double[] data;
  
  public Matrix(final int nrow, final int ncol, final double... data) {
    if (nrow * ncol != data.length) {
      throw new IllegalArgumentException("The dimensions do not match the amount of data provided. The amount of data was "
              + data.length + ", but the number of rows and columns were " + nrow + " and " + ncol + " respectively.");
    }
    this.nrow = nrow;
    this.ncol = ncol;
    this.data = data.clone();
  }
  
  public Matrix(final int nrow, final int ncol, final double datum) {
    this.nrow = nrow;
    this.ncol = ncol;
    this.data = new double[nrow * ncol];
    for (int i = 0; i < data.length; i++) {
      this.data[i] = datum;
    }
  }
  
  public Matrix(final double[][] matrixData) {
    this.nrow = matrixData.length;
    this.ncol = matrixData[0].length;
    this.data = new double[nrow * ncol];
    for (int i = 0; i < nrow; i++) {
      System.arraycopy(matrixData[i], 0, this.data, i * ncol, ncol);
    }
  }
  
  public final Matrix plus(final Matrix other) {
    if (this.nrow != other.nrow || this.ncol != other.ncol) {
      throw new IllegalArgumentException("The dimensions of this matrix must equal the dimensions of the other matrix. "
          + "This matrix has dimension (" + this.nrow + ", " + this.ncol + ") and the other matrix has dimension (" + other.nrow
          + ", " + other.ncol + ")");
    }
    final double[] sum = new double[nrow * ncol];
    for (int i = 0; i < nrow; i++) {
      for (int j = 0; j < ncol; j++) {
        sum[i * ncol + j] = this.data[i * ncol + j] + other.data[i * ncol + j];
      }
    }
    return new Matrix(this.nrow, this.ncol, sum);
  }
  
  /**
   * Multiply this matrix by the given matrix and return the result in a new Matrix.
   * @param other the matrix to multiply this one by.
   * @return a new matrix that is the product of this one with the given one.
   */
  public final Matrix times(final Matrix other) {
    if (this.ncol != other.nrow) {
      throw new IllegalArgumentException("The columns of this matrix must equal the rows of the other matrix. "
          + "This matrix has " + this.ncol + " columns and the other matrix has " + other.nrow + " rows.");
    }
    final double[] product = new double[this.nrow * other.ncol];
    for (int i = 0; i < this.nrow; i++) {
      for (int j = 0; j < other.ncol; j++) {
        for (int k = 0; k < this.ncol; k++) {
          product[i * this.nrow + j] += this.data[i * this.ncol + k] * other.data[j + k * other.ncol];
        }
      }
    }
    return new Matrix(this.nrow, other.ncol, product);
  }
  
  public final Vector times(final Vector vector) {
    double[] elements = vector.elements();
    if (this.ncol != elements.length) {
      throw new IllegalArgumentException("The columns of this matrix must equal the rows of the vector. "
          + "This matrix has " + this.ncol + " columns and the vector has " + elements.length + " rows.");
    }
    final double[] product = new double[this.nrow];
    for (int i = 0; i < this.nrow; i++) {
        for (int k = 0; k < this.ncol; k++) {
          product[i] += this.data[i * this.ncol + k] * elements[k];
        }
    }
    return new Vector(product);
  }

  public final Matrix scaledBy(final double c) {
    final double[] scaled = new double[this.data.length];
    for (int i = 0; i < this.data.length; i++) {
      scaled[i] = this.data[i] * c;
    }
    return new Matrix(this.nrow, this.ncol, scaled);
  }
  
  public final Matrix minus(final Matrix other) {
    if (this.nrow != other.nrow || this.ncol != other.ncol) {
      throw new IllegalArgumentException("The dimensions of this matrix must equal the dimensions of the other matrix. "
          + "This matrix has dimension (" + this.nrow + ", " + this.ncol + ") and the other matrix has dimension (" + other.nrow
          + ", " + other.ncol + ")");
    }
    final double[] minus = new double[nrow * ncol];
    for (int i = 0; i < nrow; i++) {
      for (int j = 0; j < ncol; j++) {
        minus[i * ncol + j] = this.data[i * ncol + j] - other.data[i * ncol + j];
      }
    }
    return new Matrix(this.nrow, this.ncol, minus);
  }
  
  public final double[] diagonal() {
    final double[] diag = new double[Math.min(nrow, ncol)];
    System.arraycopy(data, 0, diag, 0, diag.length);
    return diag;
  }
  
  public final double[] data() {
    return this.data.clone();
  }


  public final double[][] data2D() {
    final double[][] twoD = new double[this.nrow][this.ncol];
    for (int i = 0; i < nrow; i++) {
      System.arraycopy(this.data, i * ncol, twoD[i], 0, ncol);
    }
    return twoD;
  }

  @Override
  public String toString() {
    return "Matrix{" +
        "nrow=" + nrow +
        ", ncol=" + ncol +
        ", data=" + Arrays.toString(data) +
        '}';
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Matrix matrix = (Matrix) o;

    if (nrow != matrix.nrow) return false;
    return ncol == matrix.ncol && Arrays.equals(data, matrix.data);

  }

  @Override
  public int hashCode() {
    int result = nrow;
    result = 31 * result + ncol;
    result = 31 * result + Arrays.hashCode(data);
    return result;
  }

  /**
   * A class that allows one to start with an identity matrix, then set specific elements before creating
   * an immutable Matrix.
   * @author Jacob Rachiele
   *
   */
  public static final class IdentityBuilder {
    
    final int n;
    final double[] data;
    
    public IdentityBuilder(final int n) {
      this.n = n;
      this.data = new double[n * n];
      for (int i = 0; i < n; i++) {
        this.data[i * n + i] = 1.0;
      }
    }
    
    public final IdentityBuilder set(final int i, final int j, final double value) {
      this.data[i * n + j] = value;
      return this;
    }
    
    public final Matrix build() {
      return new Matrix(n, n, data);
    }
  }

}
