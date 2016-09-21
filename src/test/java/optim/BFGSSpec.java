package optim;

import org.junit.Test;

import linear.doubles.Vector;

public final class BFGSSpec {
  
  @Test
  public void testBFGS() {
    AbstractMultivariateFunction f = new RosenbrockFunction();
    Vector startingPoint = new Vector(-1.0, 0.5);
    final double tol = 1E-6;
    BFGS solver = new BFGS(f, startingPoint, tol);
    System.out.println(f.functionEvaluations);
  }


}
