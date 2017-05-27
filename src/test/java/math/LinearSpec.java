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
        LinearExpression<Real> realExpression1 = new RealLinearExpression(1, 2, 2);
        LinearExpression<Real> realExpression2 = new RealLinearExpression(1, 3, 3);
        LinearExpression<Real> realExpression3 = new RealLinearExpression(2, 6, 5);
        LinearEquation<Real> linearEquation1 = new LinearEquation<>(realExpression1, Real.from(4.0));
        LinearEquation<Real> linearEquation2 = new LinearEquation<>(realExpression2, Real.from(5.0));
        LinearEquation<Real> linearEquation3 = new LinearEquation<>(realExpression3, Real.from(6.0));

        SystemOfLinearEquations<Real> system = new SystemOfLinearEquations<>(
                Arrays.asList(linearEquation1, linearEquation2, linearEquation3));
        System.out.println(system);
        system.scaleAndAdd(0, 1, Real.from(-1.0));
        System.out.println(system);
        system.scaleAndAdd(0, 2, Real.from(-2.0));
        System.out.println(system);
        system.scaleAndAdd(1, 2, Real.from(-2.0));
        System.out.println(system);
        system.scaleEquation(2, Real.from(-1.0));
        System.out.println(system);
    }

    @Test
    public void testRationalSystem() {
        LinearExpression<Rational> rationalExpression1 = new RationalLinearExpression(
                Rational.from(7, 15),
                Rational.from(6, 15),
                Rational.from(2, 15)
        );
        LinearExpression<Rational> rationalExpression2 = new RationalLinearExpression(
                Rational.from(6, 15),
                Rational.from(4, 15),
                Rational.from(5, 15)
        );
        LinearExpression<Rational> rationalExpression3 = new RationalLinearExpression(
                Rational.from(2, 15),
                Rational.from(5, 15),
                Rational.from(8, 15)
        );
        LinearEquation<Rational> rationalEquation1 = new LinearEquation<>(rationalExpression1, Rational.from(380));
        LinearEquation<Rational> rationalEquation2 = new LinearEquation<>(rationalExpression2, Rational.from(500));
        LinearEquation<Rational> rationalEquation3 = new LinearEquation<>(rationalExpression3, Rational.from(620));
        SystemOfLinearEquations<Rational> system = new SystemOfLinearEquations<>(
                Arrays.asList(rationalEquation1, rationalEquation2, rationalEquation3)
        );
        system.swapLocation(0, 1);
        System.out.println(system);
        system.scaleEquation(0, Rational.from(2));
        System.out.println(system);
        system.scaleAndAdd(0, 1, Rational.from(2));
        System.out.println(system);
    }
}
