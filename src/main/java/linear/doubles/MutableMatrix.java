package linear.doubles;

/**
 * A matrix whose internal data may change when acted on.
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
public final class MutableMatrix {
  
  private final int nrow;
  private final int ncol;
  private final double[] data;
  
  public MutableMatrix(final int nrow, final int ncol, final double... data) {
    this.nrow = nrow;
    this.ncol = ncol;
    this.data = data.clone();
  }
  
  public MutableMatrix(final double[][] matrixData) {
    this.nrow = matrixData.length;
    this.ncol = matrixData[0].length;
    this.data = new double[nrow * ncol];
    for (int i = 0; i < nrow; i++) {
      for (int j = 0; j < ncol; j++) {
        this.data[i * nrow + j] = matrixData[i][j];
      }
    }
  }
  
  public final MutableMatrix plus(final MutableMatrix other) {
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
    return new MutableMatrix(this.nrow, this.ncol, sum);
  }
  
  public final MutableMatrix times(final MutableMatrix other) {
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
    return new MutableMatrix(this.nrow, other.ncol, product);
  }
  
  public final void scaledBy(final double c) {
    for (int i = 0; i < this.data.length; i++) {
      this.data[i] += c;
    }
  }
  
  public final double[] data() {
    return this.data.clone();
  }

}
