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
 * The vector is "generic" in the sense that it is not specified as being either a row-vector or column-vector, but
 * can act as either one.
 *
 * @author Jacob Rachiele
 */
final class GenericVector implements Vector {

    private final double[] elements;

    /**
     * Create a new vector using the provided elements.
     *
     * @param elements the elements of the new vector.
     */
    GenericVector(double... elements) {
        this.elements = elements.clone();
    }

    @Override
    public double[] elements() {
        return this.elements.clone();
    }

    @Override
    public double at(final int i) {
        return this.elements[i];
    }

    @Override
    public int size() {
        return this.elements.length;
    }

    @Override
    public GenericVector plus(final Vector other) {
        if (other.elements().length == 0) {
            return this;
        }
        final double[] summed = new double[this.size()];
        for (int i = 0; i < summed.length; i++) {
            summed[i] = this.elements[i] + other.at(i);
        }
        return new GenericVector(summed);
    }

    @Override
    public GenericVector minus(final Vector other) {
        final double[] differenced = new double[this.size()];
        for (int i = 0; i < differenced.length; i++) {
            differenced[i] = this.elements[i] - other.at(i);
        }
        return new GenericVector(differenced);
    }

    @Override
    public GenericVector minus(final double scalar) {
        final double[] differenced = new double[this.size()];
        for (int i = 0; i < differenced.length; i++) {
            differenced[i] = this.elements[i] - scalar;
        }
        return new GenericVector(differenced);
    }

    @Override
    public GenericVector scaledBy(final double alpha) {
        final double[] scaled = new double[this.size()];
        for (int i = 0; i < scaled.length; i++) {
            scaled[i] = alpha * this.elements[i];
        }
        return new GenericVector(scaled);
    }

    @Override
    public double dotProduct(final Vector other) {
        if (other.elements().length == 0) {
            throw new IllegalArgumentException("The dot product is undefined for zero length vectors");
        }
        double product = 0.0;
        for (int i = 0; i < elements.length; i++) {
            product += this.elements[i] * other.at(i);
        }
        return product;
    }

    @Override
    public Matrix outerProduct(final Vector other) {
        double[] otherElements = other.elements();
        double[] product = new double[elements.length * otherElements.length];
        for (int i = 0; i < elements.length; i++) {
            for (int j = 0; j < otherElements.length; j++) {
                product[i * otherElements.length + j] = elements[i] * otherElements[j];
            }
        }
        return Matrix.create(elements.length, otherElements.length, product);

    }

    Vector axpy(final GenericVector other, final double alpha) {
        final double[] result = new double[this.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = alpha * this.elements[i] + other.elements[i];
        }
        return new GenericVector(result);
    }

    @Override
    public double norm() {
        return Math.sqrt(dotProduct(this));
    }

    @Override
    public double sum() {
        return Statistics.sumOf(elements);
    }

    @Override
    public double sumOfSquares() {
        return Statistics.sumOfSquared(elements);
    }

    @Override
    public Vector push(double value) {
        double[] newElements = new double[this.elements.length + 1];
        newElements[0] = value;
        System.arraycopy(this.elements, 0, newElements, 1, this.elements.length);
        return new GenericVector(newElements);
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
        GenericVector other = (GenericVector) obj;
        return Arrays.equals(elements, other.elements);
    }

    @Override
    public String toString() {
        return "elements: " + Arrays.toString(elements);
    }
}
