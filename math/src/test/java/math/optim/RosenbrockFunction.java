package math.optim;


import static java.lang.Math.pow;
import math.linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;

public final class RosenbrockFunction extends AbstractMultivariateFunction {

  @Override
  public final double at(final Vector point) {
    functionEvaluations++;
    final int n = point.size();
    double sum = 0.0;
    for (int i = 0; i < (n - 1); i++) {
      sum += 100 * (pow(point.at(i + 1) - pow(point.at(i), 2), 2)) +
          pow(1 - point.at(i), 2);
    }
    return sum;  
  }
  
  @Override
  public final Vector gradientAt(final Vector point) {
    gradientEvalutations++;
    final double x = point.at(0);
    final double y = point.at(1);
    final double gx = -400 * x * (y - x * x) - 2 * (1 - x);
    final double gy = 200 * (y - x * x);
    return new Vector(gx, gy);
  }

}
