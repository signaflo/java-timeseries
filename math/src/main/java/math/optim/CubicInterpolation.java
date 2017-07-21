/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.optim;

/**
 * A class for performing cubic polynomial interpolation of a function.
 *
 * @author Jacob Rachiele
 */
final class CubicInterpolation {

    private final double x1;
    private final double x2;
    private final double dydx1;
    private final double dydx2;
    private final double z;
    private final double w;

    public CubicInterpolation(final double x1, final double x2, final double y1, final double y2, final double dydx1,
                              final double dydx2) {
        validate(x1, x2);
        double y21;
        double y11;
        if (x1 > x2) {
            this.x1 = x2;
            y11 = y2;
            this.dydx1 = dydx2;
            this.x2 = x1;
            y21 = y1;
            this.dydx2 = dydx1;
        } else {
            this.x1 = x1;
            this.x2 = x2;
            y11 = y1;
            y21 = y2;
            this.dydx1 = dydx1;
            this.dydx2 = dydx2;
        }
        double s = 3 * ((y21 - y11) / (this.x2 - this.x1));
        this.z = s - this.dydx1 - this.dydx2;
        this.w = Math.sqrt(z * z - this.dydx1 * this.dydx2);
    }

    private static void validate(final double x1, final double x2) {
        if (x1 == x2) {
            throw new RuntimeException("The two x values cannot be the same. x1 and x2 were both equal to: " + x1);
        }
    }

    static double minimum(final double x1, final double x2, final double y1, final double y2, final double dydx1,
                          final double dydx2) {
        validate(x1, x2);
        final double d1 = dydx2 + dydx1 - 3 * ((y2 - y1) / (x2 - x1));
        final double d2 = Math.signum(x1 - x2) * Math.sqrt(d1 * d1 - dydx2 * dydx1);
        return x1 - (x1 - x2) * ((dydx1 + d2 - d1) / (dydx1 - dydx2 + 2 * d2));
    }

    final double minimum() {
        return x1 + (x2 - x1) * ((w - dydx1 - z) / (dydx2 - dydx1 + 2 * w));
    }
}
