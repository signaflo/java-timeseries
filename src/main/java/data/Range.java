/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package data;

import com.google.common.primitives.Doubles;
import lombok.EqualsAndHashCode;
import stats.Statistics;

import java.util.List;

/**
 * A range of doubles.
 *
 * @author Jacob Rachiele
 */
@EqualsAndHashCode
public final class Range {

    private final double[] range;
    private final double by;

    /**
     * Create a new range with the given starting value, ending value, and increment amount.
     *
     * @param from the starting value of the range.
     * @param to the ending value of the range.
     * @param by the increment amount.
     *
     * @throws IllegalArgumentException if the <em>by</em> argument is less than or equal to 0.
     */
    public Range(final double from, final double to, final double by) {
        if (by <= 0) {
            throw new IllegalArgumentException("The by argument must be positive.");
        }
        this.by = by;
        int size = (int)Math.abs((to - from) / by) + 1;
        range = new double[size];
        for (int i = 0; i < range.length; i++) {
            range[i] = from + Math.signum(to - from) * i * by;
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
    public static Range exclusiveRange(final double from, final double to, double by) {
        return new Range(from, to - Math.signum(to - from) * by, by);
    }

    /**
     * Create a new range of doubles including the given <i>to</i> value.
     *
     * @param from the starting value of the range.
     * @param to   the ending value of the range, included in the result.
     * @param by   the increment amount.
     * @return a new range of doubles including the given <i>to</i> value.
     */
    public static Range inclusiveRange(final double from, final double to, double by) {
        return new Range(from, to, by);
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
     * Get the sum of the data in this range.
     *
     * @return the sum of the data in this range.
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
     *
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
