package optim.doubles;

import optim.AbstractFunction;

public final class TestFunction3 extends AbstractFunction {

  @Override
  public final double at(final double alpha) {
    functionEvaluations++;
    final double beta = 0.01;
    final double phi0;
    if (alpha <= 1 - beta) {
      phi0 = 1 - alpha;
    } else if (alpha >= 1 + beta) {
      phi0 = alpha - 1;
    } else {
      phi0 = (0.5/beta) * Math.pow(alpha - 1, 2) + 0.5 * beta;
    }
    return phi0 + ((0.98)/(39*Math.PI)) * Math.sin(19.5 * Math.PI * alpha);
  }
  
  @Override
  public final double slopeAt(final double alpha) {
    slopeEvaluations++;
    final double beta = 0.01;
    final double phi0Grad;
    if (alpha <= 1 - beta) {
      phi0Grad = -1.0;
    } else if (alpha >= 1 + beta) {
      phi0Grad = 1.0;
    } else {
      phi0Grad = (alpha - 1) / beta;
    }
    return phi0Grad + 0.99 * Math.cos(19.5 * Math.PI * alpha);
  }

}
