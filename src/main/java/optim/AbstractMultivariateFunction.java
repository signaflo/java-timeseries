package optim;

import linear.doubles.Vector;

public abstract class AbstractMultivariateFunction implements MultivariateFunction {
  
  protected int functionEvaluations = 0;
  protected int gradientEvalutations = 0;
  private static final double gradientTolerance = 1E-4;
  
  public Vector gradientAt(Vector point) {
    gradientEvalutations++;
    return NumericalDerivatives.centralDifferenceGradient(this, point, gradientTolerance);
  }
  
  public Vector gradientAt(final Vector point, final double functionValue) {
    gradientEvalutations++;
    return NumericalDerivatives.forwardDifferenceGradient(this, point, gradientTolerance, functionValue);
  }
  
  public int functionEvaluations() {
    return this.functionEvaluations;
  }
  
  public int gradientEvaluations() {
    return this.gradientEvalutations;
  }

}
