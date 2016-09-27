package optim.doubles;

import optim.AbstractFunction;
import optim.Function;

final class WolfePowellLineSearch {

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

  final double search() {
    final double fAlphaMax = f.at(alphaMax);
    final double dAlphaMax = f.slopeAt(alphaMax);
    double alphaIMinus1 = 0.0;
    CubicInterpolation interpolation = new CubicInterpolation(alphaIMinus1, alphaMax, f0, fAlphaMax, slope0, dAlphaMax);
    double alphaI = interpolation.minimum();
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
      
      alphaJ = new CubicInterpolation(alphaLo, alphaHi, fAlphaLo, fAlphaHi,
              dAlphaLo, dAlphaHi).minimum();
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
     
      k++;
    }
    return alphaJ;
  }
  
  private final boolean firstWolfeConditionSatisfied(final double alpha, final double fAlpha) {
    return fAlpha <= f0 + c1 * alpha * slope0;
  }

  private final boolean secondWolfeConditionSatisfied(final double derivAlpha) {
    return Math.abs(derivAlpha) <= c2 * Math.abs(slope0);
  }
}
