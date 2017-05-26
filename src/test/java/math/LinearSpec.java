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

import org.junit.Test;

import java.util.Arrays;

public class LinearSpec {

    @Test
    public void testLinear() {
//        LinearTerm<Real> realTerm1 = new LinearTerm<>(Real.from(0.5), 1);
//        LinearTerm<Real> realTerm2 = new LinearTerm<>(Real.from(2.0), 2);
//        LinearTerm<Real> realTerm3 = new LinearTerm<>(Real.from(-4.0), 3);
//        LinearTerm<Real> realTerm4 = new LinearTerm<>(Real.from(5.0), 1);
//        LinearTerm<Real> realTerm5 = new LinearTerm<>(Real.from(-3.0), 2);
//        LinearTerm<Real> realTerm6 = new LinearTerm<>(Real.from(10.0), 3);
        LinearExpression<Real> realExpression1 = new RealLinearExpression(0.5, 2.0, -4.0);
        LinearExpression<Real> realExpression2 = new RealLinearExpression(5.0, -3.0, 10.0);
        LinearEquation<Real> linearEquation = new LinearEquation<>(realExpression1, Real.from(6.0));
        LinearEquation<Real> linearEquation2 = new LinearEquation<>(realExpression2, Real.from(3.0));

        SystemOfLinearEquations<Real> system = new SystemOfLinearEquations<>(
                Arrays.asList(linearEquation, linearEquation2));
        System.out.println(system);
        system.swapLocation(0, 1);
        System.out.println(system);
        system.multiplyEquation(0, Real.from(2.0));
        System.out.println(system);
    }
}
