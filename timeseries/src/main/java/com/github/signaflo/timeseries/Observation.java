package com.github.signaflo.timeseries;

import lombok.NonNull;

import java.time.OffsetDateTime;

/**
 * An observation of a numerical event.
 *
 * @author Jacob Rachiele
 * Nov. 12, 2017
 */
public class Observation<T extends Number> {

    private final T value;
    private final OffsetDateTime observationPeriod;

    /**
     * Create a new observation with the value observed and the observation period.
     *
     * @param value the value observed.
     * @param observationPeriod the period in which the value was observed.
     */
    public Observation(T value, @NonNull OffsetDateTime observationPeriod) {
        this.value = value;
        this.observationPeriod = observationPeriod;
    }

    public T getValue() {
        return this.value;
    }

    public OffsetDateTime getObservationPeriod() {
        return this.observationPeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Observation that = (Observation) o;

        if (!value.equals(that.value)) return false;
        return observationPeriod.equals(that.observationPeriod);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = value.hashCode();
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + observationPeriod.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return value.toString() + " at " + this.observationPeriod.toString();
    }
}
