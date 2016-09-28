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

  public WolfePowellLineSearch(final AbstractFunction f, final double c1, final double c2, final double f0,
          final double slope0) {
    this.f = f;
    this.c1 = c1;
    this.c2 = c2;
    this.f0 = f0;
    this.slope0 = slope0;
    this.alphaMax = 5.0;
  }

  public final double search() {
    final double fAlphaMax = f.at(alphaMax);
    final double dAlphaMax = f.slopeAt(alphaMax);
    double alphaIMinus1 = 0.0;
    double alphaLo = 0.0;
    double alphaHi = 0.0;
    //QuadraticInterpolation interpolation = new QuadraticInterpolation(alphaIMinus1, alphaMax, f0, fAlphaMax, slope0);
    CubicInterpolation interpolation = new CubicInterpolation(alphaIMinus1, alphaMax, f0, fAlphaMax, slope0, dAlphaMax);
    double alphaI = interpolation.minimum();
    IntervalValues alphaValues = updateInterval(new IntervalValues(alphaIMinus1, alphaMax, alphaI));
    alphaLo = alphaValues.alphaL;
    alphaHi = alphaValues.alphaU;
    alphaI = alphaValues.alphaT;
    double fAlphaI = 0.0;
    double dAlpha = 0.0;

    int i = 1;
    while (i < MAX_UPDATE_ITERATIONS) {
      fAlphaI = f.at(alphaI);
      if (firstWolfeConditionSatisfied(alphaI, fAlphaI)) {
        dAlpha = f.slopeAt(alphaI);
        if (secondWolfeConditionSatisfied(dAlpha)) {
          return alphaI;
        }
        if (dAlpha >= 0) {
          return zoom(alphaI, alphaIMinus1);
        }
        alphaIMinus1 = alphaI;
        alphaI = new CubicInterpolation(alphaIMinus1, alphaMax, fAlphaI, fAlphaMax, dAlpha, dAlphaMax).minimum();
      }
      else if (fAlphaI > f.at(alphaIMinus1) && i > 1) {
        return zoom(alphaIMinus1, alphaI);
      }
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
    double alphaS= 0.0;
    final double decreaseFactor = 2.0/3.0;
    
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
      }
      else if (fAlphaHi > fAlphaLo) {
        alphaC = new CubicInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi,
                dAlphaLo, dAlphaHi).minimum();
        alphaQ = new QuadraticInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi, dAlphaLo).minimum();
        alphaJ = (abs(alphaC - alphaLo) < abs(alphaQ - alphaLo))? alphaC : 0.5 * (alphaQ + alphaC);
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
  
  private final IntervalValues updateInterval(final IntervalValues alphas) {
    double alphaL = alphas.alphaL;
    double alphaU = alphas.alphaU;
    double alphaT = alphas.alphaT;
    double oldAlphaL = 0.0;
    double alphaTSlope = 0.0;

    boolean continueUpdating = true;
    while (continueUpdating) {
      // Case U1
      if (f.at(alphaT) + slope0 * c1 * (alphaL - alphaT) > f.at(alphaL)) {
        // alphaL stays the same and,
        alphaU = alphaT;
        continueUpdating = false;
      // Case U2
      } else if (((alphaTSlope = f.slopeAt(alphaT)) - c1 * slope0) * (alphaL - alphaT) > 0) {
        // Safeguard condition 2.2 (p. 291)
        oldAlphaL = alphaL;
        alphaL = alphaT;
        alphaT = Math.min(alphaT + DELTA_MAX * (alphaT - oldAlphaL), alphaMax);
      // Case U3
      } else if ((alphaTSlope - c1 * slope0) * (alphaL - alphaT) < 0){
        alphaU = alphaL;
        alphaL = alphaT;
        continueUpdating = false;
      }
    }
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
}
