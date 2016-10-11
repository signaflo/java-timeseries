package optim;

/**
 * A partial implementation of a scalar-valued function of one variable.
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
public abstract class AbstractFunction implements Function {

  protected int functionEvaluations = 0;
  protected int slopeEvaluations = 0;
  
  /**
   * Compute and the return the slope at the given point.
   * @param point an element of the function's domain.
   * @return the slope of the function at the given point.
   */
  public double slopeAt(final double point) {
    slopeEvaluations++;
    return NumericalDerivatives.centralDifferenceApproximation(this, point, 1E-4);
  }
  
  /**
   * The number of times this function has been evaluated.
   * @return the number of times this function has been evaluated.
   */
  public int functionEvaluations() {
    return this.functionEvaluations;
  }
  
  /**
   * The number of times the slope has been computed.
   * @return the number of times the slope has been computed.
   */
  public int slopeEvaluations() {
    return this.slopeEvaluations;
  }
}
