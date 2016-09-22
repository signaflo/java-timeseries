package optim.doubles;

import optim.AbstractFunction;

public final class LineSearch {
  
  final AbstractFunction f;
  final double c1;
  final double c2;
  final double f0;
  final double slope0;
  final double alphaMin;
  final double alphaMax;
  double stepLength;
  
  public LineSearch(final AbstractFunction f, final double c1, final double c2, final double alphaMin, final double f0,
          final double slope0) {
    this.f = f;
    this.c1 = c1;
    this.c2 = c2;
    this.f0 = f0;
    this.slope0 = slope0;
    this.alphaMin = alphaMin;
    this.alphaMax = -1.0 / slope0;
    
  }

}
