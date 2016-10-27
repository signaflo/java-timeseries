/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Jacob Rachiele
 */
public class RealSpec {

  private Real a;
  private Real b;

  @Before
  public void beforeMethod() {
    a = Real.from(3.0);
    b = Real.from(4.5);
  }

  @Test
  public void whenRealAddedThenCorrectRealReturned() {
    assertThat(a.plus(b), is(Real.from(7.5)));
  }

  @Test
  public void whenRealSubtractedThenCorrectRealReturned() {
    assertThat(a.minus(b), is(Real.from(-1.5)));
  }

  @Test
  public void whenRealSqrtThenRightComplexReturned() {
    assertThat(a.sqrt(), is(new Complex(Math.sqrt(3.0))));
    a = Real.from(-3.0);
    assertThat(a.sqrt(), is(new Complex(0.0, Math.sqrt(3.0))));
  }
}
