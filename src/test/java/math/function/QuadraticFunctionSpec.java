/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import math.Complex;
import math.Real;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Jacob Rachiele
 */
public class QuadraticFunctionSpec {

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
}
