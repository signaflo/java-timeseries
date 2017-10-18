package com.github.signaflo.math.linear.doubles;

/**
 * A real-valued matrix.
 *
 * @author Jacob Rachiele
 * Jul. 23, 2017
 */
public interface Matrix {
    /**
     * Create a new matrix with the supplied com.github.signaflo.data and dimensions. The com.github.signaflo.data is assumed to be in row-major order.
     *
     * @param nrow the number of rows for the matrix.
     * @param ncol the number of columns for the matrix.
     * @param data the com.github.signaflo.data in row-major order.
     * @return a new matrix with the supplied com.github.signaflo.data and dimensions.
     *
     * @throws IllegalArgumentException if the length of the com.github.signaflo.data array does not equal the given number of rows
     * multiplied by the given number of columns.
     */
    static Matrix create(final int nrow, final int ncol, final double... data) {
        return new MatrixOneD(nrow, ncol, data);
    }

    /**
     * Create a new matrix with the given dimensions filled with the supplied value.
     *
     * @param nrow  the number of rows for the matrix.
     * @param ncol  the number of columns for the matrix.
     * @param value the com.github.signaflo.data point to fill the matrix with.
     * @return a new matrix with the given dimensions filled with the provided value.
     */
    static Matrix fill(final int nrow, final int ncol, final double value) {
        return new MatrixOneD(nrow, ncol, value);
    }

    /**
     * Create a new matrix from the given two-dimensional array of com.github.signaflo.data, assuming that the
     * com.github.signaflo.data is laid out by row. If the com.github.signaflo.data is instead laid out by column, then specify that
     * by supplying {@link Layout#BY_COLUMN} as the second argument to this method.
     *
     * @param matrixData the two-dimensional array of com.github.signaflo.data constituting the matrix.
     * @return a new matrix with the given com.github.signaflo.data.
     */
    static Matrix create(final double[]... matrixData) {
        return new MatrixOneD(Layout.BY_ROW, matrixData);
    }

    /**
     * Create a new matrix from the given two-dimensional array of com.github.signaflo.data.
     *
     * @param layout     the layout of the elements in the matrix com.github.signaflo.data.
     * @param matrixData the two-dimensional array of com.github.signaflo.data constituting the matrix.
     * @return a new matrix with the given com.github.signaflo.data and layout.
     */
    static Matrix create(Layout layout, final double[]... matrixData) {
        return new MatrixOneD(layout, matrixData);
    }

    /**
     * Create a new identity matrix with the given dimension.
     *
     * @param n the dimension of the identity matrix.
     * @return a new identity matrix with the given dimension.
     */
    static Matrix identity(final int n) {
        final double[] data = new double[n * n];
        for (int i = 0; i < n; i++) {
            data[i * n + i] = 1.0;
        }
        return new MatrixOneD(n, n, data);
    }

    /**
     * Get the element at row i, column j.
     *
     * @param i the element's row location.
     * @param j the element's column location.
     * @return the element at row i, column j.
     */
    double get(int i, int j);

    /**
     * Retrieve the number of rows of this matrix.
     *
     * @return the number of rows of this matrix.
     */
    int nrow();

    /**
     * Retrieve the number of columns of this matrix.
     *
     * @return the number of columns of this matrix.
     */
    int ncol();

    /**
     * Add this matrix to the given matrix and return the resulting sum.
     *
     * @param other the matrix to add to this one.
     * @return this matrix added to the other matrix.
     *
     * @throws IllegalArgumentException if the dimensions of this matrix do not
     * match the dimensions of the given matrix.
     */
    Matrix plus(Matrix other);

    /**
     * Multiply this matrix by the given matrix and return the resulting product.
     *
     * @param other the matrix to multiply by.
     * @return the product of this matrix with the given matrix.
     *
     * @throws IllegalArgumentException if the number of columns of this matrix does not
     * equal the number of rows of the given matrix.
     */
    Matrix times(Matrix other);

    /**
     * Multiply this matrix by the given vector and return the resulting transformation.
     *
     * @param vector the vector to multiply.
     * @return the given vector multiplied by this matrix.
     *
     * @throws IllegalArgumentException if the number of columns of this matrix does not
     * equal the size of the given vector.
     */
    Vector times(Vector vector);

    /**
     * Scale this matrix by the given value and return the scaled matrix.
     *
     * @param c the value to scale this matrix by.
     * @return this matrix scaled by the given value.
     */
    Matrix scaledBy(double c);

    /**
     * Subtract the given matrix from this matrix and return the resulting difference.
     *
     * @param other the matrix to subtract from this one.
     * @return the difference of this matrix and the given matrix.
     *
     * @throws IllegalArgumentException if the dimensions of this matrix do not
     * match the dimensions of the given matrix.
     */
    Matrix minus(Matrix other);

    /**
     * Returns true if the matrix is square and false otherwise.
     *
     * @return true if the matrix is square and false otherwise.
     */
    boolean isSquare();

    /**
     * Transpose this matrix and return the resulting transposition.
     *
     * @return the transpose of this matrix.
     */
    Matrix transpose();

    /**
     * Get the ith row of the matrix.
     *
     * @param i the row index, where indexing begins at 0.
     * @return the ith row of the matrix.
     */
    Vector getRow(int i);

    /**
     * Get the jth column of the matrix.
     *
     * @param j the column index, where indexing begins at 0.
     * @return the jth column of the matrix.
     */
    Vector getColumn(int j);

    /**
     * Push the provided vector to the top of this matrix, shifting all other vectors down.
     *
     * @param newData the new vector of com.github.signaflo.data.
     * @return a matrix with the given vector pushed to the top of this matrix.
     *
     * @throws IllegalArgumentException if size of the given vector does not equal the number of columns of this matrix.
     */
    Matrix pushRow(Vector newData);

    /**
     * Push the provided vector to the front of this matrix, shifting all other vectors to the right.
     *
     * @param newData the new vector of com.github.signaflo.data.
     * @return a matrix with the given vector pushed to the front of this matrix.
     *
     * @throws IllegalArgumentException if size of the given vector does not equal the number of rows of this matrix.
     */
    Matrix pushColumn(Vector newData);

    /**
     * Retrieve the elements on the diagonal of this matrix.
     *
     * @return the elements on the diagonal of this matrix.
     */
    double[] diagonal();

    /**
     * Obtain the array of com.github.signaflo.data underlying this matrix in row-major order.
     *
     * @return the array of com.github.signaflo.data underlying this matrix in row-major order.
     */
    double[] data();

    /**
     * Obtain the com.github.signaflo.data in this matrix as a two-dimensional array.
     *
     * @param layout the layout of the elements in the matrix com.github.signaflo.data.
     * @return the com.github.signaflo.data in this matrix as a two-dimensional array.
     */
    double[][] data2D(Layout layout);

    /**
     * Obtain the com.github.signaflo.data in this matrix as a two-dimensional array.
     *
     * @return the com.github.signaflo.data in this matrix as a two-dimensional array.
     */
    double[][] data2D();

    /**
     * Specifies the layout of the two-dimensional array representation of a matrix. In other words, this
     * specifies whether the outer part of the two-dimensional array is a sequence of row vectors or a sequence
     * of column vectors.
     */
    enum Layout {
        BY_ROW, BY_COLUMN
    }

    /**
     * Create a new builder for a square matrix of size n with ones on the diagonal.
     *
     * @param n the dimension of the matrix.
     * @return a new builder for a square matrix of size n with ones on the diagonal.
     */
    static MatrixBuilder identityBuilder(final int n) {
        return new MatrixOneD.IdentityBuilder(n);
    }

    /**
     * Create a new builder for an m by n matrix with all elements initially set to zero.
     *
     * @param m the number of columns of the matrix.
     * @param n the the number of columns of the matrix.
     * @return a new builder with all elements initially set to zero.
     */
    static MatrixBuilder builder(final int m, int n) {
        return new MatrixOneD.ZeroBuilder(m, n);
    }

}
