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
package com.github.signaflo.math;

/**
 * Represents an element of a mathematical <a target="_blank"
 * href="https://en.wikipedia.org/wiki/Field_(mathematics)">field</a>.
 *
 * @param <T> The type of field element.
 * @author Jacob Rachiele
 */
public interface FieldElement<T> extends Comparable<T> {

    /**
     * Add this element to the given element.
     *
     * @param other the element to add to this element.
     * @return this element added to the given element.
     */
    T plus(T other);

    /**
     * Subtract the given element from this element.
     *
     * @param other the element to subtract from this element.
     * @return this element subtracted by the given element.
     */
    T minus(T other);

    /**
     * Multiply this element by the given element.
     *
     * @param other the element to multiply this element by.
     * @return this element multiplied by the given element.
     */
    T times(T other);

    /**
     *
     * The square root operation applied to this element, returning a complex number. If the field this
     * element belongs to is not part of the complex number hierarchy, then implementations may throw
     * an {@link IllegalStateException}
     *
     * @return the square root of this element.
     * @throws IllegalStateException if the field this element belongs to is not part of the complex hierarchy.
     */
    Complex complexSqrt();

    /**
     * Computes and returns the square root of this number as long as the square root is of the same
     * type (i.e., belongs to the same field) as this number.
     * If the return type is not guaranteed to be of the same type as this number, then
     * {@link #complexSqrt()} should be used instead.
     *
     * @return the square root of this number if the square root is of the same type.
     * @throws IllegalStateException if the square root is not of the same type as this number.
     */
    T sqrt();

//    <N extends FieldElement<N>> N sqrt(Class<N> clazz);

    /**
     * Compute and return the conjugate of this element.
     *
     * @return the conjugate of this element.
     */
    T conjugate();

    /**
     * Return the element of this field that, when added to this element, produces the additive identity.
     *
     * @return the element of this field that, when added to this element, produces the additive identity.
     */
    T additiveInverse();

    /**
     * Compute and return the absolute value of this element.
     *
     * @return the absolute value of this element.
     */
    double abs();

    /**
     *
     * Divide this element by the given element and return the result.
     *
     * @param other the divisor.
     *
     * @return this element divided by the given element.
     */
    T dividedBy(T other);

}
