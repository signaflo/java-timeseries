package optim.doubles;

import optim.AbstractFunction;
import optim.Function;

final class WolfePowellLineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 100;
  private static final double DELTA_MIN = 7.0 / 12.0;
  private static final double DELTA_MAX = 2.0;

  private final AbstractFunction f;
  private final double c1;
  private final double c2;
  private final double f0;
  private final double slope0;
  private final double alphaMax;

  public WolfePowellLineSearch(final AbstractFunction f, final double c1, final double c2, final double f0,
          final double slope0) {
    this.f = f;
    this.c1 = c1;
    this.c2 = c2;
    this.f0 = f0;
    this.slope0 = slope0;
    this.alphaMax = -1.0 / slope0;
  }

  final double search() {
    double a = 0.0;
    double b = alphaMax;
    double fA = f0;
    double dA = slope0;
    double fMax = f.at(alphaMax);
    double dMax = f.slopeAt(alphaMax);
    CubicInterpolation interpolation = new CubicInterpolation(a, b, f0, fMax, slope0, dMax);
    double alpha = interpolation.minimum();
    double alphaHat = 0.0;
    double fAlpha = f.at(alpha);
    double dAlpha = 0.0;
    
    int k = 0;
    while (k < MAX_UPDATE_ITERATIONS) {
      if (firstWolfeConditionSatisfied(alpha, fAlpha)) {
        dAlpha = f.slopeAt(alpha);
        if (secondWolfeConditionSatisfied(dAlpha)) {
          return alpha;
        }
        alphaHat = alpha + ((alpha - a) * dAlpha) / (slope0 - dAlpha);
        a = alpha;
        fA = fAlpha;
        dA = dAlpha;
        alpha = alphaHat;
      }
      k++;
    }
    return alpha;
  }

  private final boolean firstWolfeConditionSatisfied(final double alpha, final double fAlpha) {
    return fAlpha <= f0 + c1 * alpha * slope0;
  }

  private final boolean secondWolfeConditionSatisfied(final double derivAlpha) {
    return Math.abs(derivAlpha) <= c2 * slope0;
  }
}
