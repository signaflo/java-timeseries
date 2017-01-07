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
    Real a = new Real(-4.0);
    Real b = new Real(1.0);
    Real c = new Real(5.0);
    Real d = new Real(0.0);
    assertThat(f.a(), is(a));
    assertThat(f.b(), is(b));
    assertThat(f.c(), is(c));
    assertThat(f.d(), is(d));
    assertThat(f.coefficients(), is(new Real[] {a, b, c, d}));
  }

  @Test
  public void whenEvaluatedAtThenResultCorrect() {
    Function cubic = (x) -> x * x * x * -4.0 + x * x + 5.0 * x;
    Function slope = (x) -> -12.0 * x * x + 2.0 * x + 5.0;
    f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    GeneralFunction gf = new GeneralFunction(cubic, slope);
    System.out.println(gf.at(-5.0));
    System.out.println(gf.slopeAt(-5.0));
    assertThat(f.at(-5.0), is(500.0));
    assertThat(f.slopeAt(-5.0), is(-305.0));
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
    f = new CubicFunction(1.0, 1.0, 3.0, 2.0);
    assertThat(f.criticalPoints(), is(empty));
    assertThat(f.localExtremePoints(), is(empty));
  }

  @Test
  public void whenOneExtremePointsThenCriticalPointCorrect() {
    f = new CubicFunction(1.0, 0.0, 0.0, 0.0);
    assertThat(f.criticalPoints(), is(new Real[] {Real.from(0.0)}));
    assertThat(f.localExtremePoints(), is(new Real[] {}));
  }
}
