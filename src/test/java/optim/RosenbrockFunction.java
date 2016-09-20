package optim;


import static java.lang.Math.pow;
import linear.doubles.Vector;

public final class RosenbrockFunction extends AbstractMultivariateFunction {

  @Override
  public final double at(final Vector point) {
    final int n = point.size();
    double sum = 0.0;
    for (int i = 0; i < (n - 1); i++) {
      sum += 100*(pow(point.at(i + 1) - pow(point.at(0), 2), 2)) +
          pow(point.at(0) - 1, 2);
    }
    return sum;  
  }
  
  @Override
  public final Vector gradientAt(final Vector point) {
    return NumericalDerivatives.centralDifferenceGradient(this, point, 1E-4);
  }

}
