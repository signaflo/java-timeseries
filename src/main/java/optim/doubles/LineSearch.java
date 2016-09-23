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
    double alphaT = getTrialValue(alphaL, alphaU);

    boolean continueUpdating = true;
    while (continueUpdating) {
      if (f.at(alphaT) > f.at(alphaL)) {
        // alphaL stays the same and...
        alphaU = alphaT;
        continueUpdating = false;
      } else if (f.slopeAt(alphaT) * (alphaL - alphaT) > 0) {
        alphaL = alphaT;
        alphaT = getTrialValue(alphaL, alphaU);
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
      alphaK = getTrialValue(alphaL, alphaU);
    }
    return alphas.alphaT;
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
