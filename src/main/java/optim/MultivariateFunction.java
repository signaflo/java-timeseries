package optim;

import linear.doubles.Vector;

@FunctionalInterface
public interface MultivariateFunction {
  
  double at(Vector point);

}
