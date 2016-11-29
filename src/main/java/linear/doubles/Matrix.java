/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package linear.doubles;

import java.util.Arrays;

/**
 * An immutable and thread-safe implementation of a real-valued matrix.
 * @author jrachiele
 *
 */
public final class Matrix {
  
  private final int nrow;
  private final int ncol;
  private final double[] data;

  /**
   * Create a new matrix with the supplied data and dimensions. The data is assumed to be in row-major order.
   * @param nrow the number of rows for the matrix.
   * @param ncol the number of columns for the matrix.
   * @param data the data in row-major order.
   */
  public Matrix(final int nrow, final int ncol, final double... data) {
    if (nrow * ncol != data.length) {
      throw new IllegalArgumentException("The dimensions do not match the amount of data provided. " +
          "There were " + data.length + " data points provided but the number of rows and columns " +
          "were " + nrow + " and " + ncol + " respectively.");
    }
    this.nrow = nrow;
    this.ncol = ncol;
    this.data = data.clone();
  }

  /**
   * Create a new matrix with the supplied data and dimensions. The data is assumed to be in row-major order.
   * @param nrow the number of rows for the matrix.
   * @param ncol the number of columns for the matrix.
   * @param data the data in row-major order.
   * @return a new matrix with the supplied data and dimensions.
   */
  public static Matrix create(final int nrow, final int ncol, final double[] data) {
    return new Matrix(nrow, ncol, data);
  }

  /**
   * Create a new matrix with the given dimensions filled with the supplied value.
   * @param nrow the number of rows for the matrix.
   * @param ncol the number of columns for the matrix.
   * @param value the data point to fill the matrix with.
   */
  public Matrix(final int nrow, final int ncol, final double value) {
    this.nrow = nrow;
    this.ncol = ncol;
    this.data = new double[nrow * ncol];
    for (int i = 0; i < data.length; i++) {
      this.data[i] = value;
    }
  }

  /**
   * Create a new matrix from the given two-dimensional array of data.
   * @param matrixData the two-dimensional array of data constituting the matrix.
   */
  public Matrix(final double[][] matrixData) {
    this.nrow = matrixData.length;
    this.ncol = matrixData[0].length;
    this.data = new double[nrow * ncol];
    for (int i = 0; i < nrow; i++) {
      System.arraycopy(matrixData[i], 0, this.data, i * ncol, ncol);
    }
  }

  /**
   * Add this matrix to the given matrix and return the resulting sum.
   * @param other the matrix to add to this one.
   * @return this matrix added to the other matrix.
   */
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
   * Multiply this matrix by the given matrix and return the resulting product.
   * @param other the matrix to multiply by.
   * @return the product of this matrix with the given matrix.
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

  /**
   * Transform the given vector with this matrix and return the resulting transformation.
   * @param vector the vector to transform.
   * @return the given vector transformed by this matrix.
   */
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

  /**
   * Scale this matrix by the given value and return the scaled matrix.
   * @param c the value to scale this matrix by.
   * @return this matrix scaled by the given value.
   */
  public final Matrix scaledBy(final double c) {
    final double[] scaled = new double[this.data.length];
    for (int i = 0; i < this.data.length; i++) {
      scaled[i] = this.data[i] * c;
    }
    return new Matrix(this.nrow, this.ncol, scaled);
  }

  /**
   * Subtract the given matrix from this matrix return the resulting difference.
   * @param other the matrix to subtract from this one.
   * @return the difference of this matrix and the given matrix.
   */
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

  /**
   * Returns true if the matrix is square and false otherwise.
   * @return true if the matrix is square and false otherwise.
   */
  public final boolean isSquare() {
    return this.nrow == this.ncol;
  }

  /**
   * Transpose this matrix and return the resulting transposition.
   * @return the transpose of this matrix.
   */
  public final Matrix transpose() {
    final double[] tData = new double[this.data.length];
    for (int i = 0; i < this.nrow; i++) {
      for (int j = 0; j < this.ncol; j++) {
        tData[i + j * this.nrow] = this.data[j + i * ncol];
      }
    }
    return new Matrix(this.ncol, this.nrow, tData);
  }

  /**
   * Retrieve the elements on the diagonal of this matrix.
   * @return the elements on the diagonal of this matrix.
   */
  @SuppressWarnings("ManualArrayCopy")
  public final double[] diagonal() {
    final double[] diag = new double[Math.min(nrow, ncol)];
    for (int i = 0; i < diag.length; i++) {
      diag[i] = data[ncol * i + i];
    }
    return diag;
  }

  /**
   * Obtain the array of data underlying this matrix.
   * @return the array of data underlying this matrix.
   */
  public final double[] data() {
    return this.data.clone();
  }

  /**
   * Obtain the data in this matrix as a two-dimensional array.
   * @return the data in this matrix as a two-dimensional array.
   */
  final double[][] data2D() {
    final double[][] twoD = new double[this.nrow][this.ncol];
    for (int i = 0; i < nrow; i++) {
      System.arraycopy(this.data, i * ncol, twoD[i], 0, ncol);
    }
    return twoD;
  }

  @Override
  public String toString() {
    StringBuilder representation = new StringBuilder();
    double[][] twoD = data2D();
    for (int i = 0; i < this.nrow; i++) {
      representation.append(Arrays.toString(twoD[i])).append("\n");
    }
    return representation.toString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Matrix matrix = (Matrix) o;
    return nrow == matrix.nrow && ncol == matrix.ncol && Arrays.equals(data, matrix.data);
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

    /**
     * Create a new builder with the given dimension.
     * @param n the dimension of the matrix.
     */
    public IdentityBuilder(final int n) {
      this.n = n;
      this.data = new double[n * n];
      for (int i = 0; i < n; i++) {
        this.data[i * n + i] = 1.0;
      }
    }

    /**
     * Set the matrix at the given coordinates to the provided value and return the builder.
     * @param i the row to set the value at.
     * @param j the column to set the value at.
     * @param value the value to set.
     * @return the builder with the value set at the given coordinates.
     */
    public final IdentityBuilder set(final int i, final int j, final double value) {
      this.data[i * n + j] = value;
      return this;
    }

    /**
     * Create a new matrix using the data in this builder.
     * @return a new matrix from this builder.
     */
    public final Matrix build() {
      return new Matrix(n, n, data);
    }
  }

}
