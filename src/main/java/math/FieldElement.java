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
package math;

import lombok.NonNull;

/**
 * Represents an element of a mathematical <a target="_blank"
 * href="https://en.wikipedia.org/wiki/Field_(mathematics)">field</a>.
 *
 * @param <T> The type of field element.
 * @author Jacob Rachiele
 */
public interface FieldElement<T> extends Comparable<FieldElement<T>> {

    @Override
    default int compareTo(@NonNull FieldElement<T> element) {
        if (abs() < element.abs()) {
            return -1;
        }
        if (abs() == element.abs()) {
            return 0;
        }
        return 1;
    }

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
     * The square root operation applied to this element.
     *
     * @return the square root of this element.
     */
    T sqrt();

    /**
     * Compute and return the conjugate of this element.
     *
     * @return the conjugate of this element.
     */
    T conjugate();

    T additiveInverse();

    /**
     * Compute and return the absolute value of this element.
     *
     * @return the absolute value of this element.
     */
    double abs();

}
