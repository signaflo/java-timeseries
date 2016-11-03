/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import math.Complex;
import math.Real;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Jacob Rachiele
 */
public class CubicFunctionSpec {

  CubicFunction f;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenNoLocalMinExceptionThrown() {
    f = new CubicFunction(1.0, 1.0, 1.0, 10.0);
    exception.expect(RuntimeException.class);
    f.localMinimum();
  }

  @Test
  public void whenNoLocalMaxExceptionThrown() {
    f = new CubicFunction(3.0, -1.0, 1.0, 10.0);
    exception.expect(RuntimeException.class);
    f.localMaximum();
  }

  @Test
  public void whenCriticalPointsThenCorrectValues() {
    f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    Complex[] expected = new Complex[] {new Complex(-0.567520806326), new Complex(0.734187472992)};
    Complex[] criticalPoints = f.criticalPoints();
    for (int i = 0; i < 2; i++) {
      assertThat(criticalPoints[i].real(), is(closeTo(expected[i].real(), 1E-8)));
    }
  }

  @Test
  public void whenLocalMinimumThenCorrectValue() {
    f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    assertThat(f.localMinimumPoint().value(), is(closeTo(-0.567520806326, 1E-8)));
    assertThat(f.localMinimum().value(), is(closeTo(-1.78437606588, 1E-8)));
  }

  @Test
  public void whenLocalMaximumThenCorrectValue() {
    f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    assertThat(f.localMaximumPoint().value(), is(closeTo(0.734187472992, 1E-8)));
    assertThat(f.localMaximum().value(), is(closeTo(2.62696865847, 1E-8)));
  }

  @Test
  public void whenGettersThenValuesReturned() {
    f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    assertThat(f.a(), is(Real.from(-4.0)));
    assertThat(f.b(), is(Real.from(1.0)));
    assertThat(f.c(), is(Real.from(5.0)));
    assertThat(f.d(), is(Real.from(0.0)));
  }

  @Test
  public void whenLeadingCoefficientZeroThenException() {
    exception.expect(IllegalArgumentException.class);
    new CubicFunction(0.0, 1.0, 5.0, 0.0);
  }

  @Test
  public void whenRealLeadingCoefficientZeroThenException() {
    exception.expect(IllegalArgumentException.class);
    new CubicFunction(Real.from(0.0), Real.from(1.0), Real.from(5.0), Real.from(0.0));
  }

  @Test
  public void whenLocalExtremaThenValuesCorrect() {
    f = new CubicFunction(Real.from(-4.0), Real.from(1.0), Real.from(5.0), Real.from(0.0));
    assertThat(f.localExtrema()[0].value(), is(closeTo(-1.78437606588, 1E-8)));
    assertThat(f.localExtrema()[1].value(), is(closeTo(2.62696865847, 1E-8)));
  }

  @Test
  public void whenNoExtremePointsThenEmptyArray() {
    Real[] empty = new Real[]{};
    f = new CubicFunction(1.0, 0.0, 0.0, 0.0);
    assertThat(f.localExtremePoints(), is(empty));
  }
}
