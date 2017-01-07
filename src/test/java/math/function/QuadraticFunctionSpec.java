/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import math.Complex;
import math.Real;
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
    QuadraticFunction function = new QuadraticFunction(new Real(1.5), new Real(2.0), new Real(-4.0));
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
    assertThat(function.a().value(), is(1.0));
    assertThat(function.b().value(), is(-5.0));
    assertThat(function.c().value(), is(6.0));
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
    assertThat(a, is(not(new Object())));
    assertThat(a, is(not((b))));
    assertThat(a, is(a));
    assertThat(a, is(c));
    assertThat(a.hashCode(), is(c.hashCode()));
    b = new QuadraticFunction(1.0, 5.0, 6.0);
    assertThat(a, is(not(b)));
  }

}
