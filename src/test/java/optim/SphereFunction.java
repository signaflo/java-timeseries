package optim;

import linear.doubles.Vector;

public final class SphereFunction extends AbstractMultivariateFunction implements MultivariateFunction {

  @Override
  public double at(Vector point) {
    return point.at(0) * point.at(0) + point.at(1) * point.at(1);
  }
  
  @Override
  public Vector gradientAt(Vector point) {
    return NumericalDerivatives.centralDifferenceGradient(this, point, 1E-4);
  }

}
