package math.function;

import linear.doubles.Vector;
import optim.SphereFunction;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

public class GeneralFunctionSpec {

    @Test
    public void whenFunctionEvaluatedCorrectResult() {
        GeneralFunction f = new GeneralFunction((x) -> x * x);
        assertThat(f.at(3.0), is(9.0));
    }

    @Test
    public void whenSlopeAtThenSlopeCorrect() {
        GeneralFunction f = new GeneralFunction((x) -> x * x, (x) -> 2 * x);
        assertThat(f.slopeAt(3.0), is(6.0));
    }

    @Test
    public void whenAbstractFunctionGradientThenCorrectVector() {
        AbstractMultivariateFunction f = new SphereFunction();
        assertArrayEquals(Vector.from(2.0, 4.0, 6.0).elements(), f.gradientAt(Vector.from(1.0, 2.0, 3.0)).elements(),
                          1E-4);
    }

    @Test
    public void whenDfNotSetThenDerivativeApproximated() {
        GeneralFunction f = new GeneralFunction((x) -> x * x);
        assertThat(f.slopeAt(3.0), is(closeTo(6.0, 1E-8)));
    }

    @Test
    public void whenDfSetThenDerivativeExact() {
        GeneralFunction f = new GeneralFunction((x) -> x * x);
        f.setDf((x) -> 2 * x);
        assertThat(f.slopeAt(3.0), is(6.0));
    }
}
