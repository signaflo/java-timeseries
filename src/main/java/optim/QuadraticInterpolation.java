/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

import math.function.QuadraticFunction;
import math.polynomial.NewtonPolynomial;

final class QuadraticInterpolation {

  private final double x1;
  private final double x2;
  private final double y1;
  private final double y2;
  private final double dydx;
  private final double a;
  private final double b;

  QuadraticInterpolation(final double x1, final double x2, final double y1, final double y2, final double dydx) {
    if (x1 == x2) {
      throw new IllegalArgumentException("The two x values cannot be the same. x1 and x2 were both equal to: " + x1);
    }
    if (x1 > x2) {
      this.x1 = x2;
      this.y1 = y2;
      this.x2 = x1;
      this.y2 = y1;
    } else {
      this.x1 = x1;
      this.x2 = x2;
      this.y1 = y1;
      this.y2 = y2;
    }
    this.dydx = dydx;
    this.a = computeA();
    this.b = computeB();
  }

  final double computeA() {
    return -(y1 - y2 - dydx * (x1 - x2)) / Math.pow(x1 - x2, 2);
  }

  final double computeB() {
    return dydx - 2 * x1 * a;
  }

  final double minimum() {
    return -b / (2 * a);
  }

  static double secantFormulaMinimum(final double x1, final double x2, final double dydx1, final double dydx2) {
    if (x1 <= x2) {
      return x1 - dydx1 * ((x1 - x2) / (dydx1 - dydx2));
    }
    return x2 - dydx2 * ((x2 - x1) / (dydx2 - dydx1));
  }

  // The input arguments must be ordered from least to greatest.
  static double threePointMinimum(final double x1, final double x2, final double x3, final double y1,
      final double y2, final double y3) {
    double[] point = new double[3];
    double[] value = new double[3];
    point[0] = x1; point[1] = x2; point[2] = x3;
    value[0] = y1; value[1] = y2; value[2] = y3;
    NewtonPolynomial np = new NewtonPolynomial(point, value);
    QuadraticFunction f = np.toQuadratic();
    if (f.hasMinimum()) {
      return f.extremePointDbl();
    }
    throw new IllegalStateException("The interpolating quadratic, " + f + ", had no minimum value.");
//    final double top = (x1 - x2) * (x2 - x3) * (x3 - x1);
//    final double bottom = y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2);
//    return 0.5 * (x1 + x2 + (top / bottom));
  }
  
}
