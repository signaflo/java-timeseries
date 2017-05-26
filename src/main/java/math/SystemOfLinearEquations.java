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

import java.util.ArrayList;
import java.util.List;

/**
 * A system of linear equations. This class is not immutable. It mutates the underlying list of equations.
 * @param <T> The field type of this system (e.g., complex, real, etc...)
 */
class SystemOfLinearEquations<T extends FieldElement<T>> {

    private final List<LinearEquation<T>> equations;

    SystemOfLinearEquations(List<LinearEquation<T>> equations) {
        this.equations = new ArrayList<>(equations);
    }

    void swapLocation(int equationI, int equationJ) {
        LinearEquation<T> newEquationI = equations.get(equationI);
        equations.set(equationI, equations.get(equationJ));
        equations.set(equationJ, newEquationI);
    }

    void multiplyEquation(int equation, T scalar) {
        equations.set(equation, equations.get(equation).multiplyBy(scalar));
    }

    void multiplyAndAdd(int equationI, int equationJ, T scalar) {

    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (LinearEquation<T> equation : this.equations) {
            stringBuilder.append(equation.toString()).append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }
}
