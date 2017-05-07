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
package linear;

import com.google.common.collect.ImmutableList;
import math.FieldElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An immutable and thread-safe implementation of a vector backed by a list of {@link FieldElement}s.
 *
 * @author Jacob Rachiele
 */
final class FieldVector<T extends FieldElement<T>> {

    private final List<T> elements;

    /**
     * Create a new vector using the provided list of elements.
     *
     * @param elements a list of elements constituting the elements of the new vector.
     */
    FieldVector(List<T> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    /**
     * Create a new vector using the provided elements.
     *
     * @param elements the elements of the new vector.
     */
    @SafeVarargs
    FieldVector(T... elements) {
        this.elements = ImmutableList.copyOf(Arrays.asList(elements.clone()));
    }

    /**
     * The elements of the vector as a list of field elements.
     *
     * @return the elements of the vector as a list of field element.
     */
    List<T> elements() {
        return this.elements;
    }

    /**
     * Return the element at index i, where indexing begins at 0.
     *
     * @param i the index of the element.
     * @return the element at index i, where indexing begins at 0.
     */
    T at(final int i) {
        return this.elements.get(i);
    }

    /**
     * The number of elements in this vector.
     *
     * @return the number of elements in this vector.
     */
    int size() {
        return this.elements.size();
    }

    /**
     * Add this vector to the given vector and return the result in a new vector.
     *
     * @param other the vector to add to this vector.
     * @return this vector added to the given vector.
     */
    FieldVector<T> plus(final FieldVector<T> other) {
        final List<T> summed = new ArrayList<>(this.size());
        for (int i = 0; i < this.size(); i++) {
            summed.add(this.elements.get(i).plus(other.elements.get(i)));
        }
        return new FieldVector<>(summed);
    }

    /**
     * Subtract the given vector from this vector and return the result in a new vector.
     *
     * @param other the vector to subtract from this vector.
     * @return this vector subtracted by the given vector.
     */
    FieldVector<T> minus(final FieldVector<T> other) {
        final List<T> differenced = new ArrayList<>(this.size());
        for (int i = 0; i < this.size(); i++) {
            differenced.add(this.elements.get(i).minus(other.elements.get(i)));
        }
        return new FieldVector<>(differenced);
    }

    /**
     * Scale this vector by the given scalar and return the result in a new vector.
     *
     * @param alpha the scalar to scale this vector by.
     * @return this vector scaled by the given scalar.
     */
    FieldVector<T> scaledBy(final T alpha) {
        final List<T> scaled = new ArrayList<>(this.size());
        for (int i = 0; i < this.size(); i++) {
            scaled.add(this.elements.get(i).times(alpha));
        }
        return new FieldVector<>(scaled);
    }

    FieldVector<T> axpy(final FieldVector<T> other, final T alpha) {
        final List<T> result = new ArrayList<>(this.size());
        for (int i = 0; i < this.size(); i++) {
            result.add(this.elements.get(i).times(alpha).plus(other.elements.get(i)));
        }
        return new FieldVector<>(result);
    }

    /**
     * Compute the dot product of this vector with the given vector.
     *
     * @param other the vector to take the dot product with.
     * @return the dot product of this vector with the given vector.
     */
    T dotProduct(final FieldVector<T> other) {
        if (this.size() > 0) {
            T product = this.elements.get(0).times(other.elements.get(0).conjugate());
            for (int t = 1; t < this.elements.size(); t++) {
                product = product.plus(this.elements.get(t).times(other.elements.get(t).conjugate()));
            }
            return product;
        }
        throw new IllegalStateException("The dot product is undefined for zero length vectors.");
    }

    /**
     * Compute the L2 length of this vector.
     *
     * @return the L2 length of this vector.
     */
    double norm() {
        return Math.sqrt(sumOfSquares());
    }

    private double sumOfSquares() {
        double sum = 0.0;
        if (this.size() > 0) {
            sum = Math.pow(this.elements.get(0).abs(), 2);
            for (int i = 1; i < this.elements.size(); i++) {
                sum += Math.pow(this.elements.get(i).abs(), 2);
            }
        }
        return sum;
    }


    @Override
    public String toString() {
        return "elements: " + elements;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elements == null) ? 0 : elements.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FieldVector<?> other = (FieldVector<?>) obj;
        if (elements == null) {
            if (other.elements != null) return false;
        } else if (!elements.equals(other.elements)) return false;
        return true;
    }
}
