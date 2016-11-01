/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

import optim.AbstractFunction;

public final class TestFunction2 extends AbstractFunction{

  @Override
  public final double at(final double alpha) {
    functionEvaluations++;
    final double beta = 0.004;
    return Math.pow(alpha + beta, 5) - 2 * Math.pow(alpha + beta, 4);
  }
  
  @Override
  public final double slopeAt(final double alpha) {
    slopeEvaluations++;
    return 5 * (-1.596 + alpha) * Math.pow(alpha + 0.004, 3);
  }

}
