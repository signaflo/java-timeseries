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
package math.linear.doubles;

import java.util.Arrays;

/**
 * An immutable and thread-safe implementation of a real-valued matrix.
 *
 * @author jrachiele
 */
public final class MatrixOneD implements Matrix {

    /**
     * <p>
     *     The storage order of the two-dimensional array representation of a matrix.
     * </p>
     *
     * <p>
     *     Note that the enclosing MatrixOneD class always stores its data internally as a one-dimensional
     *     array in <a target="_blank" href="https://en.wikipedia.org/wiki/Row-_and_column-major_order">
     *     row-major order</a>. However, it accepts two-dimensional arrays in constructors, returns a two-dimensional
     *     array view of itself, and uses the two-dimensional representation in its toString method. For these reasons,
     *     it needs to know whether the data in the two-dimensional array representation is stored row-by-row or
     *     column-by-column. In other words, it needs to know whether the data in the outer array is to viewed
     *     as an array of row vectors or an array of column vectors.
     * </p>
     */
    public enum Order {
        BY_ROW, BY_COLUMN
    }

    private final int nrow;
    private final int ncol;
    private final double[] data;
    private final Order order;

    /**
     * Create a new matrix with the supplied data and dimensions. The data is assumed to be in row-major order.
     *
     * @param nrow the number of rows for the matrix.
     * @param ncol the number of columns for the matrix.
     * @param data the data in row-major order.
     */
    MatrixOneD(final int nrow, final int ncol, final double... data) {
        if (nrow * ncol != data.length) {
            throw new IllegalArgumentException(
                    "The dimensions do not match the amount of data provided. " + "There were " + data.length +
                    " data points provided but the number of rows and columns " + "were " + nrow + " and " + ncol +
                    " respectively.");
        }
        this.nrow = nrow;
        this.ncol = ncol;
        this.data = data.clone();
        this.order = Order.BY_ROW;
    }

    /**
     * Create a new matrix with the given dimensions filled with the supplied value.
     *
     * @param nrow  the number of rows for the matrix.
     * @param ncol  the number of columns for the matrix.
     * @param value the data point to fill the matrix with.
     */
    MatrixOneD(final int nrow, final int ncol, final double value) {
        this.nrow = nrow;
        this.ncol = ncol;
        this.data = new double[nrow * ncol];
        for (int i = 0; i < data.length; i++) {
            this.data[i] = value;
        }
        this.order = Order.BY_ROW;
    }

    /**
     * Create a new matrix from the given two-dimensional array of data.
     *
     * @param matrixData  the two-dimensional array of data constituting the matrix.
     * @param order the storage order of the elements in the supplied two dimensional array.
     */
    MatrixOneD(final double[][] matrixData, Order order) {
        this.order = order;
        if (matrixData.length == 0) {
            //throw new IllegalArgumentException("The matrix data cannot be empty.");
            this.ncol = 0;
            this.nrow = 0;
            this.data = new double[0];
        } else if (order == Order.BY_COLUMN) {
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
    public static MatrixOneD create(final int nrow, final int ncol, final double... data) {
        return new MatrixOneD(nrow, ncol, data);
    }

    /**
     * Create a new matrix with the given dimensions filled with the supplied value.
     *
     * @param nrow  the number of rows for the matrix.
     * @param ncol  the number of columns for the matrix.
     * @param value the data point to fill the matrix with.
     * @return a new matrix with the given dimensions filled with the provided value.
     */
    public static Matrix fill(final int nrow, final int ncol, final double value) {
        return new MatrixOneD(nrow, ncol, value);
    }

    /**
     * Create a new matrix from the given two-dimensional array of data, assuming that the
     * data is stored by row. If the data is instead stored by column, then specify that
     * by supplying {@link Order#BY_COLUMN} as the second argument to this method.
     *
     * @param matrixData  the two-dimensional array of data constituting the matrix.
     * @return a new matrix with the given data.
     */
    public static Matrix create(final double[]... matrixData) {
        return new MatrixOneD(matrixData, Order.BY_ROW);
    }

    /**
     * Create a new matrix from the given two-dimensional array of data.
     *
     * @param matrixData  the two-dimensional array of data constituting the matrix.
     * @param order the storage order of the elements in the matrix data.
     * @return a new matrix with the given data and order.
     */
    public static MatrixOneD create(final double[][] matrixData, Order order) {
        return new MatrixOneD(matrixData, order);
    }


    @Override
    public double get(int i, int j) {
        return this.data[i * ncol + j];
    }

    @Override
    public int nrow() {
        return this.nrow;
    }

    @Override
    public int ncol() {
        return this.ncol;
    }

    @Override
    public MatrixOneD plus(final MatrixOneD other) {
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
        return new MatrixOneD(this.nrow, this.ncol, sum);
    }

    @Override
    public Matrix times(final MatrixOneD other) {
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
        return new MatrixOneD(this.nrow, other.ncol, product);
    }

    @Override
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

    @Override
    public MatrixOneD scaledBy(final double c) {
        final double[] scaled = new double[this.data.length];
        for (int i = 0; i < this.data.length; i++) {
            scaled[i] = this.data[i] * c;
        }
        return new MatrixOneD(this.nrow, this.ncol, scaled);
    }

    @Override
    public MatrixOneD minus(final MatrixOneD other) {
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
        return new MatrixOneD(this.nrow, this.ncol, minus);
    }

    /**
     * Returns true if the matrix is square and false otherwise.
     *
     * @return true if the matrix is square and false otherwise.
     */
    boolean isSquare() {
        return this.nrow == this.ncol;
    }

    @Override
    public Matrix transpose() {
        final double[] transposedData = new double[this.data.length];
        for (int i = 0; i < this.nrow; i++) {
            for (int j = 0; j < this.ncol; j++) {
                transposedData[i + j * this.nrow] = this.data[j + i * ncol];
            }
        }
        return new MatrixOneD(this.ncol, this.nrow, transposedData);
    }

    @Override
    public Vector getRow(int i) {
        double[] row = new double[this.ncol];
        int offset = this.ncol * i;
        System.arraycopy(this.data, offset, row, 0, row.length);
        return Vector.from(row);
    }

    @Override
    public Vector getColumn(int j) {
        double[] col = new double[this.nrow];
        for (int i = 0; i < col.length; i++) {
            col[i] = this.data[i * this.ncol + j];
        }
        return Vector.from(col);
    }

    @Override
    public Matrix push(double[] newData, boolean byRow) {
        if (byRow) {
            return rowPush(newData);
        } else {
            return columnPush(newData);
        }
    }

    private Matrix columnPush(double[] newData) {
        double[][] thisData = data2D(Order.BY_COLUMN);
        double[][] newMatrix = new double[this.nrow + 1][];
        newMatrix[0] = newData.clone();
        for (int i = 1; i < newMatrix.length; i++) {
            newMatrix[i] = thisData[i - 1].clone();
        }
        return new MatrixOneD(newMatrix, Order.BY_COLUMN);
    }

    private Matrix rowPush(double[] newData) {
        if (newData.length != this.ncol) {
            throw new IllegalArgumentException("The number of elements of the new row must match the " +
                                               "number of columns of the matrix.");
        }
        double[] newMatrix = new double[newData.length + this.data.length];
        System.arraycopy(newData, 0, newMatrix, 0, newData.length);
        System.arraycopy(this.data, 0, newMatrix, newData.length, this.data.length);
        return new MatrixOneD(this.nrow + 1, this.ncol, newMatrix);
    }

    @Override
    @SuppressWarnings("ManualArrayCopy")
    public double[] diagonal() {
        final double[] diag = new double[Math.min(nrow, ncol)];
        for (int i = 0; i < diag.length; i++) {
            diag[i] = data[ncol * i + i];
        }
        return diag;
    }

    @Override
    public double[] data() {
        return this.data.clone();
    }

    @Override
    public double[][] data2D(Order order) {
        if (order == Order.BY_ROW) {
            return data2DRowMajor();
        }
        return data2DColumnMajor();
    }

    @Override
    public double[][] data2D() {
        if (this.order == Order.BY_ROW) {
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
        StringBuilder representation = new StringBuilder(newLine);
        double[][] twoD = data2D(Order.BY_ROW);
        for (int i = 0; i < this.nrow; i++) {
            representation.append(Arrays.toString(twoD[i])).append(newLine);
        }
        return representation.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatrixOneD matrix = (MatrixOneD) o;
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
     * an immutable MatrixOneD.
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
        public MatrixOneD build() {
            return new MatrixOneD(n, n, data);
        }
    }

    /**
     * A class that allows one to start with a zero matrix, then set specific elements before creating
     * an immutable MatrixOneD.
     *
     * @author Jacob Rachiele
     */
    public static final class Builder {

        final int n;
        final double[] data;

        /**
         * Create a new builder with the given dimension.
         *
         * @param n the dimension of the matrix.
         */
        public Builder(final int n) {
            this.n = n;
            this.data = new double[n * n];
        }

        /**
         * Set the matrix at the given coordinates to the provided value and return the builder.
         *
         * @param i     the row to set the value at.
         * @param j     the column to set the value at.
         * @param value the value to set.
         * @return the builder with the value set at the given coordinates.
         */
        public Builder set(final int i, final int j, final double value) {
            this.data[i * n + j] = value;
            return this;
        }

        /**
         * Create a new matrix using the data in this builder.
         *
         * @return a new matrix from this builder.
         */
        public Matrix build() {
            return new MatrixOneD(n, n, data);
        }
    }

}
