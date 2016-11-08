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
  private static final double DELTA_MIN = 7.0/12.0;

  private final AbstractFunction f;
  private final double c1;
  private final double c2;
  private final double f0;
  private final double slope0;
  private final double alphaMin;
  private final double alphaMax;
  private final double alpha0;
  int m = 0;
  private double alphaT = 0.0;

  private final Function phi;
  private final Function dPhi;

  /**
   * Use a builder to create a new line search object.
   *
   * @param builder the line search builder containing the necessary data for the new object.
   */
  private StrongWolfeLineSearch(final Builder builder) {
    this.f = builder.f;
    this.c1 = builder.c1;
    this.c2 = builder.c2;
    this.f0 = builder.f0;
    this.slope0 = builder.slope0;
    this.alphaMin = 1E-4;
    this.alphaMax = builder.alphaMax;
    this.alpha0 = builder.alpha0;
    this.phi = (alpha) -> f.at(alpha) - f0 - c1 * slope0 * alpha;
    this.dPhi = (alpha) -> f.slopeAt(alpha) - c1 * slope0;
  }

  /**
   * Perform the line search , returning an element satisfying the strong Wolfe conditions.
   *
   * @return an element satisfying the strong Wolfe conditions.
   */
  final double search() {
    Real.Interval initialInterval = getInterval(0.0, alpha0);
    m++;
    return zoom(initialInterval);
  }

  private double zoom(Real.Interval interval) {
    double alphaLower = interval.lowerDbl();
    double priorAlphaLower = alphaLower;
    double alphaUpper = interval.upperDbl();
    double phiAlphaLower = phi.at(priorAlphaLower);
    double phiAlphaT = phi.at(alphaT);
    double dPhiAlphaLower = dPhi.at(priorAlphaLower);
    double dPhiAlphaT = dPhi.at(alphaT);
    double intervalLength = 0.0;
    double updatedIntervalLength;
    double tolerance = 1E-8;

    int trials = 0;
    int k = 1;
    double dAlpha;
    while (k < MAX_UPDATE_ITERATIONS) {
      if (trials == 0) {
        intervalLength = abs(alphaUpper - alphaLower);
      }
      updatedIntervalLength = abs(alphaUpper - alphaLower);
      if (abs((updatedIntervalLength - intervalLength) / intervalLength) < 0.667 && trials > 2) {
        alphaT = abs(alphaLower + alphaUpper) / 2.0;
        trials = 0;
      } else {
        alphaT = getTrialValue(priorAlphaLower, alphaT, phiAlphaLower, phiAlphaT, dPhiAlphaLower, dPhiAlphaT);
        phiAlphaT = phi.at(alphaT);
        dPhiAlphaT = dPhi.at(alphaT);
        trials++;
      }
      dAlpha = f.slopeAt(alphaT);
      if (phiAlphaT <= 0 && abs(dAlpha) <= c2 * abs(slope0)) {
        return alphaT;
      }
      if (abs(dPhiAlphaT) < tolerance) {
        return alphaT;
      }
      priorAlphaLower = interval.lowerDbl();
      phiAlphaLower = phi.at(priorAlphaLower);
      dPhiAlphaLower = dPhi.at(priorAlphaLower);
      interval = getInterval(alphaLower, alphaT);
      alphaLower = interval.lowerDbl();
      alphaUpper = interval.upperDbl();

      m++;
      k++;
    }
    return alphaT;
  }


  private Real.Interval getInterval(double alphaLower, double alphaK) {
    double phiAlphaLower = phi.at(alphaLower);
    double phiAlphaK = phi.at(alphaK);
    double dPhiAlphaK;
    double previousAlphaLower;
    int k = 0;
    while (k < 1000) {
      if (phiAlphaK > phiAlphaLower) {
        return new Real.Interval(alphaLower, alphaK);
      }
      dPhiAlphaK = dPhi.at(alphaK);
      if (dPhiAlphaK * (alphaLower - alphaK) > 0) {
        previousAlphaLower = alphaLower;
        alphaLower = alphaK;
        if (phiAlphaK <= 0 && dPhiAlphaK < 0) {
          alphaK = Math.min(alphaK + DELTA_MAX * (alphaK - previousAlphaLower), alphaMax);
        } else {
          double phiAlphaMin = phi.at(alphaMin);
          double alphaUp = Math.max(DELTA_MIN * alphaK, alphaMin);
          double phiAlphaUp = phi.at(alphaUp);
          double dPhiAlphaMin = dPhi.at(alphaMin);
          double dPhiAlphaUp = dPhi.at(alphaUp);
          alphaK = getTrialValue(alphaMin, alphaUp, phiAlphaMin, phiAlphaUp, dPhiAlphaMin, dPhiAlphaUp);
        }
        if (alphaK == alphaMax) {
          return new Real.Interval(alphaMax, alphaMax);
        }
      } else if (dPhiAlphaK * (alphaLower - alphaK) < 0){
        return new Real.Interval(alphaK, alphaLower);
      } else {
        return new Real.Interval(alphaK, alphaK);
      }
      k++;
    }
    throw new RuntimeException("Couldn't find an interval containing a point satisfying the Strong " +
        "Wolfe conditions.");
  }

  private double getTrialValue(final double alphaL, final double alphaT, double fAlphaL, double fAlphaT,
                               double dAlphaL, double dAlphaT) {
    final double alphaC;
    final double alphaQ;
    final double alphaS;
    if (fAlphaT > fAlphaL) {
      //alphaC = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
      alphaC = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
      alphaQ = new QuadraticInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL).minimum();
      return (abs(alphaC - alphaL) < abs(alphaQ - alphaL)) ? alphaC : 0.5 * (alphaQ + alphaC);
    } else if (dAlphaL * dAlphaT < 0) {
      alphaC = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaL, alphaT, dAlphaL, dAlphaT);
      return (abs(alphaC - alphaT) >= abs(alphaS - alphaT)) ? alphaC : alphaS;
    } else if (abs(dAlphaT) <= abs(dAlphaL)) {
      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaL, alphaT, dAlphaL, dAlphaT);
      return alphaS;
    } else {
      return new CubicInterpolation(alphaT, alphaL, fAlphaT, fAlphaL, dAlphaT, dAlphaL).minimum();
    }
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
   * A builder for the line search.
   */
  static final class Builder {

    private final AbstractFunction f;
    private final double f0;
    private final double slope0;
    private double c1 = 1E-3;
    private double c2 = 0.5;
    private double alphaMax = 1000.0;
    private double alpha0 = 1.0;

    Builder(AbstractFunction f, double f0, double slope0) {
      this.f = f;
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
