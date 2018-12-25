package com.github.signaflo.timeseries.model;

/**
 * Represents an unknown property of a process or model. A parameter is a coefficient that has yet
 * to be set or fully estimated. The core difference between a parameter and a coefficient is that a
 * parameter may change as it goes through an estimation process, whereas a coefficient should be
 * fixed and immutable. This implies that a parameter's {@link this#getUncertainty()} score should
 * never be zero.
 */
public interface Parameter extends Coefficient {

  /**
   * Get the numeric value of the parameter.
   *
   * @return The numeric value of the parameter.
   */
  @Override
  double getValue();

  /**
   * The degree to which the parameter is currently unknown. This method should return
   * {@link Double#POSITIVE_INFINITY} if nothing is known about the value of the parameter.
   *
   * @return The uncertainty associated with this coefficient.
   *
   * @throws ArithmeticException if the uncertainty score is less than or equal to 0. A parameter's
   *                             uncertainty score should not be zero, since this would make it
   *                             strictly a coefficient.
   */
  @Override
  double getUncertainty();

}
