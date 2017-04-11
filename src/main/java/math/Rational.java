/*
 * Copyright (c) 2017 Jacob Rachiele
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

package math;

public class Rational implements FieldElement<Rational> {

    private final int p;
    private final int q;

    public Rational(final int p, final int q) {
        if (q == 0) {
            throw new IllegalArgumentException("The denominator cannot be zero.");
        }
        this.p = p;
        this.q = q;
    }

    public Rational(final int p) {
        this(p, 1);
    }

    @Override
    public Rational plus(Rational other) {
        return null;
    }

    @Override
    public Rational minus(Rational other) {
        return null;
    }

    @Override
    public Rational times(Rational other) {
        return null;
    }

    @Override
    public Rational sqrt() {
        return null;
    }

    @Override
    public Rational conjugate() {
        return null;
    }

    @Override
    public Rational additiveInverse() {
        return new Rational(-p, q);
    }

    @Override
    public double abs() {
        return 0;
    }

    @Override
    public Rational dividedBy(double value) {
        return null;
    }

    int p() {
        return this.p;
    }

    int q() {
        return this.q;
    }

    @Override
    public String toString() {
        if (this.p == 0) {
            return "0";
        }
        if (this.q == 1) {
            return Integer.toString(this.p);
        }
        if ((double)this.p / this.q == 1.0) {
            return "1";
        }
        if (this.p < 0) {
            if (this.q < 0) {
                return -this.p + "/" + -this.q;
            }
            return this.p + "/" + this.q;
        }

        if (this.q < 0) {
            return -this.p + "/" + -this.q;
        }
        return this.p + "/" + this.q;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Rational rational = (Rational) o;

        if (p != rational.p) return false;
        return q == rational.q;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + p;
        result = 31 * result + q;
        return result;
    }

//    Rational(String rational) {
//        if (!isRational(rational)) {
//            throw new IllegalArgumentException();
//        }
//    }
//
//    private boolean isRational(String rational) {
//
//    }
}
