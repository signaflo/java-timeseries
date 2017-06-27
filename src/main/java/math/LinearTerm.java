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

package math;

public class LinearTerm<T extends FieldElement<T>> {

    private final int variable;
    private final T coefficient;

    private LinearTerm(final int variable, final T coefficient) {
        this.variable = variable;
        this.coefficient = coefficient;
    }

    static <T extends FieldElement<T>> LinearTerm<T> from(int variable, T coefficient) {
        return new LinearTerm<>(variable, coefficient);
    }

    @Override
    public String toString() {
        return this.coefficient + "x" + variable;
    }

    T getCoefficient() {
        return this.coefficient;
    }

    int getVariable() {
        return this.variable;
    }

    LinearTerm<T> plus(LinearTerm<T> otherTerm) {
        if (otherTerm.getVariable() != this.getVariable()) {
            throw new IllegalStateException("Two terms must have the same variable part to be added.");
        }
        return new LinearTerm<>(this.variable, this.coefficient.plus(otherTerm.coefficient));
    }
    public LinearTerm<T> times(T scalar) {
        return new LinearTerm<>(this.variable, this.coefficient.times(scalar));
    }
}
