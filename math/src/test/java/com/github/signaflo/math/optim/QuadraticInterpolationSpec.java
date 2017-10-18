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

package com.github.signaflo.math.optim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class QuadraticInterpolationSpec {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();
  
  @Test
  public void whenTwoXValuesTheSameExceptionThrown() {
    exception.expect(RuntimeException.class);
    new QuadraticInterpolation(1.0, 1.0, 2.5, 2.5, 0.0);
  }

  @Test
  public void whenStaticMinimumTwoXValuesTheSameExceptionThrown() {
    exception.expect(RuntimeException.class);
    QuadraticInterpolation.minimum(1.0, 1.0, 2.5, 2.5, 0.0);
  }
  
  @Test
  public void whenPolynomialCreatedFirstCoefficientCorrect() {
    QuadraticInterpolation interpolater = new QuadraticInterpolation(1.0, 3.0, -1.0/3.0, -3.0/11.0, -1.0/9.0);
    assertThat(interpolater.computeA(), is(closeTo(0.070707, 1E-3)));
  }
  
  @Test
  public void whenPolynomialCreatedSecondCoefficientCorrect() {
    QuadraticInterpolation interpolater = new QuadraticInterpolation(1.0, 3.0, -1.0/3.0, -3.0/11.0, -1.0/9.0);
    assertThat(interpolater.computeB(), is(closeTo(-0.252525, 1E-3)));
  }
  
  @Test
  public void whenPolynomialCreatedThenMinimumCorrect() {
    QuadraticInterpolation interpolater = new QuadraticInterpolation(1.0, 3.0, -1.0/3.0, -3.0/11.0, -1.0/9.0);
    assertThat(interpolater.minimum(), is(closeTo(1.785714, 1E-3)));
  }

  @Test
  public void whenPolynomialCreatedDifferentArgOrderThenMinimumStillCorrect() {
    QuadraticInterpolation interpolater = new QuadraticInterpolation(3.0, 1.0, -3.0/11.0, -1.0/3.0, -1.0/9.0);
    assertThat(interpolater.minimum(), is(closeTo(1.785714, 1E-3)));
  }
  
  @Test
  public void whenSecantFormulaMinPerformedThenResultCorrect() {
    double twoDerivMin = QuadraticInterpolation.secantFormulaMinimum(1.0, 3.0, -1.0/9.0, 0.05785124);
    MatcherAssert.assertThat(twoDerivMin, is(closeTo(2.315217, 1E-3)));
    twoDerivMin = QuadraticInterpolation.secantFormulaMinimum(3.0, 1.0, 0.05785124, -1.0/9.0);
    MatcherAssert.assertThat(twoDerivMin, is(closeTo(2.315217, 1E-3)));
  }
  
  @Test
  public void whenThreePointMinPerformedThenResultCorrect() {
    double twoDerivMin = QuadraticInterpolation.threePointMinimum(1.0, 1.7, 2.4, -1.0/3.0, -0.34764826, -0.309278350);
    MatcherAssert.assertThat(twoDerivMin, is(closeTo(1.540196, 1E-3)));
  }

  @Test
  public void whenThreePointWithNoMinimumThenException() {
    exception.expect(IllegalStateException.class);
    QuadraticInterpolation.threePointMinimum(2.0, 4.0, 7.0, -4.0, -16.0, -49.0);
  }

}
