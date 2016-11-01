/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

import linear.doubles.Vector;

/**
 * A function for the line search component of a quasi-Newton algorithm.
 * @author Jacob Rachiele
 *
 */
final class QuasiNewtonLineFunction extends AbstractFunction {
  
  private final AbstractMultivariateFunction f;
  private final Vector x;
  private final Vector p;
  
  /**
   * Construct a new line function for the quasi-Newton algorithm with the given function, 
   * point vector, and search direction.
   * @param f the function being optimized.
   * @param point the current input point.
   * @param searchDirection the current search direction.
   */
  QuasiNewtonLineFunction(final AbstractMultivariateFunction f, final Vector point,
      final Vector searchDirection) {
    this.f = f;
    this.x = point;
    this.p = searchDirection;
  }

  @Override
  public final double at(final double alpha) {
    functionEvaluations++;
    return f.at(x.plus(p.scaledBy(alpha))); 
  }

}
