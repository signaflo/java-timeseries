package optim;

public abstract class AbstractFunction implements Function {

  protected int functionEvaluations = 0;
  protected int slopeEvaluations = 0;
  
  public double slopeAt(final double point) {
    slopeEvaluations++;
    return NumericalDerivatives.centralDifferenceApproximation(this, point, 1E-4);
  }
  
  public int functionEvaluations() {
    return this.functionEvaluations;
  }
  
  public int slopeEvaluations() {
    return this.slopeEvaluations;
  }
}
