/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */
package optim;

import math.function.QuadraticFunction;
import math.polynomial.interpolation.NewtonPolynomial;

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
            throw new IllegalArgumentException(
                    "The two x values cannot be the same. x1 and x2 were both equal to: " + x1);
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

    static double minimum(final double x1, final double x2, final double y1, final double y2, final double dydx) {
        if (x1 == x2) {
            throw new IllegalArgumentException(
                    "The two x values cannot be the same. x1 and x2 were both equal to: " + x1);
        }
        double w1, w2, z1, z2;
        if (x1 > x2) {
            w1 = x2;
            z1 = y2;
            w2 = x1;
            z2 = y1;
        } else {
            w1 = x1;
            w2 = x2;
            z1 = y1;
            z2 = y2;
        }
        double a = -(z1 - z2 - dydx * (w1 - w2)) / Math.pow(w1 - w2, 2);
        double b = dydx - 2 * w1 * a;
        return -b / (2 * a);
    }

    static double secantFormulaMinimum(final double x1, final double x2, final double dydx1, final double dydx2) {
        if (x1 <= x2) {
            return x1 - dydx1 * ((x1 - x2) / (dydx1 - dydx2));
        }
        return x2 - dydx2 * ((x2 - x1) / (dydx2 - dydx1));
    }

    // The input arguments must be ordered from least to greatest.
    static double threePointMinimum(final double x1, final double x2, final double x3, final double y1, final double y2,
                                    final double y3) {
        double[] point = new double[3];
        double[] value = new double[3];
        point[0] = x1;
        point[1] = x2;
        point[2] = x3;
        value[0] = y1;
        value[1] = y2;
        value[2] = y3;
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

    double computeA() {
        return -(y1 - y2 - dydx * (x1 - x2)) / Math.pow(x1 - x2, 2);
    }

    double computeB() {
        return dydx - 2 * x1 * a;
    }

    double minimum() {
        return -b / (2 * a);
    }

}
