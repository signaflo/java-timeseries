package optim;

import linear.doubles.Vector;

public abstract class AbstractMultivariateFunction implements MultivariateFunction {
  
  private static final double gradientTolerance = 1E-4;
  
  public Vector gradientAt(Vector point) {
    return NumericalDerivatives.forwardDifferenceGradient(this, point, gradientTolerance);
  }

}
