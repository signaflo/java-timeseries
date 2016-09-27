package optim.doubles;

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
    this.x1 = x1;
    this.x2 = x2;
    this.y1 = y1;
    this.y2 = y2;
    this.dydx = dydx;
    this.a = computeA();
    this.b = computeB();
  }
  
  final double computeA() {
    return -(y1 - y2 - dydx*(x1 - x2)) / Math.pow(x1 - x2, 2);
  }

  final double computeB() {
    return dydx - 2 * x1 * a;
  }

  final double minimum() {
    return -b / (2*a);
  }
  
  static final double secantFormulaMinimum(final double x1, final double x2,
          final double dydx1, final double dydx2) {
    return x1 - dydx1 * ((x1 - x2) / (dydx1 - dydx2));
  }
  
  static final double threePointMinimum(final double x1, final double x2, final double x3,
          final double y1, final double y2, final double y3) {
    final double top = (y1 - y2) * (x2 - x3) * (x3 - x1);
    final double bottom = y1 * (x2 - x3) + y2 * (x3 - x1) + y3 * (x1 - x2);
    return 0.5 * (x1 + x2 + (top/bottom));
  }
}
