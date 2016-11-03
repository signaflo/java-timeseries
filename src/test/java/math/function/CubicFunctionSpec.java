/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import math.Complex;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Jacob Rachiele
 */
public class CubicFunctionSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenNoLocalMinExceptionThrown() {
    CubicFunction f = new CubicFunction(1.0, 1.0, 1.0, 10.0);
    exception.expect(RuntimeException.class);
    f.localMinimum();
  }

  @Test
  public void whenNoLocalMaxExceptionThrown() {
    CubicFunction f = new CubicFunction(3.0, -1.0, 1.0, 10.0);
    exception.expect(RuntimeException.class);
    f.localMaximum();
  }

  @Test
  public void whenCriticalPointsThenCorrectValues() {
    CubicFunction f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    Complex[] expected = new Complex[] {new Complex(-0.567520806326), new Complex(0.734187472992)};
    Complex[] criticalPoints = f.criticalPoints();
    for (int i = 0; i < 2; i++) {
      assertThat(criticalPoints[i].real(), is(closeTo(expected[i].real(), 1E-8)));
    }
  }

  @Test
  public void whenLocalMinimumThenCorrectValue() {
    CubicFunction f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    assertThat(f.localMinimumPoint().value(), is(closeTo(-0.567520806326, 1E-8)));
    assertThat(f.localMinimum().value(), is(closeTo(-1.78437606588, 1E-8)));
  }

  @Test
  public void whenLocalMaximumThenCorrectValue() {
    CubicFunction f = new CubicFunction(-4.0, 1.0, 5.0, 0.0);
    assertThat(f.localMaximumPoint().value(), is(closeTo(0.734187472992, 1E-8)));
    assertThat(f.localMaximum().value(), is(closeTo(2.62696865847, 1E-8)));
  }

  @Test
  public void testCubicFunction() {
    CubicFunction f = new CubicFunction(1.0, -1.4, -1.0, 10.0);
    System.out.println(f.localMinimumPoint());
    System.out.println(f.localMinimum());
    System.out.println(f.localMaximumPoint());
    System.out.println(f.localMaximum());
  }
}
