package com.github.signaflo.math.optim;

import com.github.signaflo.math.function.AbstractMultivariateFunction;
import com.github.signaflo.math.linear.doubles.Vector;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

public final class BFGSSpec {

    @Test
    public void testBFGS() {
        AbstractMultivariateFunction f = new RosenbrockFunction();
        Vector startingPoint = Vector.from(0.5, 1.5);
        final double tol = 1E-8;
        BFGS solver = new BFGS(f, startingPoint, tol, 1e-8);
        assertThat(solver.functionValue(), is(closeTo(0.0, 1E-7)));
        double[] expectedSolution = {1.0, 1.0};
        assertArrayEquals(expectedSolution, solver.parameters().elements(), 1E-3);
        double[] expectedHessian = {0.2, 0.4, 0.4, 0.8};
        assertArrayEquals(expectedHessian, solver.inverseHessian().data(), 1E-2);

    }
}
