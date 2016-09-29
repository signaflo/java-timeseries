package optim.doubles;

import static java.lang.Math.abs;

import optim.AbstractFunction;

public final class StrongWolfeLineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 100;
  private static final double DELTA_MIN = 7.0 / 12.0;
  private static final double DELTA_MAX = 2.0;

  private final AbstractFunction f;
  private final double c1;
  private final double c2;
  private final double f0;
  private final double slope0;
  private final double alphaMax;
  private final double alpha1;

  public StrongWolfeLineSearch(final Builder builder) {
    this.f = builder.f;
    this.c1 = builder.c1;
    this.c2 = builder.c2;
    this.f0 = builder.f0;
    this.slope0 = builder.slope0;
    this.alphaMax = builder.alphaMax;
    this.alpha1 = builder.alpha0;
  }

  public final double search() {

    double alphaT = 0.0;
    double newAlphaT = alpha1;
    double alphaL = 0.0;
    double newAlphaL = 1E-3;
    double alphaU = 0.0;
    double newAlphaU = alphaMax;

    double fAlphaT = f.at(newAlphaT);
    double dAlphaT = 0.0;
    double fAlphaL = f.at(newAlphaL);
    double fAlphaU = 0.0;
    double dAlphaL = 0.0;
    double dAlphaU = 0.0;
    
    double[] interval = updateInterval(newAlphaL, newAlphaU, newAlphaT, fAlphaL, fAlphaT);
    newAlphaL = interval[0]; newAlphaU = interval[1];
    fAlphaL = f.at(newAlphaL);
    fAlphaU = f.at(newAlphaU);
    dAlphaL = f.slopeAt(newAlphaL);
    dAlphaU = f.slopeAt(newAlphaU);
    
    newAlphaT = new CubicInterpolation(newAlphaL, newAlphaU, fAlphaL, fAlphaU, dAlphaL, dAlphaU).minimum();

    int k = 1;
    while (k < MAX_UPDATE_ITERATIONS) {
      alphaT = newAlphaT;
      alphaL = newAlphaL;
      alphaU = newAlphaU;
      
      fAlphaT = f.at(alphaT);
      dAlphaT = f.slopeAt(alphaT);
      if (firstWolfeConditionSatisfied(alphaT, fAlphaT) && secondWolfeConditionSatisfied(dAlphaT)) {
        return alphaT;
      }
      else {
        interval = updateInterval(alphaL, alphaU, alphaT, fAlphaL, fAlphaT);
        newAlphaL = interval[0]; newAlphaU = interval[1];
        fAlphaL = f.at(alphaL);
        //fAlphaU = f.at(alphaU);
        dAlphaL = f.slopeAt(alphaL);
        //dAlphaU = f.slopeAt(alphaU);
        newAlphaT = new CubicInterpolation(alphaL, alphaT, fAlphaL, fAlphaT, dAlphaL, dAlphaT).minimum();
        k++;
      }
    }
    return newAlphaT;
  }

  private final boolean firstWolfeConditionSatisfied(final double alpha, final double fAlpha) {
    return fAlpha <= f0 + c1 * alpha * slope0;
  }

  private final boolean secondWolfeConditionSatisfied(final double derivAlpha) {
    return (abs(derivAlpha) -  c2 * abs(slope0)) <= 1E-12;
  }

  // We pass in fAlphaL and fAlphaT, the function values at these points, to save unnecessary computations.
  private final double[] updateInterval(double alphaL, double alphaU, double alphaT, double fAlphaL,
      double fAlphaT) {
    double oldAlphaT = 0.0;
    double dAlphaT = 0.0;
    // Case U1
    if (fAlphaT + slope0 * c1 * (alphaL - alphaT) > fAlphaL) {
      // alphaL stays the same and,
      alphaU = alphaT;
    } else if (((dAlphaT = f.slopeAt(alphaT)) - c1 * slope0) * (alphaL - alphaT) > 0) {
      // Safeguard condition 2.2 (p. 291)
      oldAlphaT = alphaL;
      alphaL = alphaT;
      alphaT = Math.min(alphaT + DELTA_MAX * (alphaT - oldAlphaT), alphaMax);
      if (alphaT == alphaMax) {
        return (new double[] {alphaL, alphaU});
      }
      fAlphaL = fAlphaT;
      fAlphaT = f.at(alphaT);
      return updateInterval(alphaL, alphaU, alphaT, fAlphaL, fAlphaT);

      // Case U3
    } else if ((dAlphaT - c1 * slope0) * (alphaL - alphaT) < 0) {
      alphaU = alphaL;
      alphaL = alphaT;
    }
    // If none of the three conditionals above are true then we leave the interval unchanged.
    if (alphaL < alphaU) {
      return new double[] {alphaL, alphaU};
    }
    return new double[] {alphaU, alphaL};
  }

  public static final Builder newBuilder(final AbstractFunction f, final double f0, final double slope0) {
    return new Builder(f, f0, slope0);
  }

  public static final class Builder {

    private final AbstractFunction f;
    private double c1 = 0.1;
    private double c2 = 0.1;
    private final double f0;
    private final double slope0;
    private double alphaMax = 1000.0;
    private double alpha0 = 5.0;

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
