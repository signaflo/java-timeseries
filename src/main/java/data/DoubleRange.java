/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package data;

import com.google.common.primitives.Doubles;
import stats.Statistics;

import java.util.List;

/**
 * Represents a range of doubles.
 *
 * @author Jacob Rachiele
 */
public final class DoubleRange {

    private final double[] range;

    private DoubleRange(final int from, final int to, final double by) {
        range = new double[Math.abs(to - from)];
        for (int i = 0; i < range.length; i++) {
            range[i] = from + i * by;
        }
    }

    /**
     * Create a new range of doubles excluding the given <i>to</i> value.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, not included in the result.
     * @return a new range of doubles excluding the given <i>to</i> value.
     */
    public static DoubleRange exclusiveRange(final int from, final int to) {
        return new DoubleRange(from, to, 1);
    }

    /**
     * Create a new range of doubles including the given <i>to</i> value.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, included in the result.
     * @return a new range of doubles including the given <i>to</i> value.
     */
    public static DoubleRange inclusiveRange(final int from, final int to) {
        return new DoubleRange(from, to + 1, 1);
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
     * Get the sum of the data in the range.
     *
     * @return the sum of the data in the range.
     */
    double sum() {
        return Statistics.sumOf(range);
    }
}
