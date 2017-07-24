package math.linear.doubles;

/**
 * A builder for a {@link Matrix} object.
 *
 * @author Jacob Rachiele
 * Jul. 24, 2017
 */
public interface MatrixBuilder {
    /**
     * Set the matrix at the given coordinates to the provided value and return the builder.
     *
     * @param i     the row to set the value at.
     * @param j     the column to set the value at.
     * @param value the value to set.
     * @return the builder with the value set at the given coordinates.
     */
    MatrixBuilder set(int i, int j, double value);

    /**
     * Create a new matrix using the data in this builder.
     *
     * @return a new matrix from this builder.
     */
    Matrix build();
}
