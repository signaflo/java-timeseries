package optim;

import linear.doubles.Vector;

public abstract class AbstractMultivariateFunction implements MultivariateFunction {
  
  int functionEvaluations = 0;
  int gradientEvalutations = 0;
  private static final double gradientTolerance = 1E-4;
  
  public Vector gradientAt(Vector point) {
    gradientEvalutations++;
    return NumericalDerivatives.forwardDifferenceGradient(this, point, gradientTolerance);
  }
  
  public Vector gradientAt(final Vector point, final double functionValue) {
    gradientEvalutations++;
    return NumericalDerivatives.forwardDifferenceGradient(this, point, gradientTolerance, functionValue);
  }

}
