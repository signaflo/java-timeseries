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

import math.stats.Statistics;

import java.util.Arrays;

/**
 * An immutable and thread-safe implementation of a real-valued vector backed by an array of primitive doubles.
 *
 * @author Jacob Rachiele
 */
public final class Vector {

    private final double[] elements;

    /**
     * Create a new vector using the provided elements.
     *
     * @param elements the elements of the new vector.
     */
    public Vector(double... elements) {
        this.elements = elements.clone();
    }

    /**
     * Create a new vector from the given elements.
     *
     * @param elements the elements of the new vector.
     * @return a new vector with the given elements.
     */
    public static Vector from(double... elements) {
        return new Vector(elements);
    }

    /**
     * The elements of the vector as an array of primitive doubles.
     *
     * @return the elements of the vector as an array of primitive doubles.
     */
    public double[] elements() {
        return this.elements.clone();
    }

    /**
     * Return the element at index i, where indexing begins at 0.
     *
     * @param i the index of the element.
     * @return the element at index i, where indexing begins at 0.
     */
    public double at(final int i) {
        return this.elements[i];
    }

    /**
     * The number of elements in this vector.
     *
     * @return the number of elements in this vector.
     */
    public int size() {
        return this.elements.length;
    }

    /**
     * Add this vector to the given vector and return the result in a new vector.
     *
     * @param other the vector to add to this vector.
     * @return this vector added to the given vector.
     */
    public Vector plus(final Vector other) {
        if (other.elements().length == 0) {
            return this;
        }
        final double[] summed = new double[this.size()];
        for (int i = 0; i < summed.length; i++) {
            summed[i] = this.elements[i] + other.elements[i];
        }
        return new Vector(summed);
    }

    /**
     * Subtract the given vector from this vector and return the result in a new vector.
     *
     * @param other the vector to subtract from this vector.
     * @return this vector subtracted by the given vector.
     */
    public Vector minus(final Vector other) {
        final double[] differenced = new double[this.size()];
        for (int i = 0; i < differenced.length; i++) {
            differenced[i] = this.elements[i] - other.elements[i];
        }
        return new Vector(differenced);
    }

    /**
     * Subtract the given scalar from this vector and return the result in a new vector.
     *
     * @param scalar the scalar to subtract from this vector.
     * @return this vector subtracted by the given scalar.
     */
    public Vector minus(final double scalar) {
        final double[] differenced = new double[this.size()];
        for (int i = 0; i < differenced.length; i++) {
            differenced[i] = this.elements[i] - scalar;
        }
        return new Vector(differenced);
    }

    /**
     * Scale this vector by the given scalar and return the result in a new vector.
     *
     * @param alpha the scalar to scale this vector by.
     * @return this vector scaled by the given scalar.
     */
    public Vector scaledBy(final double alpha) {
        final double[] scaled = new double[this.size()];
        for (int i = 0; i < scaled.length; i++) {
            scaled[i] = alpha * this.elements[i];
        }
        return new Vector(scaled);
    }

    /**
     * Compute the dot product of this vector with the given vector.
     *
     * @param other the vector to take the dot product with.
     * @return the dot product of this vector with the given vector.
     */
    public double dotProduct(final Vector other) {
        if (other.elements.length > 0) {
            double product = 0.0;
            for (int i = 0; i < elements.length; i++) {
                product += this.elements[i] * other.elements[i];
            }
            return product;
        }
        throw new IllegalArgumentException("The dot product is undefined for zero length vectors");
    }

    /**
     * Compute the outer product of this vector and the given vector.
     *
     * @param other the vector to compute the outer product of this vector with.
     * @return the outer product of this vector with the given vector.
     */
    public Matrix outerProduct(final Vector other) {
        double[] product = new double[elements.length * other.elements.length];
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < other.elements.length; j++) {
                product[i * other.elements.length + j] = elements[i] * other.elements[j];
            }
        }
        return new Matrix(elements.length, other.elements.length, product);

    }

    Vector axpy(final Vector other, final double alpha) {
        final double[] result = new double[this.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alpha * this.elements[i] + other.elements[i];
        }
        return new Vector(result);
    }

    /**
     * Compute the L2 length of this vector.
     *
     * @return the L2 length of this vector.
     */
    public double norm() {
        return Math.sqrt(dotProduct(this));
    }

    /**
     * Compute the sum of the elements of this vector.
     *
     * @return the sum of the elements of this vector.
     */
    public double sum() {
        return Statistics.sumOf(elements);
    }

    /**
     * Compute the sum of squared elements of this vector.
     *
     * @return the sum of squared elements of this vector.
     */
    public double sumOfSquares() {
        return Statistics.sumOfSquared(elements);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(elements);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Vector other = (Vector) obj;
        return Arrays.equals(elements, other.elements);
    }

    @Override
    public String toString() {
        return "elements: " + Arrays.toString(elements);
    }
}
