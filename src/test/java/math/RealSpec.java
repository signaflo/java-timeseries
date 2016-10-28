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

  @Test
  public void whenAdditiveInverseThenRightNumberReturned() {
    assertThat(a.negative(), is(Real.from(-3.0)));
  }

  @Test
  public void whenEqualsAndHashCodeThenCorrectValues() {
    assertThat(a.equals(new Object()), is(false));
    assertThat(a.equals(null), is(false));
    assertThat(a.equals(b), is(false));
    assertThat(a.equals(a), is(true));
    assertThat(a.equals(new Real(3.0)), is(true));
    assertThat(a.hashCode(), is(new Real(3.0).hashCode()));
    assertThat(a.hashCode(), is(not(b.hashCode())));
  }
}
