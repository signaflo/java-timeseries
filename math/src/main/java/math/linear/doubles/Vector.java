package math.linear.doubles;

import math.operations.DoubleFunctions;

/**
 * A mathematical vector.
 *
 * @author Jacob Rachiele
 * Jul. 23, 2017
 */
public interface Vector {

    /**
     * Create a new vector of ones with the given size.
     *
     * @param n the size of the new vector.
     * @return a new vector of ones with the given size.
     */
    static Vector ones(int n) {
        return new GenericVector(DoubleFunctions.fill(n, 1.0));
    }
    /**
     * Create a new vector from the given elements.
     *
     * @param elements the elements of the new vector.
     * @return a new vector with the given elements.
     */
    static Vector from(double... elements) {
        return new GenericVector(elements);
    }

    /**
     * The elements of the vector as an array of primitive doubles.
     *
     * @return the elements of the vector as an array of primitive doubles.
     */
    double[] elements();

    /**
     * Return the element at index i, where indexing begins at 0.
     *
     * @param i the index of the element.
     * @return the element at index i, where indexing begins at 0.
     */
    double at(int i);

    /**
     * The number of elements in this vector.
     *
     * @return the number of elements in this vector.
     */
    int size();

    /**
     * Add this vector to the given vector and return the resulting vector.
     *
     * @param other the vector to add to this vector.
     * @return this vector added to the given vector.
     */
    Vector plus(Vector other);

    /**
     * Subtract the given vector from this vector and return the resulting vector.
     *
     * @param other the vector to subtract from this vector.
     * @return this vector subtracted by the given vector.
     */
    Vector minus(Vector other);

    /**
     * Subtract the given scalar from this vector and return the resulting vector.
     *
     * @param scalar the scalar to subtract from this vector.
     * @return this vector subtracted by the given scalar.
     */
    Vector minus(double scalar);

    /**
     * Scale this vector by the given scalar and return the resulting vector.
     *
     * @param alpha the scalar to scale this vector by.
     * @return this vector scaled by the given scalar.
     */
    Vector scaledBy(double alpha);

    /**
     * Compute and return the dot product of this vector with the given vector.
     *
     * @param other the vector to take the dot product with.
     * @return the dot product of this vector with the given vector.
     */
    double dotProduct(Vector other);

    /**
     * Compute and return the outer product of this vector and the given vector.
     *
     * @param other the vector to compute the outer product of this vector with.
     * @return the outer product of this vector with the given vector.
     */
    Matrix outerProduct(Vector other);

    /**
     * Compute and return the L2 length of this vector.
     *
     * @return the L2 length of this vector.
     */
    double norm();

    /**
     * Compute and return the sum of the elements of this vector.
     *
     * @return the sum of the elements of this vector.
     */
    double sum();

    /**
     * Compute and return the sum of squared elements of this vector.
     *
     * @return the sum of squared elements of this vector.
     */
    double sumOfSquares();

    /**
     * Push the provided value to the front of this vector and return the resulting vector.
     *
     * @param value the value to push to the front of the vector.
     * @return a vector with the provided value pushed to the front of this vector.
     */
    Vector push(double value);
}
