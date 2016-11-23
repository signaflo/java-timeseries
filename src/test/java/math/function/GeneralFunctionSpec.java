package math.function;

import linear.doubles.Vector;
import optim.SphereFunction;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;

/**
 * Created by jacob on 11/23/16.
 */
public class GeneralFunctionSpec {

  @Test
  public void whenFunctionEvaluatedCorrectResult() {
    GeneralFunction f = new GeneralFunction((x) -> x * x);
    assertThat(f.at(3.0), is(9.0));
  }

  @Test
  public void whenAbstractFunctionGradientThenCorrectVector() {
    AbstractMultivariateFunction f = new SphereFunction();
    assertArrayEquals(Vector.from(2.0, 4.0, 6.0).elements(),
        f.gradientAt(Vector.from(1.0, 2.0, 3.0)).elements(), 1E-4);
  }
}
