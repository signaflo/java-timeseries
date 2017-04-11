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

package data.generic;

import math.FieldElement;

public class Zero<T extends FieldElement<T>> {

    private final T value;

    public static <T extends FieldElement<T>> Zero<T> instance(T value) {
        return new Zero<>(value);
    }
    Zero(T value) {
        this.value = value;
    }

    T getValue() {
        return value;
    }


//    @Override
//    public T plus(T other) {
//        return other;
//    }
//
//    @Override
//    public T minus(T other) {
//        return other.additiveInverse();
//    }
//
//    @Override
//    public T times(T other) {
//        return this.getValue();
//    }
//
//    @Override
//    public T sqrt() {
//        return this.getValue();
//    }
//
//    @Override
//    public T conjugate() {
//        return this.getValue();
//    }
//
//    @Override
//    public T additiveInverse() {
//        return this.getValue();
//    }
//
//    @Override
//    public double abs() {
//        return 0.0;
//    }
//
//    @Override
//    public T dividedBy(double value) {
//        return this.getValue();
//    }
}
