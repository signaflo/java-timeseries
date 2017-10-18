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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class CubicInterpolationSpec {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();
  
  @Test
  public void whenTwoXValuesTheSameExceptionThrown() {
    exception.expect(RuntimeException.class);
    new CubicInterpolation(1.0, 1.0, 2.5, 2.5, -0.1, 0.1);
  }
  
  @Test
  public void whenCubicInterpolationPerformedThenMinimumCorrect() {
    CubicInterpolation interpolation = new CubicInterpolation(1.0, 3.0, -1.0/3.0, -3.0/11.0, -1.0/9.0, 0.057851);
    assertThat(interpolation.minimum(), is(closeTo(1.528795, 1E-4)));
  }

  @Test
  public void whenCubicInterpolationPerformedDifferentArgOrderThenMinimumStillCorrect() {
    CubicInterpolation interpolation = new CubicInterpolation(3.0, 1.0, -3.0/11.0, -1.0/3.0, 0.057851, -1.0/9.0);
    assertThat(interpolation.minimum(), is(closeTo(1.528795, 1E-4)));
  }

}
