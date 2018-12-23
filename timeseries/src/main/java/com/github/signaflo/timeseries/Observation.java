package com.github.signaflo.timeseries;

import lombok.NonNull;

import java.time.Instant;

/**
 * An observation of a numerical event.
 *
 * @author Jacob Rachiele
 * Nov. 12, 2017
 */
public class Observation {

  private final double value;
  private final Instant observationPeriod;

  /**
   * Create a new observation with the value observed and the observation period.
   *
   * @param value             the value observed.
   * @param observationPeriod the period in which the value was observed.
   */
  public Observation(double value, @NonNull Instant observationPeriod) {
    this.value = value;
    this.observationPeriod = observationPeriod;
  }

  public double getValue() {
    return this.value;
  }

  public Instant getObservationPeriod() {
    return this.observationPeriod;
  }


}
