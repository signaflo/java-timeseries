/*
 * Copyright (c) 2017 Jacob Rachiele
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

package math.linear;

import math.FieldElement;

public class LinearEquation<T extends FieldElement<T>> {

    private final LinearExpression<T> leftSide;
    private final T rightSide;

    LinearEquation(LinearExpression<T> leftSide, T rightSide) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    LinearEquation<T> multiplyBy(T scalar) {
        final T newRightSide = this.rightSide.times(scalar);
        final LinearExpression<T> newLeftSide = this.leftSide.times(scalar);
        return new LinearEquation<>(newLeftSide, newRightSide);
    }

    LinearEquation<T> add(LinearEquation<T> otherEquation) {
        final T newRightSide = this.rightSide.plus(otherEquation.rightSide);
        final LinearExpression<T> newLeftSide = this.leftSide.plus(otherEquation.leftSide);
        return new LinearEquation<>(newLeftSide, newRightSide);
    }

    @Override
    public String toString() {
        return this.leftSide.toString() + " = " + this.rightSide.toString();
    }
}
