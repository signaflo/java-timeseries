package optim.doubles;

import static java.lang.Math.abs;
import optim.AbstractFunction;
import optim.Function;

public final class WolfePowellLineSearch {

  private static final int MAX_UPDATE_ITERATIONS = 100;
  private static final double DELTA_MIN = 7.0 / 12.0;
  private static final double DELTA_MAX = 2.0;

  private final AbstractFunction f;
  private final double c1;
  private final double c2;
  private final double f0;
  private final double slope0;
  private final double alphaMax;
  private final double alpha0;
  
  public WolfePowellLineSearch(final Builder builder) {
    this.f = builder.f;
    this.c1 = builder.c1;
    this.c2 = builder.c2;
    this.f0 = builder.f0;
    this.slope0 = builder.slope0;
    this.alphaMax = builder.alphaMax;
    this.alpha0 = builder.alpha0;
  }

  public final double search() {
    final double fAlphaMax = f.at(alphaMax);
    final double dAlphaMax = f.slopeAt(alphaMax);
    double alphaIMinus1 = 0.0;
    double alphaLo = 0.0;
    double alphaHi = 0.0;
    // QuadraticInterpolation interpolation = new QuadraticInterpolation(alphaIMinus1, alphaMax, f0, fAlphaMax, slope0);
    // CubicInterpolation interpolation = new CubicInterpolation(alphaIMinus1, alphaMax, f0, fAlphaMax, slope0,
    // dAlphaMax);
    double alphaI = alpha0; // interpolation.minimum();
    double fAlphaI = f.at(alphaI);
    double dAlpha = 0.0;
    double fAlphaIMinus1 = f0;

    IntervalValues alphaValues = updateInterval(new IntervalValues(alphaIMinus1, alphaMax, alphaI), f0, fAlphaI);
    alphaLo = alphaValues.alphaL;
    alphaHi = alphaValues.alphaU;
    alphaI = alphaValues.alphaT;

    int i = 1;
    while (i < MAX_UPDATE_ITERATIONS) {
      if (firstWolfeConditionSatisfied(alphaI, fAlphaI)) {
        dAlpha = f.slopeAt(alphaI);
        if (secondWolfeConditionSatisfied(dAlpha)) {
          return alphaI;
        }
        if (dAlpha >= 0) {
          return zoom(alphaI, alphaIMinus1);
        }
        alphaIMinus1 = alphaI;
        if (alphaI == alphaMax) {
          return alphaI;
        }
        alphaI = new CubicInterpolation(alphaIMinus1, alphaMax, fAlphaI, fAlphaMax, dAlpha, dAlphaMax).minimum();
      } else if (fAlphaI > fAlphaIMinus1 && i > 1) {
        return zoom(alphaIMinus1, alphaI);
      }
      alphaI = new CubicInterpolation(0.0, DELTA_MIN * alphaI, f0, f.at(DELTA_MIN * alphaI), slope0,
          f.slopeAt(DELTA_MIN * alphaI)).minimum();
      fAlphaI = f.at(alphaI);
      fAlphaIMinus1 = fAlphaI;
      i++;
    }
    return alphaI;
  }

  private final double zoom(double alphaLo, double alphaHi) {
    double oldIntervalLength = abs(alphaHi - alphaLo);
    double newIntervalLength = abs(alphaHi - alphaLo);
    int intervalCount = 0;
    double alphaTmp = 0.0;
    double fAlphaTmp = 0.0;
    double dAlphaTmp = 0.0;
    if (alphaLo > alphaHi) {
      alphaTmp = alphaLo;
      alphaLo = alphaHi;
      alphaHi = alphaTmp;
    }
    double alphaJ = 0.0;
    double fAlphaJ = 0.0;
    double dAlphaJ = 0.0;
    double fAlphaLo = f.at(alphaLo);
    double fAlphaHi = f.at(alphaHi);
    double dAlphaLo = f.slopeAt(alphaLo);
    double dAlphaHi = f.slopeAt(alphaHi);
    double alphaC = 0.0;
    double alphaQ = 0.0;
    double alphaS = 0.0;
    final double decreaseFactor = 2.0 / 3.0;

    int k = 0;
    while (k < MAX_UPDATE_ITERATIONS) {
      if (alphaLo > alphaHi) {
        alphaTmp = alphaLo;
        fAlphaTmp = fAlphaLo;
        dAlphaTmp = dAlphaLo;
        alphaLo = alphaHi;
        fAlphaLo = fAlphaHi;
        dAlphaLo = dAlphaHi;
        alphaHi = alphaTmp;
        fAlphaHi = fAlphaTmp;
        dAlphaHi = dAlphaTmp;
      }

      if (intervalCount > 2) {
        alphaJ = (alphaLo + alphaHi) / 2.0;
        intervalCount = 0;
      } else if (fAlphaHi > fAlphaLo) {
        alphaC = new CubicInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo, dAlphaHi).minimum();
        alphaQ = new QuadraticInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo).minimum();
        alphaJ = (abs(alphaC - alphaLo) < abs(alphaQ - alphaLo)) ? alphaC : 0.5 * (alphaQ + alphaC);
      }
      fAlphaJ = f.at(alphaJ);
      if (firstWolfeConditionSatisfied(alphaJ, fAlphaJ) && fAlphaJ - fAlphaLo < 1E-8) {
        dAlphaJ = f.slopeAt(alphaJ);
        if (secondWolfeConditionSatisfied(dAlphaJ)) {
          return alphaJ;
        }
        if ((dAlphaJ) * (alphaHi - alphaLo) >= 0) {
          alphaHi = alphaLo;
          fAlphaHi = fAlphaLo;
          dAlphaHi = dAlphaLo;
        }
        alphaLo = alphaJ;
        fAlphaLo = fAlphaJ;
        dAlphaLo = dAlphaJ;
      } else {
        alphaHi = alphaJ;
        fAlphaHi = fAlphaJ;
        dAlphaHi = dAlphaJ;
      }
      oldIntervalLength = newIntervalLength;
      newIntervalLength = abs(alphaHi - alphaLo);
      k++;
      if (((oldIntervalLength - newIntervalLength) / oldIntervalLength) < decreaseFactor) {
        intervalCount++;
      }
    }
    return alphaJ;
  }

  private final boolean firstWolfeConditionSatisfied(final double alpha, final double fAlpha) {
    return fAlpha <= f0 + c1 * alpha * slope0;
  }

  private final boolean secondWolfeConditionSatisfied(final double derivAlpha) {
    return abs(derivAlpha) <= c2 * abs(slope0);
  }

  // We pass in fAlphaL and fAlphaT, the function values at these points, to save unnecessary computations.
  private final IntervalValues updateInterval(final IntervalValues alphas, double fAlphaL, double fAlphaT) {
    double alphaL = alphas.alphaL;
    double alphaU = alphas.alphaU;
    double alphaT = alphas.alphaT;
    double oldAlphaL = 0.0;
    double dAlphaT = 0.0;

    // Case U1
    if (fAlphaT + slope0 * c1 * (alphaL - alphaT) > fAlphaL) {
      // alphaL stays the same and,
      alphaU = alphaT;
      // Case U2
    } else if (((dAlphaT = f.slopeAt(alphaT)) - c1 * slope0) * (alphaL - alphaT) > 0) {
      // Safeguard condition 2.2 (p. 291)
      oldAlphaL = alphaL;
      alphaL = alphaT;
      alphaT = Math.min(alphaT + DELTA_MAX * (alphaT - oldAlphaL), alphaMax);
      if (alphaT == alphaMax) {
        return (new IntervalValues(alphaL, alphaU, alphaT));
      }
      fAlphaL = fAlphaT;
      fAlphaT = f.at(alphaT);
      return updateInterval(new IntervalValues(alphaL, alphaU, alphaT), fAlphaL, fAlphaT);
      // Case U3
    } else if ((dAlphaT - c1 * slope0) * (alphaL - alphaT) < 0) {
      alphaU = alphaL;
      alphaL = alphaT;
    }
    // If none of the three conditionals above are true then we leave the interval unchanged.
    return new IntervalValues(alphaL, alphaU, alphaT);
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
    
    public final WolfePowellLineSearch build() {
      return new WolfePowellLineSearch(this);
    }
  }
}
