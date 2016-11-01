/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

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
      throw new RuntimeException("The two x values cannot be the same. x1 and x2 were both equal to: " + x1);
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

  static final double secantFormulaMinimum(final double x1, final double x2, final double dydx1, final double dydx2) {
    if (x1 <= x2) {
      return x1 - dydx1 * ((x1 - x2) / (dydx1 - dydx2));
    }
    return x2 - dydx2 * ((x2 - x1) / (dydx2 - dydx1));
  }

  static final double threePointMinimum(final double x1, final double x2, final double x3, final double y1,
      final double y2, final double y3) {
    final double a1;
    final double a2;
    final double a3;
    final double b1;
    final double b2;
    final double b3;
    
    if (x1 <= x2) {
      if (x1 <= x3) {
        if (x2 <= x3) {
          a1 = x1;
          a2 = x2;
          a3 = x3;
          b1 = y1;
          b2 = y2;
          b3 = y3;
        }
        else {
          a1 = x1;
          a2 = x3;
          a3 = x2;
          b1 = y1;
          b2 = y3;
          b3 = y2;
        }
      }
      else {
        a1 = x3;
        a2 = x1;
        a3 = x2;
        b1 = y3;
        b2 = y1;
        b3 = y2;
      }
    }
    else if (x2 <= x3) {
      if (x1 <= x3) {
        a1 = x2;
        a2 = x1;
        a3 = x3;
        b1 = y2;
        b2 = y1;
        b3 = y3;
      }
      else {
        a1 = x2;
        a2 = x3;
        a3 = x1;
        b1 = y2;
        b2 = y3;
        b3 = y1;
      }
    }
    else {
      a1 = x3;
      a2 = x2;
      a3 = x1;
      b1 = y3;
      b2 = y2;
      b3 = y1;
    }
    final double top = (b1 - b2) * (a2 - a3) * (a3 - a1);
    final double bottom = b1 * (a2 - a3) + b2 * (a3 - a1) + b3 * (a1 - a2);
    return 0.5 * (a1 + a2 + (top / bottom));
  }
  
}
