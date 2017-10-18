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

package com.github.signaflo.math.function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

import com.github.signaflo.math.linear.doubles.Vector;

public class NumericDerivativesSpec {

  @Test
  public void whenForwardDerivCalculatedApproximationClose() {
    Function f = (x) -> x * x * x;
    final double h = 1E-4;
    final double point = 4.0;
    final double forwardDeriv = NumericalDerivatives.forwardDifferenceApproximation(f, point, h);
    MatcherAssert.assertThat(forwardDeriv, is(closeTo(48.0, 1E-2)));
  }
  
  @Test
  public void whenCentralDerivCalculatedApproximationClose() {
    Function f = (x) -> x * x * x;
    final double h = 1E-4;
    final double point = 4.0;
    final double centralDeriv = NumericalDerivatives.centralDifferenceApproximation(f, point, h);
    MatcherAssert.assertThat(centralDeriv, is(closeTo(48.0, 1E-8)));
  }
  
  @Test
  public void whenForwardGradientCalculatedApproximationClose() {
    MultivariateDoubleFunction f = (point) -> point[0] * point[0] + point[1] * point[1];
    final double h = 1E-4;
    final double[] point = new double[] {3, 4};
    final double[] forwardGradient = NumericalDerivatives.forwardDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, forwardGradient, 1E-4);
  }
  
  @Test
  public void whenCentralGradientCalculatedApproximationClose() {
    MultivariateDoubleFunction f = (point) -> point[0] * point[0] + point[1] * point[1];
    final double h = 1E-4;
    final double[] point = new double[] {3, 4};
    final double[] centralGradient= NumericalDerivatives.centralDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, centralGradient, 1E-10);
  }
  
  @Test
  public void whenForwardGradientVectorCalculatedApproximationClose() {
    MultivariateFunction f = (point) -> point.at(0) * point.at(0) + point.at(1) * point.at(1);
    final double h = 1E-4;
    final Vector point = Vector.from(3.0, 4.0);
    final Vector forwardGradient = NumericalDerivatives.forwardDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, forwardGradient.elements(), 1E-4);
  }
  
  @Test
  public void whenCentralGradientVectorCalculatedApproximationClose() {
    MultivariateFunction f = (point) -> point.at(0) * point.at(0) + point.at(1) * point.at(1);
    final double h = 1E-4;
    final Vector point = Vector.from(3.0, 4.0);
    final Vector centralGradient= NumericalDerivatives.centralDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, centralGradient.elements(), 1E-10);
  } 
}
