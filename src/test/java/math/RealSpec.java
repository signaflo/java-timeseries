/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

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
    assertThat(a.additiveInverse(), is(Real.from(-3.0)));
  }

  @Test
  public void whenRealNumberSquaredResultCorrect() {
    assertThat(a.squared(), is(Real.from(9.0)));
  }

  @Test
  public void whenRealNumberCubedResultCorrect() {
    assertThat(a.cubed(), is(Real.from(27.0)));
  }

  @Test
  public void whenRealNumberDividedResultCorrect() {
    assertThat(a.dividedBy(Real.from(2.0)), is(Real.from(1.5)));
  }

  @Test
  public void whenRealIntervalThenLowerAndUpperReturned() {
    Real.Interval interval = new Real.Interval(3.0, 10.0);
    assertThat(interval.lower(), is(Real.from(3.0)));
    assertThat(interval.upper(), is(Real.from(10.0)));
    assertThat(interval.lowerDbl(), is(3.0));
    assertThat(interval.upperDbl(), is(10.0));
  }

  @Test
  public void whenRealIntervalContainsNumberThenTrue() {
    Real.Interval interval = new Real.Interval(3.0, 10.0);
    assertThat(interval.contains(9.99999999), is(true));
    assertThat(interval.contains(10.0000001), is(false));
    assertThat(interval.contains(2.9999999), is(false));
    interval = new Real.Interval(Real.from(10.0), Real.from(3.0));
    assertThat(interval.contains(9.99999999), is(true));
    assertThat(interval.contains(10.0000001), is(false));
    assertThat(interval.contains(2.99999999), is(false));
  }

  @Test
  public void whenRealIntervalDoesntContainNumberThenFalse() {
    Real.Interval interval = new Real.Interval(3.0, 10.0);
    assertThat(interval.doesntContain(9.99999999), is(false));
    assertThat(interval.doesntContain(10.0000001), is(true));
    interval = new Real.Interval(Real.from(10.0), Real.from(3.0));
    assertThat(interval.doesntContain(9.99999999), is(false));
    assertThat(interval.doesntContain(10.0000001), is(true));
  }

  @Test
  public void testEqualsAndHashCode() {
    Real c = new Real(3.0);
    //noinspection ObjectEqualsNull
    assertThat(a.equals(null), is(false));
    assertThat(a, is(not((b))));
    assertThat(a, is(a));
    assertThat(a, is(c));
    assertThat(a.hashCode(), is(c.hashCode()));
  }

  @Test
  public void testIntervalEqualsAndHashCode() {
    Real.Interval a = new Real.Interval(3.0, 5.0);
    Real.Interval b = new Real.Interval(5.0, 3.0);
    Real.Interval c = new Real.Interval(3.0, 5.0);
    //noinspection ObjectEqualsNull
    assertThat(a.equals(null), is(false));
    assertThat(a, is(not(new Object())));
    assertThat(a, is(not((b))));
    assertThat(a, is(a));
    assertThat(a, is(c));
    assertThat(a.hashCode(), is(c.hashCode()));
  }
}
