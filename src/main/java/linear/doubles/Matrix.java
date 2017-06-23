/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */
package linear.doubles;

import java.util.Arrays;

/**
 * An immutable and thread-safe implementation of a real-valued matrix.
 *
 * @author jrachiele
 */
public final class Matrix {

    public enum Order {
        ROW_MAJOR,
        COLUMN_MAJOR
    }

    private final int nrow;
    private final int ncol;
    private final double[] data;

    /**
     * Create a new matrix with the supplied data and dimensions. The data is assumed to be in row-major order.
     *
     * @param nrow the number of rows for the matrix.
     * @param ncol the number of columns for the matrix.
     * @param data the data in row-major order.
     */
    Matrix(final int nrow, final int ncol, final double... data) {
        if (nrow * ncol != data.length) {
            throw new IllegalArgumentException(
                    "The dimensions do not match the amount of data provided. " + "There were " + data.length +
                    " data points provided but the number of rows and columns " + "were " + nrow + " and " + ncol +
                    " respectively.");
        }
        this.nrow = nrow;
        this.ncol = ncol;
        this.data = data.clone();
    }

    public int nrow() {
        return this.nrow;
    }

    public int ncol() {
        return this.ncol;
    }

    /**
     * Create a new matrix with the given dimensions filled with the supplied value.
     *
     * @param nrow  the number of rows for the matrix.
     * @param ncol  the number of columns for the matrix.
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
     *
     * @param matrixData the two-dimensional array of data constituting the matrix.
     * @param order the storage order of the elements in the matrix data.
     */
    public Matrix(final double[][] matrixData, Order order) {
        if (matrixData.length == 0) {
            //throw new IllegalArgumentException("The matrix data cannot be empty.");
            this.ncol = 0;
            this.nrow = 0;
            this.data = new double[0];
        }
        else if (order == Order.COLUMN_MAJOR) {
            this.ncol = matrixData.length;
            this.nrow = matrixData[0].length;
            this.data = new double[ncol * nrow];
            for (int i = 0; i < nrow; i++) {
                for (int j = 0; j < ncol; j++) {
                    this.data[i * ncol + j] = matrixData[j][i];
                }
            }
        } else {
            this.nrow = matrixData.length;
            this.ncol = matrixData[0].length;
            this.data = new double[nrow * ncol];
            for (int i = 0; i < nrow; i++) {
                System.arraycopy(matrixData[i], 0, this.data, i * ncol, ncol);
            }

        }
    }

    /**
     * Create a new matrix with the supplied data and dimensions. The data is assumed to be in row-major order.
     *
     * @param nrow the number of rows for the matrix.
     * @param ncol the number of columns for the matrix.
     * @param data the data in row-major order.
     * @return a new matrix with the supplied data and dimensions.
     */
    public static Matrix create(final int nrow, final int ncol, final double[] data) {
        return new Matrix(nrow, ncol, data);
    }

    /**
     * Add this matrix to the given matrix and return the resulting sum.
     *
     * @param other the matrix to add to this one.
     * @return this matrix added to the other matrix.
     */
    public Matrix plus(final Matrix other) {
        if (this.nrow != other.nrow || this.ncol != other.ncol) {
            throw new IllegalArgumentException(
                    "The dimensions of this matrix must equal the dimensions of the other matrix. " +
                    "This matrix has dimension (" + this.nrow + ", " + this.ncol +
                    ") and the other matrix has dimension (" + other.nrow + ", " + other.ncol + ")");
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
     *
     * @param other the matrix to multiply by.
     * @return the product of this matrix with the given matrix.
     */
    public Matrix times(final Matrix other) {
        if (this.ncol != other.nrow) {
            throw new IllegalArgumentException(
                    "The columns of this matrix must equal the rows of the other matrix. " + "This matrix has " +
                    this.ncol + " columns and the other matrix has " + other.nrow + " rows.");
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
     *
     * @param vector the vector to transform.
     * @return the given vector transformed by this matrix.
     */
    public Vector times(final Vector vector) {
        double[] elements = vector.elements();
        if (this.ncol != elements.length) {
            throw new IllegalArgumentException(
                    "The columns of this matrix must equal the rows of the vector. " + "This matrix has " + this.ncol +
                    " columns and the vector has " + elements.length + " rows.");
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
     *
     * @param c the value to scale this matrix by.
     * @return this matrix scaled by the given value.
     */
    public Matrix scaledBy(final double c) {
        final double[] scaled = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++) {
            scaled[i] = this.data[i] * c;
        }
        return new Matrix(this.nrow, this.ncol, scaled);
    }

    /**
     * Subtract the given matrix from this matrix and return the resulting difference.
     *
     * @param other the matrix to subtract from this one.
     * @return the difference of this matrix and the given matrix.
     */
    public Matrix minus(final Matrix other) {
        if (this.nrow != other.nrow || this.ncol != other.ncol) {
            throw new IllegalArgumentException(
                    "The dimensions of this matrix must equal the dimensions of the other matrix. " +
                    "This matrix has dimension (" + this.nrow + ", " + this.ncol +
                    ") and the other matrix has dimension (" + other.nrow + ", " + other.ncol + ")");
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
     *
     * @return true if the matrix is square and false otherwise.
     */
    boolean isSquare() {
        return this.nrow == this.ncol;
    }

    /**
     * Transpose this matrix and return the resulting transposition.
     *
     * @return the transpose of this matrix.
     */
    public Matrix transpose() {
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
     *
     * @return the elements on the diagonal of this matrix.
     */
    @SuppressWarnings("ManualArrayCopy")
    public double[] diagonal() {
        final double[] diag = new double[Math.min(nrow, ncol)];
        for (int i = 0; i < diag.length; i++) {
            diag[i] = data[ncol * i + i];
        }
        return diag;
    }

    /**
     * Obtain the array of data underlying this matrix in row-major order.
     *
     * @return the array of data underlying this matrix in row-major order.
     */
    public double[] data() {
        return this.data.clone();
    }

    /**
     * Obtain the data in this matrix as a two-dimensional array.
     *
     * @param order the storage order of the elements in the matrix data.
     *
     * @return the data in this matrix as a two-dimensional array.
     */
    public double[][] data2D(Order order) {
        if (order == Order.ROW_MAJOR) {
            return data2DRowMajor();
        }
        return data2DColumnMajor();
    }

    private double[][] data2DRowMajor() {
        final double[][] twoD = new double[this.nrow][this.ncol];
        for (int i = 0; i < nrow; i++) {
            System.arraycopy(this.data, i * ncol, twoD[i], 0, ncol);
        }
        return twoD;
    }

    private double[][] data2DColumnMajor() {
        final double[][] twoD = new double[this.ncol][this.nrow];
        for (int i = 0; i < ncol; i++) {
            for (int j = 0; j < nrow; j++) {
                twoD[i][j] = this.data[i + j * ncol];
            }
        }
        return twoD;
    }

    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        StringBuilder representation = new StringBuilder();
        double[][] twoD = data2D(Order.ROW_MAJOR);
        for (int i = 0; i < this.nrow; i++) {
            representation.append(Arrays.toString(twoD[i])).append(newLine);
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
     *
     * @author Jacob Rachiele
     */
    public static final class IdentityBuilder {

        final int n;
        final double[] data;

        /**
         * Create a new builder with the given dimension.
         *
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
         *
         * @param i     the row to set the value at.
         * @param j     the column to set the value at.
         * @param value the value to set.
         * @return the builder with the value set at the given coordinates.
         */
        public IdentityBuilder set(final int i, final int j, final double value) {
            this.data[i * n + j] = value;
            return this;
        }

        /**
         * Create a new matrix using the data in this builder.
         *
         * @return a new matrix from this builder.
         */
        public Matrix build() {
            return new Matrix(n, n, data);
        }
    }

}
