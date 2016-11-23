package optim;

import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;

public final class SphereFunction extends AbstractMultivariateFunction {

  @Override
  public double at(Vector point) {
    functionEvaluations++;
    return point.sumOfSquares();
  }
  
//  @Override
//  public Vector gradientAt(Vector point) {
//    double[] gradient = new double[point.size()];
//    for (int i = 0; i < gradient.length; i++) {
//      gradient[i] = 2 * point.at(i);
//    }
//    return Vector.from(gradient);
//  }

}
