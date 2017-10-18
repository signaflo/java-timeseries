/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */
package com.github.signaflo.math.optim;

import com.github.signaflo.math.Real;
import com.github.signaflo.math.function.AbstractFunction;
import com.github.signaflo.math.function.Function;
import com.github.signaflo.math.function.SlopeFunction;

import static java.lang.Math.abs;

/**
 * A line search implementation designed to find a point in the domain that satisfies the strong Wolfe conditions.
 */
final class StrongWolfeLineSearch {

    private static final int MAX_UPDATE_ITERATIONS = 40;
    private static final double DELTA_MAX = 4.0;
    private static final double DELTA_MIN = 7.0 / 12.0;
    private static final double EPSILON = Math.ulp(1.0);

    private final AbstractFunction phi;
    private final double c1;
    private final double c2;
    private final double f0; // The value of phi at alpha = 0.
    private final double slope0; // The slope of phi at alpha = 0.
    private final double alphaMax;
    private final Function psi;
    private final SlopeFunction dPsi;
    private double alphaLower;
    private double psiAlphaLower;
    private double dPsiAlphaLower;
    private double alphaUpper;
    private double psiAlphaUpper;
    private double dPsiAlphaUpper;
    private double alphaT;
    private double psiAlphaT;
    private double dPsiAlphaT;
    private final double tolerance;

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
        this.alphaMax = builder.alphaMax;
        this.alphaT = builder.alpha0;
        this.psi = (alpha) -> phi.at(alpha) - f0 - c1 * slope0 * alpha;
        this.dPsi = (alpha, f) -> phi.slopeAt(alpha, f) - c1 * slope0;
        this.psiAlphaT = psi.at(alphaT);
        this.dPsiAlphaT = dPsi.at(alphaT, psiAlphaT + f0 + c1 * slope0 * alphaT);
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
     * Perform the line search, returning an element satisfying the strong Wolfe conditions.
     *
     * @return an element satisfying the strong Wolfe conditions.
     */
    double search() {
        if (psiAlphaT <= tolerance && ((abs(dPsiAlphaT + c1 * slope0) - c2 * abs(slope0)) < tolerance)) {
            return alphaT;
        }
        Real.Interval initialInterval = getInitialInterval();
        if (initialInterval.endpointsEqual(EPSILON)) {
            return initialInterval.lowerDbl();
        }
        return zoom();
    }

    private double zoom() {
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
                dPsiAlphaUpper = dPsi.at(alphaUpper, psiAlphaUpper + f0 + c1 * slope0 * alphaUpper);
                k++;
            }
            newIntervalLength = abs(alphaUpper - alphaLower);
            if (abs((newIntervalLength - oldIntervalLength) / oldIntervalLength) < 0.667 && trials > 1) {
                alphaT = abs(alphaLower + alphaUpper) / 2.0;
                trials = 1;
                oldIntervalLength = newIntervalLength;
            } else {
                alphaT = getTrialValue(alphaLower, alphaUpper, psiAlphaLower, psiAlphaUpper, dPsiAlphaLower,
                                       dPsiAlphaUpper);
                trials++;
            }
            if (Double.isNaN(alphaT)) {
                throw new NaNStepLengthException("The step length in Strong Wolfe line search was NaN");
            }
            psiAlphaT = psi.at(alphaT);
            dPsiAlphaT = dPsi.at(alphaT, psiAlphaT + f0 + c1 * slope0 * alphaT);
//      double phiAlphaT = phi.at(alphaT);
//      double dPhiAlphaT = phi.slopeAt(alphaT);
            if (psiAlphaT <= 0 && ((abs(dPsiAlphaT - c1 * slope0) - c2 * abs(slope0)) < 0)) {
                return alphaT;
            }
            updateInterval(alphaLower, alphaT, alphaUpper, psiAlphaLower, psiAlphaT, dPsiAlphaT);
            if (alphaLower == alphaUpper) {
                return alphaLower;
            }
            k++;
        }
        return alphaT;
    }

    private void updateInterval(final double alphaLower, final double alphaK, final double alphaUpper,
                                final double psiAlphaLower, final double psiAlphaK, final double dPsiAlphaK) {
        if (psiAlphaLower > psiAlphaUpper || psiAlphaLower > 0 || dPsiAlphaLower * (alphaUpper - alphaLower) >= 0) {
            throw new RuntimeException("The assumptions of Theorem 2.1 (More-Thuente 1994) are not met.");
        }
        if (psiAlphaK > psiAlphaLower) {
            this.alphaUpper = alphaK;
            this.psiAlphaUpper = psiAlphaK;
            this.dPsiAlphaUpper = dPsiAlphaK;
            //return new Real.Interval(alphaLower, alphaK);
        } else if (dPsiAlphaK * (alphaLower - alphaK) > 0) {
            this.alphaLower = alphaK;
            this.psiAlphaLower = psiAlphaK;
            this.dPsiAlphaLower = dPsiAlphaK;
            //return new Real.Interval(alphaK, alphaUpper);
        } else if (dPsiAlphaK * (alphaLower - alphaK) < 0) {
            this.alphaUpper = alphaLower;
            this.psiAlphaUpper = psiAlphaLower;
            this.dPsiAlphaUpper = dPsiAlphaLower;
            this.alphaLower = alphaK;
            this.psiAlphaLower = psiAlphaK;
            this.dPsiAlphaLower = dPsiAlphaK;
            //return new Real.Interval(alphaK, alphaLower);
        } else {
            this.alphaT = this.alphaLower = this.alphaUpper;
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
                dPsiAlphaLower = dPsiAlphaT;
                if (psiAlphaT <= 0 && dPsiAlphaT < 0) {
                    alphaT = Math.min(alphaT + DELTA_MAX * (alphaT - priorAlphaLower), alphaMax);
                    psiAlphaT = psi.at(alphaT);
                    dPsiAlphaT = dPsi.at(alphaT, psiAlphaT + f0 + c1 * slope0 * alphaT);
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
        throw new RuntimeException(
                "Couldn't find an interval containing a point satisfying the Strong " + "Wolfe conditions.");
    }

    private double getTrialValue(final double alphaL, final double alphaT, double fAlphaL, double fAlphaT,
                                 double dAlphaL, double dAlphaT) {
        double alphaC = CubicInterpolation.minimum(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT);
        double alphaQ = QuadraticInterpolation.minimum(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL);
        return (abs(alphaC - alphaL) < abs(alphaQ - alphaL)) ? alphaC : 0.5 * (alphaQ + alphaC);
    }
//
//  private double getTrialValue(final double alphaL, final double alphaT, double fAlphaL, double fAlphaT,
//                               double dAlphaL, double dAlphaT, double alphaU) {
//    final double alphaC;
//    final double alphaQ;
//    final double alphaS;
//    if (fAlphaT > fAlphaL) {
//      alphaC = CubicInterpolation.minimum(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT);
//      alphaQ = QuadraticInterpolation.minimum(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL);
//      return (abs(alphaC - alphaL) < abs(alphaQ - alphaL)) ? alphaC : 0.5 * (alphaQ + alphaC);
//    } else if (dAlphaL * dAlphaT < 0) {
//      alphaC = CubicInterpolation.minimum(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT);
//      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaL, alphaT, dAlphaL, dAlphaT);
//      return (abs(alphaC - alphaT) >= abs(alphaS - alphaT)) ? alphaC : alphaS;
//    } else if (abs(dAlphaT) <= abs(dAlphaL)) {
//      if (alphaL == alphaT) {
//        return alphaT;
//      }
//      alphaC = CubicInterpolation.minimum(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT);
//      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaL, alphaT, dAlphaL, dAlphaT);
//      double newAlphaT = (abs(alphaC - alphaT) < abs(alphaS - alphaT)) ? alphaC : alphaS;
//      if (alphaT > alphaL) {
//        return Math.min(alphaT + 0.67 * (alphaU - alphaT), newAlphaT);
//      }
//      return Math.max(alphaT + 0.67 * (alphaU - alphaT), newAlphaT);
//    } else {
//      double fAlphaU = psi.at(alphaU);
//      double dAlphaU = dPsi.at(alphaU);
//      return new CubicInterpolation(alphaU, alphaT, fAlphaU, fAlphaT, dAlphaU, dAlphaT).minimum();
//    }
//  }

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
