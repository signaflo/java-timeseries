package optim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Ignore;
import org.junit.Test;

import data.TestData;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import timeseries.TimeSeries;
import timeseries.models.arima.Arima.ModelOrder;
import timeseries.models.arima.Arima.OptimFunction;
import timeseries.models.arima.FittingStrategy;

public final class BFGSSpec {
  
  @Test
  public void testBFGS() {
    AbstractMultivariateFunction f = new RosenbrockFunction();
    Vector startingPoint = new Vector(0.5, 1.5);
    final double tol = 1E-6;
    BFGS solver = new BFGS(f, startingPoint, tol, 1e-8);
    System.out.println(solver.functionValue());
    System.out.println(solver.parameters());
    System.out.println(f.functionEvaluations);
    System.out.println(f.gradientEvalutations);
  }
  
  @Test
  @Ignore
  public void testHardProblem() {
    TimeSeries timeSeries = TestData.hardProblem();
    AbstractMultivariateFunction f = new OptimFunction(timeSeries, ModelOrder.order(1, 0, 1, 1, 0, 1), FittingStrategy.USS,
            12);
    final Vector initParams = new Vector(0, 0, 0, 0, timeSeries.mean());
    final Matrix initHessian = getInitialHessian(timeSeries, initParams.elements(), 1);
    new BFGS(f, initParams, 1e-8, 1e-8, initHessian);
    assertThat(f.functionEvaluations, is(lessThan(100)));
  }
  
  private Matrix getInitialHessian(final TimeSeries differencedSeries, final double[] initParams, final int constant) {
    final int n = initParams.length;
    final Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(n);
    if (constant == 1) {
      final double meanParScale = 10 * differencedSeries.stdDeviation() / Math.sqrt(differencedSeries.n());
      return builder.set(n - 1, n - 1, meanParScale).build();
    }
    return builder.build();
  }
}
