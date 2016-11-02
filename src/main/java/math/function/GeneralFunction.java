/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

/**
 * A thin wrapper for a {@link Function f}. Pass a lambda expression representing f to the constructor
 * to quickly obtain a concrete {@link AbstractFunction} instead of writing a new class that extends AbstractFunction.
 * @author Jacob Rachiele
 */
public class GeneralFunction extends AbstractFunction {

  private final Function f;

  public GeneralFunction(final Function f) {
    this.f = f;
  }

  @Override
  public double at(double point) {
    functionEvaluations++;
    return f.at(point);
  }
}
