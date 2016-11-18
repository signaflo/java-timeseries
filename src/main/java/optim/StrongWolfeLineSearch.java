/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package optim;

import math.Real;
import math.function.AbstractFunction;
import math.function.Function;

import static java.lang.Math.abs;

/**
 * A line search implementation designed to find a point in the domain that satisfies the strong Wolfe conditions.
 */
final class StrongWolfeLineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 40;
  private static final double DELTA_MAX = 4.0;
  private static final double DELTA_MIN = 7.0 / 12.0;

  private final AbstractFunction phi;
  private final double c1;
  private final double c2;
  private final double f0; // The value of phi at alpha = 0.
  private final double slope0; // The slope of phi at alpha = 0.
  private final double alphaMin;
  private final double alphaMax;
  private final Function psi;
  private final Function dPsi;
  int m = 0; // A count of the highest level iteration.
  private double alphaLower;
  private double psiAlphaLower;
  private double dPsiAlphaLower;
  private double alphaUpper;
  private double psiAlphaUpper;
  private double dPsiAlphaUpper;
  private double alphaT;
  private double psiAlphaT;
  private double dPsiAlphaT;
  private double tolerance;

  /**
   * Use a builder to create a new line search object.
   *
   * @param builder the line search builder containing the necessary data for the new object.
   */
  private StrongWolfeLineSearch(final Builder builder) {
    this.phi = builder.phi;
    this.c1 = builder.c1;
    this.c2 = builder.c2;
    this.f0 = builder.f0;
    this.slope0 = builder.slope0;
    this.alphaMin = 1E-4;
    this.alphaMax = builder.alphaMax;
    this.alphaT = builder.alpha0;
    this.psi = (alpha) -> phi.at(alpha) - f0 - c1 * slope0 * alpha;
    this.dPsi = (alpha) -> phi.slopeAt(alpha) - c1 * slope0;
    this.psiAlphaT = psi.at(alphaT);
    this.dPsiAlphaT = dPsi.at(alphaT);
    this.alphaLower = 0;
    this.psiAlphaLower = 0;
    this.dPsiAlphaLower = slope0 * (1 - c1);
    this.tolerance = 1E-8;
  }

  /**
   * Get a new builder for the line search.
   *
   * @param f      the function.
   * @param f0     the value of the function at 0.
   * @param slope0 the slope of the function at 0.
   * @return a new builder for the line search.
   */
  static Builder newBuilder(final AbstractFunction f, final double f0, final double slope0) {
    return new Builder(f, f0, slope0);
  }

  /**
   * Perform the line search , returning an element satisfying the strong Wolfe conditions.
   *
   * @return an element satisfying the strong Wolfe conditions.
   */
  final double search() {
    if (psiAlphaT <= tolerance  && ((abs(dPsiAlphaT + c1 *slope0) - c2 * abs(slope0)) < tolerance)) {
      return alphaT;
    }
    Real.Interval initialInterval = getInitialInterval();
    m++;
    if (initialInterval.endpointsEqual()) {
      return initialInterval.lowerDbl();
    }
    return zoom(initialInterval);
  }

  private double zoom(Real.Interval interval) {
    alphaLower = interval.lowerDbl();
    alphaUpper = interval.upperDbl();
    double priorAlphaUpper = alphaMax;
    double oldIntervalLength = abs(alphaUpper - alphaLower);
    double newIntervalLength;
//
//    if (psiAlphaT <= abs(tolerance)  && ((abs(dPsiAlphaT + c1 *slope0) - c2 * abs(slope0)) < abs(tolerance))) {
//      return alphaT;
//    }
    int trials = 0;
    int k = 1;
    while (k < MAX_UPDATE_ITERATIONS) {
      while (Double.isInfinite(psiAlphaUpper) && k < MAX_UPDATE_ITERATIONS) {
        alphaUpper = 0.5 * alphaUpper;
        psiAlphaUpper = psi.at(alphaUpper);
        dPsiAlphaUpper = dPsi.at(alphaUpper);
        k++;
      }
      newIntervalLength = abs(alphaUpper - alphaLower);
      if (abs((newIntervalLength - oldIntervalLength) / oldIntervalLength) < 0.667 && trials > 1) {
        alphaT = abs(alphaLower + alphaUpper) / 2.0;
        trials = 0;
        oldIntervalLength = newIntervalLength;
      } else {
        alphaT = getTrialValue(alphaLower, alphaUpper, psiAlphaLower, psiAlphaUpper, dPsiAlphaLower,
            dPsiAlphaUpper, priorAlphaUpper);
        trials++;
      }
      psiAlphaT = psi.at(alphaT);
      dPsiAlphaT = dPsi.at(alphaT);
      if (psiAlphaT <= tolerance  && ((abs(dPsiAlphaT + c1 *slope0) - c2 * abs(slope0)) < tolerance)) {
        return alphaT;
      }
      interval = updateInterval(alphaLower, alphaT, alphaUpper, psiAlphaLower, psiAlphaT, dPsiAlphaT);
      alphaLower = interval.lowerDbl();
      priorAlphaUpper = alphaUpper;
      alphaUpper = interval.upperDbl();
      if (alphaLower == alphaUpper) {
        return alphaLower;
      }
      m++;
      k++;
    }
    return alphaT;
  }

  private Real.Interval updateInterval(final double alphaLower, final double alphaK, final double alphaUpper,
                                       final double psiAlphaLower, final double psiAlphaK, final double dPsiAlphaK) {
    if (psiAlphaLower > psiAlphaUpper || psiAlphaLower > 0 || dPsiAlphaLower * (alphaUpper - alphaLower) >= 0) {
      throw new RuntimeException("The assumption of Theorem 2.1 (More-Thuente 1994) are not met.");
    }
    if (psiAlphaK > psiAlphaLower) {
      this.alphaUpper = alphaK;
      this.psiAlphaUpper = psiAlphaK;
      this.dPsiAlphaUpper = dPsiAlphaK;
      return new Real.Interval(alphaLower, alphaK);
    }
    if (dPsiAlphaK * (alphaLower - alphaK) > 0) {
      this.alphaLower = alphaK;
      this.psiAlphaLower = psiAlphaK;
      this.dPsiAlphaLower = dPsiAlphaK;
      return new Real.Interval(alphaK, alphaUpper);
    } else if (dPsiAlphaK * (alphaLower - alphaK) < 0) {
      this.alphaUpper = alphaLower;
      this.psiAlphaUpper = psiAlphaLower;
      this.dPsiAlphaUpper = dPsiAlphaLower;
      this.alphaLower = alphaK;
      this.psiAlphaLower = psiAlphaK;
      this.dPsiAlphaLower = dPsiAlphaK;
      return new Real.Interval(alphaK, alphaLower);
    } else {
      return new Real.Interval(alphaK, alphaK);
    }
  }

  // Return an interval containing at least one point satisfying the Strong Wolfe Conditions.
  private Real.Interval getInitialInterval() {
    double priorAlphaLower;
    int k = 0;
    while (k < 1000) {
      if (psiAlphaT > psiAlphaLower) {
        this.alphaUpper = alphaT;
        this.psiAlphaUpper = psiAlphaT;
        this.dPsiAlphaUpper = dPsiAlphaT;
        return new Real.Interval(alphaLower, alphaT);
      }
      if (dPsiAlphaT * (alphaLower - alphaT) > 0) {
        priorAlphaLower = alphaLower;
        alphaLower = alphaT;
        psiAlphaLower = psiAlphaT;
        if (psiAlphaT <= 0 && dPsiAlphaT < 0) {
          alphaT = Math.min(alphaT + DELTA_MAX * (alphaT - priorAlphaLower), alphaMax);
          psiAlphaT = psi.at(alphaT);
          dPsiAlphaT = dPsi.at(alphaT);
        }
        if (alphaT == alphaMax) {
          return new Real.Interval(alphaMax, alphaMax);
        }
      } else if (dPsiAlphaT * (alphaLower - alphaT) < 0) {
        this.alphaUpper = alphaLower;
        this.psiAlphaUpper = psiAlphaLower;
        this.dPsiAlphaUpper = dPsiAlphaLower;
        this.alphaLower = alphaT;
        this.psiAlphaLower = psiAlphaT;
        this.dPsiAlphaLower = dPsiAlphaT;
        return new Real.Interval(alphaT, alphaLower);
      } else {
        return new Real.Interval(alphaT, alphaT);
      }
      k++;
    }
    throw new RuntimeException("Couldn't find an interval containing a point satisfying the Strong " +
        "Wolfe conditions.");
  }

  private double getTrialValue(final double alphaL, final double alphaT, double fAlphaL, double fAlphaT,
                               double dAlphaL, double dAlphaT, double alphaU) {
    final double alphaC;
    final double alphaQ;
    final double alphaS;
    if (fAlphaT > fAlphaL) {
      alphaC = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
      alphaQ = new QuadraticInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL).minimum();
      return (abs(alphaC - alphaL) < abs(alphaQ - alphaL)) ? alphaC : 0.5 * (alphaQ + alphaC);
    } else if (dAlphaL * dAlphaT < 0) {
      alphaC = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaL, alphaT, dAlphaL, dAlphaT);
      return (abs(alphaC - alphaT) >= abs(alphaS - alphaT)) ? alphaC : alphaS;
    } else if (abs(dAlphaT) <= abs(dAlphaL)) {
      if (alphaL == alphaT) {
        return alphaT;
      }
      alphaC = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaL, alphaT, dAlphaL, dAlphaT);
      double newAlphaT = (abs(alphaC - alphaT) < abs(alphaS - alphaT)) ? alphaC : alphaS;
      if (alphaT > alphaL) {
        return Math.min(alphaT + 0.67 * (alphaU - alphaT), newAlphaT);
      }
      return Math.max(alphaT + 0.67 * (alphaU - alphaT), newAlphaT);
    } else {
      double fAlphaU = psi.at(alphaU);
      double dAlphaU = dPsi.at(alphaU);
      return new CubicInterpolation(alphaU, alphaT, fAlphaU, fAlphaT, dAlphaU, dAlphaT).minimum();
    }
  }

  /**
   * A builder for the line search.
   */
  static class Builder {

    private final AbstractFunction phi;
    private final double f0;
    private final double slope0;
    private double c1 = 1E-3;
    private double c2 = 0.5;
    private double alphaMax = 1000.0;
    private double alpha0 = 1.0;

    Builder(AbstractFunction phi, double f0, double slope0) {
      this.phi = phi;
      this.f0 = f0;
      this.slope0 = slope0;
    }

    final Builder c1(double c1) {
      this.c1 = c1;
      return this;
    }

    final Builder c2(double c2) {
      this.c2 = c2;
      return this;
    }

    final Builder alphaMax(double alphaMax) {
      this.alphaMax = alphaMax;
      return this;
    }

    final Builder alpha0(double alpha0) {
      this.alpha0 = alpha0;
      return this;
    }

    public final StrongWolfeLineSearch build() {
      return new StrongWolfeLineSearch(this);
    }
  }
}
