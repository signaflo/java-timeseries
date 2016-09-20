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
  final double[] alpha = new double[1000];
  final double[] phiAtAlpha = new double[1000];
  final double initialSlope;

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
    this.initialSlope = gradient.dotProduct(searchDirection);
    alpha[0] = initialStepLength;
    phiAtAlpha[0] = f.at(point.plus(searchDirection));
  }

  public final double computeAlpha() {
    if (phiAtAlpha[0] <= functionValue + c1 * initialSlope) {
      return 1.0;
    }
    alpha[1] = 0.5;
    phiAtAlpha[1] = f.at(point.plus(searchDirection.scaledBy(alpha[1])));
    if (phiAtAlpha[1] <= functionValue + c1 * alpha[1] * initialSlope &&
        f.gradientAt(point.plus(searchDirection.scaledBy(alpha[1]))).dotProduct(searchDirection)
        >= c2 * initialSlope) {
      return alpha[1];
    }
    int i = 2;
    double phiAlphaDot = 0.0;
    while (true) {
      alpha[i] = cubicInterpolation(alpha[i - 2], alpha[i -1 ], phiAtAlpha[i - 2], phiAtAlpha[i - 1]);
      phiAtAlpha[i] = f.at(point.plus(searchDirection.scaledBy(alpha[i])));
      if (phiAtAlpha[i] > functionValue + c1 * alpha[i] * initialSlope ||
          phiAtAlpha[i] >= phiAtAlpha[i - 1]) {
        return zoom(alpha[i - 1], alpha[i]);
      }
      phiAlphaDot = f.gradientAt(point.plus(searchDirection.scaledBy(alpha[i]))).dotProduct(searchDirection);
      if (Math.abs(phiAlphaDot) <= c2 * initialSlope) {
        return alpha[i];
      }
      if (phiAlphaDot >= 0) {
        return zoom(alpha[i], alpha[i - 1]);
      }
    }

  }

  private final double cubicInterpolation(final double alpha0, final double alpha1, final double phiAlpha0,
      final double phiAlpha1) {
    final double alpha0squared = alpha0 * alpha0;
    final double alpha1squared = alpha1 * alpha1;
    final double alpha0cubed = alpha0squared * alpha0;
    final double alpha1cubed = alpha1squared * alpha1;
    final double scaleFactor = 1 / (alpha0squared * alpha1squared * (alpha1 - alpha0));
    final double a = scaleFactor * (alpha0squared * (phiAlpha1 - functionValue - initialSlope * alpha1)
        + -alpha1squared * (phiAlpha0 - functionValue - initialSlope * alpha0));
    final double b = scaleFactor * (-alpha0cubed * (phiAlpha1 - functionValue - initialSlope * alpha1)
        + alpha1cubed * (phiAlpha0 - functionValue - initialSlope * alpha0));
    return (-b + Math.sqrt(b * b - 3 * a * initialSlope) / (3 * a));
  }

  private final double quadraticInterpolation() {
    return -0.5 * (initialSlope / (2 * (phiAtAlpha[0] - functionValue - initialSlope)));
  }

  private final double zoom(double alphaLo, double alphaHi) {
    final int maxIter = 1000;
    int i = 0;
    while (true) {
      double phiAlphaLo = f.at(point.plus(searchDirection.scaledBy(alphaLo)));
      double phiAlphaHi = f.at(point.plus(searchDirection.scaledBy(alphaHi)));
      
      final double alphaJ = cubicInterpolation(alphaLo, alphaHi, phiAlphaLo, phiAlphaHi);
      final double phiAlphaJ = f.at(point.plus(searchDirection.scaledBy(alphaJ)));
      if (phiAlphaJ > (functionValue + c1 * alphaJ * initialSlope) || phiAlphaJ >= phiAlphaLo) {
        alphaHi = alphaJ;
      } else {
        final double alphaJDot = f.gradientAt(point.plus(searchDirection.scaledBy(alphaJ))).dotProduct(searchDirection);
        if (Math.abs(alphaJDot) <= -c2 * initialSlope) {
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
