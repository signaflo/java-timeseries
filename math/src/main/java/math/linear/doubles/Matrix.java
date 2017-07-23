package math.linear.doubles;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jul. 23, 2017
 */
public interface Matrix {
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
    MatrixOneD plus(MatrixOneD other);

    /**
     * Multiply this matrix by the given matrix and return the resulting product.
     *
     * @param other the matrix to multiply by.
     * @return the product of this matrix with the given matrix.
     */
    Matrix times(MatrixOneD other);

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
    MatrixOneD scaledBy(double c);

    /**
     * Subtract the given matrix from this matrix and return the resulting difference.
     *
     * @param other the matrix to subtract from this one.
     * @return the difference of this matrix and the given matrix.
     */
    MatrixOneD minus(MatrixOneD other);

    /**
     * Transpose this matrix and return the resulting transposition.
     *
     * @return the transpose of this matrix.
     */
    Matrix transpose();

    Vector getRow(int i);

    Vector getColumn(int j);

    Matrix push(double[] newData, boolean byRow);

    /**
     * Retrieve the elements on the diagonal of this matrix.
     *
     * @return the elements on the diagonal of this matrix.
     */
    @SuppressWarnings("ManualArrayCopy")
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
    double[][] data2D(MatrixOneD.Order order);

    /**
     * Obtain the data in this matrix as a two-dimensional array.
     *
     * @return the data in this matrix as a two-dimensional array.
     */
    double[][] data2D();
}
