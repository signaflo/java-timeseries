package optim.doubles;

import linear.doubles.Vector;
import optim.AbstractFunction;
import optim.AbstractMultivariateFunction;

final class QuasiNewtonLineFunction extends AbstractFunction {
  
  private final AbstractMultivariateFunction f;
  private final Vector x;
  private final Vector p;
  
  public QuasiNewtonLineFunction(final AbstractMultivariateFunction f, final Vector x, final Vector p) {
    this.f = f;
    this.x = x;
    this.p = p;
  }

  @Override
  public final double at(final double alpha) {
    functionEvaluations++;
    return f.at(x.plus(p.scaledBy(alpha))); 
  }

}
