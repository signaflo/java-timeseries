package optim;

public abstract class AbstractFunction implements Function {

  public int functionEvaluations = 0;
  public int slopeEvaluations = 0;
  
  public double slopeAt(final double point) {
    slopeEvaluations++;
    return NumericalDerivatives.centralDifferenceApproximation(this, point, 1E-4);
  }
}
