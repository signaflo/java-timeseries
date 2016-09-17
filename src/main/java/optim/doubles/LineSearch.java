package optim.doubles;

import optim.MultivariateFunction;
import static data.Operators.scaled;
import static data.Operators.sumOf;

public final class LineSearch {
  
  final MultivariateFunction f;
  final double[] gradient;
  final double[] searchDirection;
  final double constant;
  final double stepLength;
  
  public LineSearch(final MultivariateFunction f, final double constant, final double[] gradient, final double[] searchDirection,
      final double stepLength) {
    this.f = f;
    this.constant = constant;
    this.stepLength = stepLength;
    this.gradient = gradient.clone();
    this.searchDirection = searchDirection.clone();
    sumOf(gradient, scaled(searchDirection, stepLength));
  }

}
