package optim;

public abstract class AbstractFunction implements Function {

  public double slopeAt(final double point) {
    return NumericalDerivatives.centralDifferenceApproximation(this, point, 1E-4);
  }
}
