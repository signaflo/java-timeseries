package com.github.signaflo.timeseries.model;

/**
 * A numeric property of a process or model. Implementing classes should be immutable -- once a
 * coefficient is instantiated it should never change. Mutable properties of processes or models
 * should use the {@link Parameter} concept.
 */
public interface Coefficient {

  /**
   * Get the numeric value of the coefficient.
   *
   * @return The numeric value of the coefficient.
   */
  double getValue();

  /**
   * The uncertainty is the degree to which an estimated coefficient is unknown. Implementing
   * classes may report this as a standard deviation, a variance, or with some other
   * uncertainty score.
   *
   * <p>
   *   The uncertainty should return 0 if the coefficient is pre-determined. It should return
   *   a positive number if the coefficient is estimated.
   * </p>
   *
   * @return The uncertainty associated with this coefficient.
   *
   * @throws ArithmeticException if the uncertainty is less than zero.
   */
  double getUncertainty();


}
