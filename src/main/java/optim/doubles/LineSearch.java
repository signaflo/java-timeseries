package optim.doubles;

import optim.AbstractFunction;
import optim.Function;

public final class LineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 100;
  private static final double DELTA_MIN = 7.0/12.0;
  private static final double DELTA_MAX = 2.0;
  
  private final AbstractFunction f;
  private final Function psi;
  private final Function psiSlope;
  private final double c1;
  private final double c2;
  private final double f0;
  private final double slope0;
  private final double alphaMin;
  private final double alphaMax;
  private double stepLength;

  public LineSearch(final AbstractFunction f, final double c1, final double c2,
          final double alphaMin, final double f0, final double slope0) {
    this.f = f;
    this.c1 = c1;
    this.c2 = c2;
    this.f0 = f0;
    this.slope0 = slope0;
    this.alphaMin = alphaMin;
    this.alphaMax = -1.0 / slope0;
    this.psi = (alpha) -> f.at(alpha) - f0 - c1 * alpha * slope0;
    this.psiSlope = (alpha) -> f.slopeAt(alpha) - c1 * slope0;

  }

  private final IntervalValues updateInterval(final IntervalValues alphas) {
    double alphaL = alphas.alphaL;
    double alphaU = alphas.alphaU;
    double alphaT = alphas.alphaT;
    double oldAlphaL;

    boolean continueUpdating = true;
    while (continueUpdating) {
      // Case U1
      if (f.at(alphaT) > f.at(alphaL)) {
        // alphaL stays the same and,
        alphaU = alphaT;
        continueUpdating = false;
      // Case U2
      } else if (f.slopeAt(alphaT) * (alphaL - alphaT) > 0) {
        // Safeguard condition 2.2 (p. 291)
        oldAlphaL = alphaL;
        alphaL = alphaT;
        alphaT = Math.min(alphaT + DELTA_MAX * (alphaT - oldAlphaL), alphaMax);
      // Case U3
      } else {
        alphaU = alphaL;
        alphaL = alphaT;
        continueUpdating = false;
      }
    }
    return new IntervalValues(alphaL, alphaU, alphaT);
  }

  private final double search() {
    double alphaL = alphaMin;
    double alphaU = alphaMax;
    double alphaK = getTrialValue(alphaL, alphaU);
    IntervalValues alphas = new IntervalValues(alphaL, alphaU, alphaK);

    int k = 0;
    while (!converged() && k < MAX_UPDATE_ITERATIONS) {
      alphas = updateInterval(alphas);
      alphaL = alphas.alphaL;
      alphaU = alphas.alphaU;
      k++;
      if (psi.at(alphaK) > 0 || psiSlope.at(alphaK) >= 0) {
        alphaK = getTrialValue(alphaMin, Math.max(DELTA_MIN * alphaK, alphaMin));
      }
      alphaK = getTrialValue(alphaL, alphaU);
    }
    return alphas.alphaT;
  }
  
  private final double quadraticMinimum(final double alpha) {
    return -0.5 * (slope0 / (alpha - f0 - slope0));
  }
  
  private final double cubicMinimum(final double alpha0, final double alpha1, final double phiAlpha0,
      final double phiAlpha1) {
    final double alpha0squared = alpha0 * alpha0;
    final double alpha1squared = alpha1 * alpha1;
    final double alpha0cubed = alpha0squared * alpha0;
    final double alpha1cubed = alpha1squared * alpha1;
    final double scaleFactor = 1 / (alpha0squared * alpha1squared * (alpha1 - alpha0));
    final double a = scaleFactor * (alpha0squared * (phiAlpha1 - f0 - slope0 * alpha1)
        + -alpha1squared * (phiAlpha0 - f0 - slope0 * alpha0));
    final double b = scaleFactor * (-alpha0cubed * (phiAlpha1 - f0 - slope0 * alpha1)
        + alpha1cubed * (phiAlpha0 - f0 - slope0 * alpha0));
    return ((-b + Math.sqrt(b * b - 3 * a * slope0)) / (3 * a));
  }

  private final double getTrialValue(final double alphaL, final double alphaU) {
    return 0.0;
  }

  private final boolean converged() {
    return false;
  }
  
  private final class IntervalValues {
    private final double alphaL;
    private final double alphaU;
    private final double alphaT;

    IntervalValues(final double alphaL, final double alphaU, final double alphaT) {
      this.alphaL = alphaL;
      this.alphaU = alphaU;
      this.alphaT = alphaT;
    }
  }

}
