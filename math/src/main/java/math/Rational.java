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

import lombok.NonNull;

/**
 * A representation of a rational number. This class is immutable and thread-safe.
 */
public final class Rational implements FieldElement<Rational> {

    private final int p;
    private final int q;

    public static Rational from(int p, int q) {
        return new Rational(p, q);
    }

    public static Rational from(int p) {
        return new Rational(p, 1);
    }

    private Rational(int p, int q) {
        if (q == 0) {
            throw new IllegalArgumentException("The denominator cannot be zero.");
        }
        this.p = p;
        this.q = q;
    }

    @Override
    public Rational plus(Rational other) {
        return new Rational(this.p * other.q + other.p * this.q, this.q * other.q);
    }

    @Override
    public Rational minus(Rational other) {
        return new Rational(this.p * other.q - other.p * this.q, this.q * other.q);
    }

    @Override
    public Rational times(Rational other) {
        checkNonZero(other.q);
        return new Rational(this.p * other.p, this.q * other.q);
    }

    @Override
    public Rational sqrt() {
        double top = Math.sqrt(this.p);
        if (Math.abs(top - (int)top) > 0.0 || !Double.isFinite(top)) {
            throw new IllegalStateException("The square root of the rational number is not rational.");
        }
        double bottom = Math.sqrt(this.q);
        if (Math.abs(bottom - (int)bottom) > 0.0 || !Double.isFinite(bottom)) {
            throw new IllegalStateException("The square root of the rational number is not rational.");
        }
        return new Rational((int)top, (int)bottom);
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public <N extends FieldElement<N>> N sqrt(Class<N> clazz) {
//        if (clazz.equals(Rational.class)) {
//            return (N)this.sqrt();
//        } else if (clazz.equals(Real.class)) {
//            return (N)this.realSqrt();
//        } else if (clazz.equals(Complex.class)){
//            return (N)this.complexSqrt();
//        }
//        throw new IllegalStateException("No square root of the given class type could be computed.");
//    }

    @Override
    public Complex complexSqrt() {
        return new Complex(this.p / (double) this.q).complexSqrt();
    }

    @Override
    public Rational conjugate() {
        return this;
    }

    @Override
    public Rational additiveInverse() {
        return new Rational(-p, q);
    }

    @Override
    public double abs() {
        return Math.abs((double)p / q);
    }

    @Override
    public Rational dividedBy(Rational value) {
        checkNonZero(value.p);
        return new Rational(this.p * value.q, this.q * value.p);
    }

    private void checkNonZero(int value) {
        if (value == 0) {
            throw new ArithmeticException("Attempt to divide a rational number by zero.");
        }
    }

    @Override
    public int compareTo(@NonNull Rational other) {
        double thisVal = Real.from((double)this.p / this.q).asDouble();
        double otherVal = Real.from((double)other.p / other.q).asDouble();
        return Double.compare(thisVal, otherVal);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Rational: ");
        if (this.p == 0) {
            return sb.append("0").toString();
        }
        if (this.q == 1) {
            return sb.append(Integer.toString(this.p)).toString();
        }
        if ((double)this.p / this.q == 1.0) {
            return sb.append("1").toString();
        }
        if (this.p < 0) {
            if (this.q < 0) {
                return sb.append(-this.p).append("/").append(-this.q).toString();
            }
            return sb.append(this.p).append("/").append(this.q).toString();
        }

        if (this.q < 0) {
            return sb.append(-this.p).append("/").append(-this.q).toString();
        }
        return sb.append(this.p).append("/").append(this.q).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rational rational = (Rational) o;

        Real real = Real.from((double)this.p / this.q);
        Real otherReal = Real.from((double)rational.p / rational.q);
        return real.equals(otherReal);
    }

    @Override
    public int hashCode() {
        Real real = Real.from((double)this.p / this.q);
        int result = real.hashCode();
        return result * 31;
    }
}
