package optim.doubles;

final class CubicInterpolation {
  
  private final double x1;
  private final double x2;
  private final double dydx1;
  private final double dydx2;
  private final double s;
  private final double z;
  private final double w;
  
  public CubicInterpolation(final double x1, final double x2, final double y1,
          final double y2, final double dydx1, final double dydx2) {
    validate(x1, x2, dydx1, dydx2);
    this.x1 = x1;
    this.x2 = x2;
    this.dydx1 = dydx1;
    this.dydx2 = dydx2;
    this.s = 3 * ((y2 - y1) / (x2 - x1));
    this.z = s - dydx1 - dydx2;
    this.w = Math.sqrt(z * z - dydx1 * dydx2);
  }

  final double minimum() {
    return x1 + (x2 - x1) * ((w - dydx1 - z) / (dydx2 - dydx1 + 2 * w));
  }
  
  private final void validate(final double x1, final double x2, final double dydx1, final double dydx2) {
    if (x1 == x2) {
      throw new RuntimeException("The two x values cannot be the same. x1 and x2 were both equal to: " + x1);
    }
    if (dydx2 <= 0) {
      throw new RuntimeException("The slope at x2 must be positive. It was: " + dydx2);
    }
    if (dydx1 >= 0) {
      throw new RuntimeException("The slope at x1 must be negative. It was: " + dydx1);
    }
  }
}
