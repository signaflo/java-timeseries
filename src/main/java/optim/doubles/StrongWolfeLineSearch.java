package optim.doubles;

import static java.lang.Math.abs;

import optim.AbstractFunction;

public final class StrongWolfeLineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 100;
  private static final double DELTA_MAX = 4.0;

  private final AbstractFunction f;
  private final double c1;
  private final double c2;
  private final double f0;
  private final double slope0;
  private final double alphaMin = 1E-3;
  private final double alphaMax;
  private final double alpha0;

  private double oldAlphaT = 0.0;
  private double alphaT = 0.0;
  private double newAlphaT = 0.0;

  private double fAlphaT = 0.0;
  private double dAlphaT = 0.0;
  private double fNewAlphaT = 0.0;
  private double dNewAlphaT = 0.0;

  private double gradientTolerance = 1E-8;
  int m = 0;

  public StrongWolfeLineSearch(final Builder builder) {
    this.f = builder.f;
    this.c1 = builder.c1;
    this.c2 = builder.c2;
    this.f0 = builder.f0;
    this.slope0 = builder.slope0;
    this.alphaMax = builder.alphaMax;
    this.alpha0 = builder.alpha0;
  }

  public final double search() {
    fNewAlphaT = f0;
    dNewAlphaT = slope0;
    newAlphaT = alpha0;

    int i = 1;
    while (i < MAX_UPDATE_ITERATIONS) {
      m++;
      fAlphaT = fNewAlphaT;
      dAlphaT = dNewAlphaT;
      fNewAlphaT = f.at(newAlphaT);
      if (abs(fNewAlphaT) == Double.POSITIVE_INFINITY) {
        return alphaMin;
      }
      dNewAlphaT = f.slopeAt(newAlphaT);
      if (fNewAlphaT > f0 + c1 * newAlphaT * slope0 || fNewAlphaT >= fAlphaT && i > 1) {
        return zoom(alphaT, newAlphaT, fAlphaT, fNewAlphaT, dAlphaT, dNewAlphaT);
      }
      // dNewAlphaT = f.slopeAt(newAlphaT);
      if (abs(dNewAlphaT) <= -c2 * slope0) {
        return newAlphaT;
      }
      if (dNewAlphaT >= 0) {
        return zoom(newAlphaT, alphaT, fNewAlphaT, fAlphaT, dNewAlphaT, dAlphaT);
      }
      oldAlphaT = alphaT;
      alphaT = newAlphaT;
      // Safeguard condition 2.2 since alphaT is increasing.
      newAlphaT = Math.min(alphaT + DELTA_MAX * (alphaT - oldAlphaT), alphaMax);
      i++;
    }
    return newAlphaT;
  }

  private final double zoom(double alphaLo, double alphaHi, double fAlphaLo, double fAlphaHi, double dAlphaLo,
      double dAlphaHi) {
    double alphaJ = 0.0;
    double fAlphaJ = 0.0;
    double dAlphaJ = 1.0;
    // double mid = 0.0;
    // double fMid = 0.0;

    int k = 1;
    while (k < MAX_UPDATE_ITERATIONS && abs(dAlphaJ) > gradientTolerance) {
      m++;
      // mid = 0.80*alphaLo + 0.2*alphaHi;
      // fMid = f.at(mid);
      // alphaJ = QuadraticInterpolation.threePointMinimum(alphaLo, mid, alphaHi, fAlphaLo, fMid, fAlphaHi);
      alphaJ = getTrialValue(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo, dAlphaHi);
      if (alphaJ < alphaMin) {
        alphaJ = 0.8 * alphaLo + 0.2 * alphaHi;
      }
      fAlphaJ = f.at(alphaJ);
      dAlphaJ = f.slopeAt(alphaJ);
      if (fAlphaJ > f0 + c1 * alphaJ * slope0 || fAlphaJ >= fAlphaLo) {
        alphaHi = alphaJ;
        fAlphaHi = fAlphaJ;
        dAlphaHi = dAlphaJ;
      } else {
        if (abs(dAlphaJ) <= -c2 * slope0) {
          return alphaJ;
        }
        if (dAlphaJ * (alphaHi - alphaLo) >= 0) {
          alphaHi = alphaLo;
          fAlphaHi = fAlphaLo;
          dAlphaHi = dAlphaLo;
        }
        alphaLo = alphaJ;
        fAlphaLo = fAlphaJ;
        dAlphaLo = dAlphaJ;
      }
      k++;
    }
    return alphaJ;
  }

  private final double getTrialValue(final double alphaLo, final double alphaHi, double fAlphaLo, double fAlphaHi,
      double dAlphaLo, double dAlphaHi) {
    final double alphaC;
    final double alphaQ;
    final double alphaS;
    if (fAlphaHi > fAlphaLo) {
      alphaC = new CubicInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo, dAlphaHi).minimum();
      alphaQ = new QuadraticInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo).minimum();
      return (abs(alphaC - alphaLo) < abs(alphaQ - alphaLo)) ? alphaC : 0.5 * (alphaQ + alphaC);
    } else if (dAlphaLo * dAlphaHi < 0) {
      alphaC = new CubicInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo, dAlphaHi).minimum();
      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaLo, alphaHi, dAlphaLo, dAlphaHi);
      return (abs(alphaC - alphaHi) >= abs(alphaS - alphaHi)) ? alphaC : alphaS;
    } else if (abs(dAlphaHi) <= abs(dAlphaLo)) {
      alphaS = QuadraticInterpolation.secantFormulaMinimum(alphaLo, alphaHi, dAlphaLo, dAlphaHi);
      return alphaS;
    } else {
      return new CubicInterpolation(alphaHi, alphaLo, fAlphaHi, fAlphaLo, dAlphaHi, dAlphaLo).minimum();
    }
  }

  public static final Builder newBuilder(final AbstractFunction f, final double f0, final double slope0) {
    return new Builder(f, f0, slope0);
  }

  public static final class Builder {

    private final AbstractFunction f;
    private double c1 = 1E-3;
    private double c2 = 0.5;
    private final double f0;
    private final double slope0;
    private double alphaMax = 1000.0;
    private double alpha0 = 1.0;

    public Builder(AbstractFunction f, double f0, double slope0) {
      this.f = f;
      this.f0 = f0;
      this.slope0 = slope0;
    }

    public final Builder c1(double c1) {
      this.c1 = c1;
      return this;
    }

    public final Builder c2(double c2) {
      this.c2 = c2;
      return this;
    }

    public final Builder alphaMax(double alphaMax) {
      this.alphaMax = alphaMax;
      return this;
    }

    public final Builder alpha0(double alpha0) {
      this.alpha0 = alpha0;
      return this;
    }

    public final StrongWolfeLineSearch build() {
      return new StrongWolfeLineSearch(this);
    }
  }
}
