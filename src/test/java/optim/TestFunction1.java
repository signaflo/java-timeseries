/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

public class TestFunction1 extends AbstractFunction {

  @Override
  public final double at(final double point) {
    functionEvaluations++;
    return -point / (point * point + 2.0);
  }
  
  @Override
  public final double slopeAt(final double point) {
    slopeEvaluations++;
    return (point * point - 2) / Math.pow(point * point + 2, 2);
  }

}
