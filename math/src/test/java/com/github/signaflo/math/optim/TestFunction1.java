/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package com.github.signaflo.math.optim;

import com.github.signaflo.math.function.AbstractFunction;

public class TestFunction1 extends AbstractFunction {

  @Override
  public final double at(final double x) {
    functionEvaluations++;
    return -x / (x * x + 2.0);
  }
  
  @Override
  public final double slopeAt(final double x) {
    slopeEvaluations++;
    return (x * x - 2) / Math.pow(x * x + 2, 2);
  }

}
