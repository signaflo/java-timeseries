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
    this.nrow = nrow;
    this.ncol = ncol;
    this.data = data.clone();
  }
  
  public Matrix(final double[][] matrixData) {
    this.nrow = matrixData.length;
    this.ncol = matrixData[0].length;
    this.data = new double[nrow * ncol];
    for (int i = 0; i < nrow; i++) {
      for (int j = 0; j < ncol; j++) {
        this.data[i * nrow + j] = matrixData[i][j];
      }
    }
  }
  
  public final Matrix plus(final Matrix other) {
    if (this.nrow != other.nrow && this.ncol != other.ncol) {
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
    if (this.nrow != other.nrow && this.ncol != other.ncol) {
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
  
  public final double[] data() {
    return this.data.clone();
  }

  @Override
  public String toString() {
    return "Matrix [nrow=" + nrow + ", ncol=" + ncol + ", data=" + Arrays.toString(data) + "]";
  }
  
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
    
    public final IdentityBuilder setElement(final int i, final int j, final double value) {
      this.data[i * n + j] = value;
      return this;
    }
    
    public final Matrix build() {
      return new Matrix(n, n, data);
    }
  }

}
