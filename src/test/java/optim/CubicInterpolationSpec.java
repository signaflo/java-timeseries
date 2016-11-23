/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class CubicInterpolationSpec {
  
  @Rule
  public final ExpectedException exception = ExpectedException.none();
  
  @Test
  public void whenTwoXValuesTheSameExceptionThrown() {
    exception.expect(RuntimeException.class);
    new CubicInterpolation(1.0, 1.0, 2.5, 2.5, -0.1, 0.1);
  }
  
  @Test
  public void whenCubicInterpolationPerformedMinimumCorrect() {
    CubicInterpolation interpolation = new CubicInterpolation(1.0, 3.0, -1.0/3.0, -3.0/11.0, -1.0/9.0, 0.057851);
    assertThat(interpolation.minimum(), is(closeTo(1.528795, 1E-4)));
  }

}
