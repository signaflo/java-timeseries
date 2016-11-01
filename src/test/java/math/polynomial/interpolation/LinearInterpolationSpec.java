/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial.interpolation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class LinearInterpolationSpec {
  
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Test
  public void whenTwoPointsSameExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    new LinearInterpolation(3.0, 3.0, 7.0, 10.0);
  }
  
  @Test
  public void whenFunctionValuesGivenFirstCoefficientCorrect() {
   LinearInterpolation interpolation = new LinearInterpolation(3.5, 5.0, 12.25, 25.0);
   assertThat(interpolation.a0(), is(equalTo(-17.5)));
  }
  
  @Test
  public void whenFunctionValuesGivenSecondCoefficientCorrect() {
    LinearInterpolation interpolation = new LinearInterpolation(3.5, 5.0, 12.25, 25.0);
    assertThat(interpolation.a1(), is(equalTo(8.5)));
  }

}
