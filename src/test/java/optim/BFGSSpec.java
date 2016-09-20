package optim;

import org.junit.Test;

import linear.doubles.Vector;

public final class BFGSSpec {
  
  @Test
  public void testBFGS() {
    AbstractMultivariateFunction f = new RosenbrockFunction();
    Vector startingPoint = new Vector(0.5, 1.0);
    final double tol = 1E-8;
    BFGS solver = new BFGS(f, startingPoint, tol);
    System.out.println(f.functionEvaluations);
  }


}
