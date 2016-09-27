package optim.doubles;

import optim.AbstractFunction;
import optim.Function;

public final class LineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 100;
  private static final double DELTA_MIN = 7.0 / 12.0;
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

  public LineSearch(final AbstractFunction f, final double c1, final double c2, final double alphaMin, final double f0,
          final double slope0) {
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

  private final IntervalValues refineInterval(final IntervalValues alphas) {
    final double alphaL = alphas.alphaL;
    final double alphaU = alphas.alphaU;
    final double alphaT = alphas.alphaT;
    final double newAlphaL;
    final double newAlphaU;
    final double newAlphaT;
    if (psi.at(alphaT) > psi.at(alphaL)) {
      newAlphaL = alphaL;
      newAlphaU = alphaT;
      newAlphaT = alphaT;
      // Case U2
    } else if (f.slopeAt(alphaT) * (alphaL - alphaT) > 0) {
      newAlphaL = alphaT;
      newAlphaU = alphaU;
      // Safeguard condition 2.2 (p. 291)
      newAlphaT = Math.min(alphaT + DELTA_MAX * (alphaT - alphaL), alphaMax);
      return refineInterval(new IntervalValues(newAlphaL, newAlphaU, newAlphaT));
      // Case U3
    } else {
      newAlphaL = alphaT;
      newAlphaU = alphaL;
      newAlphaT = alphaT;
    }
    return new IntervalValues(newAlphaL, newAlphaU, newAlphaT);
  }

  /**
   * The purpose of getting the initial interval is to find an interval satisfying the conditions
   * of theorem 2.1 (p. 290). If we start with an interval that satisfies these conditions then
   * using the updating algorithm described on p. 291 guarantees that all subsequent intervals
   * will also satisfy 2.1 and hence contain the minimum of the line function.
   * @return
   */
  private final IntervalValues getInitialInterval(final double alphaL, final double alphaU, final double alphaT) {
    final double newAlphaL;
    final double newAlphaU;
    final double newAlphaT;
    if (psi.at(alphaT) > psi.at(alphaL)) {
      // Theorem 2.1 is met.
      newAlphaL = alphaL;
      newAlphaU = alphaT;
      newAlphaT = alphaT;
      // Case U2
    } else if (f.slopeAt(alphaT) * (alphaL - alphaT) > 0) {
      // Theorem 2.1 is not met.
      newAlphaL = alphaT;
      newAlphaU = alphaU;
      // Safeguard condition 2.2 (p. 291)
      newAlphaT = Math.min(alphaT + DELTA_MAX * (alphaT - alphaL), alphaMax);
      if (newAlphaT == alphaMax) {
        return new IntervalValues(newAlphaL, newAlphaU, newAlphaT);
      }
      // try again.
      return getInitialInterval(newAlphaL, newAlphaU, newAlphaT);
      // Case U3
    } else {
      // Theorem 2.1 is met.
      newAlphaL = alphaT;
      newAlphaU = alphaL;
      newAlphaT = alphaT;
    }
    return new IntervalValues(newAlphaL, newAlphaU, newAlphaT);
  }
  
  final double search() {
    double alphaL = alphaMin;
    double alphaU = alphaMax;
    double alphaK = getSafeguardedTrialValue(alphaMin, alphaMax);
    IntervalValues alphas = getInitialInterval(alphaL, alphaU, alphaK);
    if (alphas.alphaT == alphaMax) {
      if (psi.at(alphaMax) <= 0 && psiSlope.at(alphaMax) < 0) {
        return alphaMax;
      }
    }

    int k = 0;
    while (!satisfiesStrongWolfeConditions(alphaK) && k < MAX_UPDATE_ITERATIONS) {
      alphas = refineInterval(alphas);
      alphaL = alphas.alphaL;
      alphaU = alphas.alphaU;
      k++;
      if (psi.at(alphaK) > 0 || psiSlope.at(alphaK) >= 0) {
        alphaK = getSafeguardedTrialValue(alphaMin, Math.max(DELTA_MIN * alphaK, alphaMin));
      }
      alphaK = getSafeguardedTrialValue(alphaL, alphaU);
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
    final double b = scaleFactor
            * (-alpha0cubed * (phiAlpha1 - f0 - slope0 * alpha1) + alpha1cubed * (phiAlpha0 - f0 - slope0 * alpha0));
    return ((-b + Math.sqrt(b * b - 3 * a * slope0)) / (3 * a));
  }

  private final double getSafeguardedTrialValue(final double alphaL, final double alphaU) {
    return 1.0;
  }

  private final boolean satisfiesStrongWolfeConditions(final double alpha) {
    return f.at(alpha) <= f0 + c1 * alpha * slope0 &&
            Math.abs(f.slopeAt(alpha)) <= c2 * Math.abs(slope0);
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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      long temp;
      temp = Double.doubleToLongBits(alphaL);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(alphaT);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(alphaU);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      IntervalValues other = (IntervalValues) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (Double.doubleToLongBits(alphaL) != Double.doubleToLongBits(other.alphaL)) return false;
      if (Double.doubleToLongBits(alphaT) != Double.doubleToLongBits(other.alphaT)) return false;
      if (Double.doubleToLongBits(alphaU) != Double.doubleToLongBits(other.alphaU)) return false;
      return true;
    }

    private LineSearch getOuterType() {
      return LineSearch.this;
    }

    @Override
    public String toString() {
      return "IntervalValues [alphaL=" + alphaL + ", alphaU=" + alphaU + ", alphaT=" + alphaT + "]";
    }
  }

}
