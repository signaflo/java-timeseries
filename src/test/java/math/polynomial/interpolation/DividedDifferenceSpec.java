/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial.interpolation;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Jacob Rachiele
 */
public class DividedDifferenceSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenPointAndValuesLengthDifferThenException() {
    exception.expect(IllegalArgumentException.class);
    new DividedDifference(new double[] {0.3}, new double[] {0.4, 1.1});
  }

  @Test
  public void whenNoDataThenException() {
    exception.expect(IllegalArgumentException.class);
    new DividedDifference(new double[] {}, new double[] {});
  }

  @Test
  public void whenGetDividedDifferenceStartLessThanEndThenException() {
    exception.expect(IllegalArgumentException.class);
    DividedDifference dd = new DividedDifference(new double[] {2.0}, new double[] {4.0});
    dd.getDividedDifference(2, 1);
  }

  @Test
  public void whenGetDividedDifferenceStartEqulasEndThenFunctionValue() {
    DividedDifference dd = new DividedDifference(new double[] {2.0}, new double[] {4.0});
    assertThat(dd.getDividedDifference(0, 0), is(equalTo(4.0)));
  }

  @Test
  public void whenOnePointThenCoefficientEqualsFunctionValue() {
    DividedDifference dd = new DividedDifference(new double[] {2.0}, new double[] {4.0});
    assertThat(dd.getCoefficient(0), is(equalTo(4.0)));
  }

  @Test
  public void whenMultiplePointsThenFirstCoefficientEqualsFirstFunctionValue() {
    DividedDifference dd = new DividedDifference(new double[] {2.0, 3.0}, new double[] {4.0, 9.0});
    assertThat(dd.getCoefficient(0), is(equalTo(4.0)));
  }

  @Test
  public void whenTwoPointsThenSecondCoefficientEqualsLinearSlopeValue() {
    DividedDifference dd = new DividedDifference(new double[] {2.0, 3.0}, new double[] {4.0, 9.0});
    assertThat(dd.getCoefficient(1), is(equalTo(5.0)));
  }

  @Test
  public void whenThreePointsThenThirdCoefficientEqualsThirdDividedDifference() {
    DividedDifference dd = new DividedDifference(new double[] {2.0, 4.0, 7.0}, new double[] {4.0, 16.0, 49.0});
    assertThat(dd.getCoefficient(2), is(equalTo(1.0)));
  }
}
