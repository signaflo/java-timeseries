package optim;

import linear.doubles.Vector;

public final class LineSearch {
  
  final Vector gradient;
  final Vector searchDirection;
  final double constant;
  final double stepLength;
  
  public LineSearch(final double constant, final Vector gradient, final Vector searchDirection,
      final double stepLength) {
    this.constant = constant;
    this.stepLength = stepLength;
    this.gradient = gradient;
    this.searchDirection = searchDirection;
  }

}
