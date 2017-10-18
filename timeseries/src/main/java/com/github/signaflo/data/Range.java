/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package com.github.signaflo.data;

import com.google.common.primitives.Doubles;
import lombok.EqualsAndHashCode;
import com.github.signaflo.math.stats.Statistics;

import java.util.List;

/**
 * A range of doubles.
 *
 * @author Jacob Rachiele
 */
@EqualsAndHashCode
public final class Range {

    private static final double TOLERANCE = Math.sqrt(Math.ulp(1.0));
    private final double[] range;
    private final double by;

    /**
     * Create a new range with the given starting value, ending value, and increment amount.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range.
     * @param by   the increment amount.
     * @throws IllegalArgumentException if the <em>by</em> argument is less than or equal to 0.
     */
    Range(final double from, final double to, final double by) {
        if (by <= 0) {
            throw new IllegalArgumentException("The by argument must be positive.");
        }
        this.by = by;
        int size = (int) (Math.abs((to - from) / by) + TOLERANCE) + 1;
        range = new double[size];
        if (size > 0) {
            range[0] = from;
        }
        for (int i = 1; i < range.length; i++) {
            range[i] = range[i - 1] + Math.signum(to - from) * by;
        }
    }

    /**
     * Create a new range of doubles excluding the given <i>to</i> value.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, not included in the result.
     * @param by   the increment amount.
     * @return a new range of doubles excluding the given <i>to</i> value.
     */
    public static Range exclusiveRange(final double from, final double to, final double by) {
        double dist = to - from;
        double ratio = dist / by;
        double offset = (Math.abs(ratio - (int) ratio) < TOLERANCE) ? Math.signum(dist) * by : 0.0;
        return new Range(from, to - offset, by);
    }

    /**
     * Create a new range of doubles including the given <i>to</i> value if possible.
     * Note that the given <i>to</i> value will be included only if (<i>to - from</i>)
     * is an integer multiple of <i>by</i>.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, included in the result if possible.
     * @param by   the increment amount.
     * @return a new range of doubles including the given <i>to</i> value if possible.
     */
    public static Range inclusiveRange(final double from, final double to, final double by) {
        return new Range(from, to, by);
    }

    /**
     * Create a new range of doubles excluding the given <i>to</i> value, with a default spacing between
     * values of 1.0.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, not included in the result.
     * @return a new range of doubles excluding the given <i>to</i> value.
     */
    public static Range exclusiveRange(final double from, final double to) {
        return exclusiveRange(from, to, 1.0);
    }

    /**
     * Create a new range of doubles including the given <i>to</i> value if possible, with a default spacing
     * between values of 1.0. Note that the given <i>to</i> value will be included only if (<i>to - from</i>)
     * is an integer multiple of <i>1</i>.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, included in the result if possible.
     * @return new range of doubles including the given <i>to</i> value if possible.
     */
    public static Range inclusiveRange(final double from, final double to) {
        return inclusiveRange(from, to, 1.0);
    }

    /**
     * Get the range as an array of doubles.
     *
     * @return the range as an array of doubles.
     */
    public double[] asArray() {
        return this.range.clone();
    }

    public List<Double> asList() {
        return Doubles.asList(this.range.clone());
    }

    /**
     * Get the sum of the com.github.signaflo.data in this range.
     *
     * @return the sum of the com.github.signaflo.data in this range.
     */
    public double sum() {
        return Statistics.sumOf(range);
    }

    /**
     * Get the size, or number of elements, of this range.
     *
     * @return the size of this range.
     */
    public int size() {
        return this.range.length;
    }

    /**
     * Get the starting value of this range.
     *
     * @return the starting value of this range.
     */
    public double start() {
        return this.range[0];
    }

    /**
     * Get the ending value of this range.
     *
     * @return the ending value of this range.
     */
    public double end() {
        return this.range[this.range.length - 1];
    }

    /**
     * Return the ith element of this range, where indexing begins at 0.
     *
     * @param i the index of the range value to retrieve.
     * @return the ith element of this range.
     */
    public double at(int i) {
        return this.range[i];
    }

    @Override
    public String toString() {
        return String.format("Range: %s to %s by %s", Double.toString(range[0]),
                             Double.toString(range[range.length - 1]), Double.toString(by));
    }
}
