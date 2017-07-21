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

package math.function;

import math.Complex;
import math.Real;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Jacob Rachiele
 */
public class QuadraticFunctionSpec {

  private QuadraticFunction function;

  @Before
  public void beforeMethod() {
    function = new QuadraticFunction(1.0, -5.0, 6.0);
  }


  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenFirstCoefficientZeroExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    new QuadraticFunction(0.0, 3.5, 2.0);
  }

  @Test
  public void whenFirstCoefficientRealZeroExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    new QuadraticFunction(Real.from(0.0), Real.from(3.5), Real.from(2.0));
  }

  @Test
  public void whenZerosComputedThenValuesCorrect() {
    QuadraticFunction function = new QuadraticFunction(Real.from(1.5), Real.from(2.0), Real.from(-4.0));
    Complex[] expectedZeros = new Complex[] {new Complex(1.0971675407097272),
        new Complex(-2.4305008740430605)};
    assertThat(function.zeros(), is(expectedZeros));
  }

  @Test
  public void whenZerosWithLargeSecondCoefficientThenValuesCorrect() {
    QuadraticFunction function = new QuadraticFunction(1.0, -80.0, 1.0);
    Complex[] expectedZeros = new Complex[] {new Complex(79.98749804626442),
        new Complex(0.012501953735586824)};
    assertThat(function.zeros(), is(expectedZeros));
  }

  @Test
  public void givenQuadraticFunctionCoefficientsReturned() {
    assertThat(function.a().asDouble(), is(1.0));
    assertThat(function.b().asDouble(), is(-5.0));
    assertThat(function.c().asDouble(), is(6.0));
    assertThat(function.coefficients(), is(new Real[] {Real.from(1.0), Real.from(-5.0), Real.from(6.0)}));
  }

  @Test
  public void whenEvaluatedAtThenResultCorrect() {
    AbstractFunction f = new QuadraticFunction(-4.0, 1.0, 5.0);
    assertThat(f.at(-5.0), is(-100.0));
    assertThat(f.slopeAt(-5.0), is(41.0));
  }

  @Test
  public void givenQuadraticFunctionExtremaCorrect() {
    assertThat(function.extremePoint(), is(Real.from(5.0/2.0)));
    assertThat(function.extremePointDbl(), is(2.5));
    assertThat(function.extremum(), is(Real.from(-0.25)));
    assertThat(function.extremumDbl(), is(-0.25));
  }

  @Test
  public void whenFunctionHasMinOrMaxThenHasMinimumIsTrue() {
    assertThat(function.hasMinimum(), is(true));
    assertThat(function.hasMaximum(), is(false));
    function = new QuadraticFunction(-1.0, -5.0, -6.0);
    assertThat(function.hasMinimum(), is(false));
    assertThat(function.hasMaximum(), is(true));
  }

  @Test
  public void whenFunctionEvaluatedThenCorrectResult() {
    assertThat(function.at(Real.from(3.5)), is(Real.from(0.75)));
  }

  @Test
  public void testEqualsAndHashCode() {
    QuadraticFunction a = function;
    QuadraticFunction b = new QuadraticFunction(-1.0, -5.0, 6.0);
    QuadraticFunction c = new QuadraticFunction(1.0, -5.0, 6.0);
    //noinspection ObjectEqualsNull
    assertThat(a.equals(null), is(false));
    MatcherAssert.assertThat(a, is(not(new Object())));
    MatcherAssert.assertThat(a, is(not((b))));
    assertThat(a, is(a));
    assertThat(a, is(c));
    assertThat(a.hashCode(), is(c.hashCode()));
    b = new QuadraticFunction(1.0, 5.0, 6.0);
    MatcherAssert.assertThat(a, is(not(b)));
  }

}
