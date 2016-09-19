package optim;

import org.junit.Test;

import linear.doubles.Vector;

public final class BFGSSpec {
  
  @Test
  public void testBFGS() {
    AbstractMultivariateFunction f = new SphereFunction();
    Vector startingPoint = new Vector(1.0, 2.5);
    final double tol = 1E-8;
    BFGS solver = new BFGS(f, startingPoint, tol);
  }


}
