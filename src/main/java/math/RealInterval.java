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

/**
 * An interval on the real line.
 */
public final class RealInterval implements Interval<Real> {

    private static final double EPSILON = Math.ulp(1.0);

    private final Real lower;
    private final Real upper;

    RealInterval(final Real lower, final Real upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public RealInterval(final double lower, final double upper) {
        this.lower = Real.from(lower);
        this.upper = Real.from(upper);
    }

    public double lowerDbl() {
        return this.lower.value();
    }

    public double upperDbl() {
        return this.upper.value();
    }

    @Override
    public Real lower() {
        return this.lower;
    }

    @Override
    public Real upper() {
        return this.upper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RealInterval interval = (RealInterval) o;

        if (!lower.equals(interval.lower)) return false;
        return upper.equals(interval.upper);
    }

    @Override
    public int hashCode() {
        int result = lower.hashCode();
        result = 31 * result + upper.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "(" + Double.toString(this.lower.value()) + ", " + Double.toString(this.upper.value()) + ")";
    }
}
