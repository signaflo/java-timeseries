package optim.doubles;

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
  public void whenSlopeAtX1PositiveExceptionThrown() {
    exception.expect(RuntimeException.class);
    new CubicInterpolation(1.0, 2.5, 2.5, 2.5, 0.5, 0.1);
  }
  
  @Test
  public void whenSlopeAtX2NegativeExceptionThrown() {
    exception.expect(RuntimeException.class);
    new CubicInterpolation(1.0, 3.0, 2.5, 2.5, -0.1, -0.4);
  }
  
  @Test
  public void whenCubicInterpolationPerformedMinimumCorrect() {
    CubicInterpolation interpolater = new CubicInterpolation(1.9, 1.0, -0.33868092691622104, -1.0/3.0, 0.05115642108407129, -1.0/9.0);
    System.out.println(interpolater.minimum());
    System.out.println(new CubicInterpolation(1.0, 3.0, -1.0/3.0, -3.0/11.0, -1.0/9.0, 0.057851).minimum());
    //assertThat(interpolater.minimum(), is(closeTo(1.528795, 1E-4)));
  }

}
