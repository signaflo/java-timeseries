package optim;

import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;

public final class SphereFunction extends AbstractMultivariateFunction {

  @Override
  public double at(Vector point) {
    functionEvaluations++;
    return point.sumOfSquares();
  }
  
  @Override
  public Vector gradientAt(Vector point) {
    return NumericalDerivatives.centralDifferenceGradient(this, point, 1E-4);
  }

}
