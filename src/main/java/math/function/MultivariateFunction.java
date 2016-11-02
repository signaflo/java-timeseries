/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import linear.doubles.Vector;

/**
 * A scalar-valued function of several variables.
 * @author Jacob Rachiele
 *
 */
@FunctionalInterface
public interface MultivariateFunction {
  
  /**
   * Compute and return the value of the function at the given point.
   * @param point the point at which to evaluate the function.
   * @return the value of the function at the given point.
   */
  double at(Vector point);

}
