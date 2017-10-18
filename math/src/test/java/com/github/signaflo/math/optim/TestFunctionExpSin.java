/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package com.github.signaflo.math.optim;

import com.github.signaflo.math.function.AbstractFunction;

/**
 * @author Jacob Rachiele
 */
public class TestFunctionExpSin extends AbstractFunction {

  @Override
  public final double at(final double point) {
    functionEvaluations++;
    return -Math.exp(-point) * Math.sin(point);
  }
}
