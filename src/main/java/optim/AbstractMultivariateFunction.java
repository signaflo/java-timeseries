/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package optim;

import linear.doubles.Vector;

/**
 * A partial implementation of a scalar-valued function of several variables.
 *
 */
public abstract class AbstractMultivariateFunction implements MultivariateFunction {
  
  protected int functionEvaluations = 0;
  protected int gradientEvalutations = 0;
  private static final double gradientTolerance = 1E-4;
  
  public Vector gradientAt(Vector point) {
    gradientEvalutations++;
    return NumericalDerivatives.centralDifferenceGradient(this, point, gradientTolerance);
  }
  
  public Vector gradientAt(final Vector point, final double functionValue) {
    gradientEvalutations++;
    return NumericalDerivatives.forwardDifferenceGradient(this, point, gradientTolerance, functionValue);
  }
  
  /**
   * The number of times this function has been evaluated.
   * @return the number of times this function has been evaluated.
   */
  public int functionEvaluations() {
    return this.functionEvaluations;
  }
  
  /**
   * The number of times the gradient has been computed.
   * @return the number of times the gradient has been computed.
   */
  public int gradientEvaluations() {
    return this.gradientEvalutations;
  }

}
