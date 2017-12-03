package com.github.signaflo.timeseries.model.arima;

import com.github.signaflo.math.NA;

import java.util.Objects;
import java.util.OptionalDouble;

/**
 * Represents a model coefficient.
 *
 * @author Jacob Rachiele
 * Nov. 19, 2017
 */
public final class Coefficient {

    private final double value;
    private final Number stdError;

    private Coefficient(Builder builder) {
        this.value = builder.value;
        this.stdError = builder.stdError;
    }

    /**
     * Get a new builder for a coefficient.
     *
     * @param value the value of the coefficient.
     * @return a new coefficient builder with the given value set.
     */
    public static Builder builder(double value) {
        return new Builder(value);
    }

    /**
     * Get the value of this coefficient.
     *
     * @return the value of this coefficient.
     */
    public double value() {
        return this.value;
    }

    /**
     * Get the standard error of this coefficient, which may be missing.
     *
     * @return the standard error of this coefficient if it exists, otherwise, an empty OptionalDouble.
     */
    public OptionalDouble standardError() {
        if (Objects.equals(stdError.doubleValue(), NA.missingDouble().doubleValue())) {
            return OptionalDouble.empty();
        }
        return OptionalDouble.of(this.stdError.doubleValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Coefficient that = (Coefficient) o;

        if (Double.compare(that.value, value) != 0) return false;
        return stdError.equals(that.stdError);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + stdError.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Coefficient (SE): " + value + " (" + stdError + ")";
    }

    public static class Builder {
        private final double value;
        private Number stdError = NA.missingDouble();

        private Builder(double value) {
            this.value = value;
        }

        public Builder setStandardError(double stdError) {
            this.stdError = stdError;
            return this;
        }

        public Coefficient build() {
            return new Coefficient(this);
        }
    }
}
