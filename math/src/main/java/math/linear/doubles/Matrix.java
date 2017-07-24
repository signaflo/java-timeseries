package math.linear.doubles;

/**
 * A real-valued matrix.
 *
 * @author Jacob Rachiele
 *         Jul. 23, 2017
 */
public interface Matrix {
    /**
     * Create a new matrix with the supplied data and dimensions. The data is assumed to be in row-major order.
     *
     * @param nrow the number of rows for the matrix.
     * @param ncol the number of columns for the matrix.
     * @param data the data in row-major order.
     * @return a new matrix with the supplied data and dimensions.
     */
    static Matrix create(final int nrow, final int ncol, final double... data) {
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
    static Matrix fill(final int nrow, final int ncol, final double value) {
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
    static Matrix create(final double[]... matrixData) {
        return new MatrixOneD(matrixData, Order.BY_ROW);
    }

    /**
     * Create a new matrix from the given two-dimensional array of data.
     *
     * @param matrixData  the two-dimensional array of data constituting the matrix.
     * @param order the storage order of the elements in the matrix data.
     * @return a new matrix with the given data and order.
     */
    static Matrix create(final double[][] matrixData, Order order) {
        return new MatrixOneD(matrixData, order);
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
     */
    Matrix plus(Matrix other);

    /**
     * Multiply this matrix by the given matrix and return the resulting product.
     *
     * @param other the matrix to multiply by.
     * @return the product of this matrix with the given matrix.
     */
    Matrix times(Matrix other);

    /**
     * Multiply this matrix by the given vector and return the resulting transformation.
     *
     * @param vector the vector to multiply.
     * @return the given vector multiplied by this matrix.
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
     * Push the provided vector to the top or front of this matrix, shifting all other vectors down or to the right.
     *
     * @param newData the new vector of data.
     * @param isRow whether the provided vector is a row vector or not.
     * @return a matrix with the given vector pushed to the top or front of this matrix.
     */
    Matrix push(Vector newData, boolean isRow);

    /**
     * Retrieve the elements on the diagonal of this matrix.
     *
     * @return the elements on the diagonal of this matrix.
     */
    double[] diagonal();

    /**
     * Obtain the array of data underlying this matrix in row-major order.
     *
     * @return the array of data underlying this matrix in row-major order.
     */
    double[] data();

    /**
     * Obtain the data in this matrix as a two-dimensional array.
     *
     * @param order the storage order of the elements in the matrix data.
     * @return the data in this matrix as a two-dimensional array.
     */
    double[][] data2D(Matrix.Order order);

    /**
     * Obtain the data in this matrix as a two-dimensional array.
     *
     * @return the data in this matrix as a two-dimensional array.
     */
    double[][] data2D();

    /**
     * <p>
     *     The storage order of the two-dimensional array representation of a matrix.
     * </p>
     *
     * <p>
     *     Note that a matrix implementation class may store its data internally as a one-dimensional
     *     array. However, it may also accept two-dimensional arrays in constructors, return a two-dimensional
     *     array view of itself, or use the two-dimensional representation in its toString method. For these reasons,
     *     it needs to know whether the data in the two-dimensional array representation is stored row-by-row or
     *     column-by-column. In other words, it needs to know whether the data in the outer array is to viewed
     *     as an array of row vectors or an array of column vectors.
     * </p>
     */
    enum Order {
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
     * @param m the number of rows of the matrix.
     * @param n the the number of columns of the matrix.
     * @return a new builder with all elements initially set to zero.
     */
    static MatrixBuilder builder(final int m, int n) {
        return new MatrixOneD.ZeroBuilder(m, n);
    }

}
