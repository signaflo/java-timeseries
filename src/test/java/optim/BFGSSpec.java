package optim;

import math.function.AbstractMultivariateFunction;
import org.junit.Test;

import linear.doubles.Vector;

public final class BFGSSpec {
  
  @Test
  public void testBFGS() {
    AbstractMultivariateFunction f = new RosenbrockFunction();
    Vector startingPoint = new Vector(0.5, 1.5);
    final double tol = 1E-8;
    BFGS solver = new BFGS(f, startingPoint, tol, 1e-8);
  }
}
