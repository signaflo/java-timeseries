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

package com.github.signaflo.math.polynomial.interpolation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class LinearInterpolationSpec {
  
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Test
  public void whenTwoPointsSameExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    new LinearInterpolation(3.0, 3.0, 7.0, 10.0);
  }
  
  @Test
  public void whenFunctionValuesGivenFirstCoefficientCorrect() {
   LinearInterpolation interpolation = new LinearInterpolation(3.5, 5.0, 12.25, 25.0);
   assertThat(interpolation.a0(), is(equalTo(-17.5)));
  }
  
  @Test
  public void whenFunctionValuesGivenSecondCoefficientCorrect() {
    LinearInterpolation interpolation = new LinearInterpolation(3.5, 5.0, 12.25, 25.0);
    assertThat(interpolation.a1(), is(equalTo(8.5)));
  }

}
