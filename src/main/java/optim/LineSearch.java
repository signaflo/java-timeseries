package optim;

import linear.doubles.Vector;

public final class LineSearch {

  final AbstractMultivariateFunction f;
  final Vector point;
  final Vector gradient;
  final Vector searchDirection;
  final double c1;
  final double c2;
  final double initialStepLength;
  final double functionValue;
  final double[] alpha = new double[100];
  final double[] phiAtAlpha = new double[100];
  final double initialdotProduct;

  public LineSearch(final AbstractMultivariateFunction f, final double functionValue, final double c1, final double c2,
      final Vector gradient, final Vector point, final Vector searchDirection, final double initialStepLength) {
    this.f = f;
    this.functionValue = functionValue;
    this.c1 = c1;
    this.c2 = c2;
    this.initialStepLength = initialStepLength;
    this.gradient = gradient;
    this.searchDirection = searchDirection;
    this.point = point;
    this.initialdotProduct = gradient.dotProduct(searchDirection);
    alpha[0] = initialStepLength;
    phiAtAlpha[0] = f.at(point.plus(searchDirection));
  }

  public final double computeAlpha() {
    Vector newPoint = point.plus(searchDirection);
    if (sufficientDecrease(newPoint, 1.0) && curvatureConditionMet(newPoint)) {
      return 1.0;
    }
    alpha[1] = quadraticInterpolation();
    if (alpha[1] < 1E-4 || Math.abs(1 - alpha[1]) < 1E-1) {
      alpha[1] = 0.5;
    }
    newPoint = point.plus(searchDirection.scaledBy(alpha[1]));
    phiAtAlpha[1] = f.at(newPoint);
    if (sufficientDecrease(newPoint, alpha[1]) && curvatureConditionMet(newPoint)) {
      return alpha[1];
    }
    int i = 2;
    double phiAlphaDot = 0.0;
    while (true) {
      alpha[i] = cubicInterpolation(alpha[i - 2], alpha[i - 1], phiAtAlpha[i - 2], phiAtAlpha[i - 1]);
//      if (alpha[i] < 1E-4 || Math.abs(alpha[i] - alpha[i - 1]) < 1E-1) {
//        alpha[i] = 0.5;
//      }
      newPoint = point.plus(searchDirection.scaledBy(alpha[i]));
      phiAtAlpha[i] = f.at(newPoint);
      if (!sufficientDecrease(newPoint, alpha[i]) ||
          phiAtAlpha[i] >= phiAtAlpha[i - 1]) {
        return zoom(alpha[i - 1], alpha[i]);
      }
      phiAlphaDot = f.gradientAt(newPoint).dotProduct(searchDirection);
      if (Math.abs(phiAlphaDot) <= c2 * Math.abs(initialdotProduct)) {
        return alpha[i];
      }
      if (phiAlphaDot >= 0) {
        return zoom(alpha[i], alpha[i - 1]);
      }
      alpha[i + 1] = cubicInterpolation(alpha[i], 1.0, phiAtAlpha[i], functionValue);
      i += 1;
    }
  }
  
  private final boolean sufficientDecrease(final Vector newPoint, final double alpha) {
    final double newValue = f.at(newPoint);
    if (newValue <= functionValue + c1 * alpha * initialdotProduct) {
      return true;
    }
    return false;
  }
  
  private final boolean curvatureConditionMet(final Vector newPoint) {
    final Vector newGradient = f.gradientAt(newPoint);
    final double newDotProduct = newGradient.dotProduct(searchDirection);
    if (newDotProduct >= c2 * initialdotProduct) {
      return true;
    }
    return false;
  }

  private final double cubicInterpolation(final double alpha0, final double alpha1, final double phiAlpha0,
      final double phiAlpha1) {
    final double alpha0squared = alpha0 * alpha0;
    final double alpha1squared = alpha1 * alpha1;
    final double alpha0cubed = alpha0squared * alpha0;
    final double alpha1cubed = alpha1squared * alpha1;
    final double scaleFactor = 1 / (alpha0squared * alpha1squared * (alpha1 - alpha0));
    final double a = scaleFactor * (alpha0squared * (phiAlpha1 - functionValue - initialdotProduct * alpha1)
        + -alpha1squared * (phiAlpha0 - functionValue - initialdotProduct * alpha0));
    final double b = scaleFactor * (-alpha0cubed * (phiAlpha1 - functionValue - initialdotProduct * alpha1)
        + alpha1cubed * (phiAlpha0 - functionValue - initialdotProduct * alpha0));
    return ((-b + Math.sqrt(b * b - 3 * a * initialdotProduct)) / (3 * a));
  }

  private final double quadraticInterpolation() {
    return -0.5 * (initialdotProduct / (phiAtAlpha[0] - functionValue - initialdotProduct));
  }

  private final double zoom(double alphaLo, double alphaHi) {
    final int maxIter = 1000;
    int i = 0;
    while (true) {
      double phiAlphaLo = f.at(point.plus(searchDirection.scaledBy(alphaLo)));
      double phiAlphaHi = f.at(point.plus(searchDirection.scaledBy(alphaHi)));
      
      final double alphaJ = cubicInterpolation(alphaLo, alphaHi, phiAlphaLo, phiAlphaHi);
      final double phiAlphaJ = f.at(point.plus(searchDirection.scaledBy(alphaJ)));
      if (phiAlphaJ > (functionValue + c1 * alphaJ * initialdotProduct) || phiAlphaJ >= phiAlphaLo) {
        alphaHi = alphaJ;
      } else {
        final double alphaJDot = f.gradientAt(point.plus(searchDirection.scaledBy(alphaJ))).dotProduct(searchDirection);
        if (Math.abs(alphaJDot) <= -c2 * initialdotProduct) {
          return alphaJ;
        }
        if (alphaJDot * (alphaHi - alphaLo) >= 0) {
          alphaHi = alphaLo;
        }
        alphaLo = alphaJ;
      }
      i++;
    }
  }

}
