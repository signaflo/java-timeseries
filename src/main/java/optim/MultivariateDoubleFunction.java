package optim;

@FunctionalInterface
public interface MultivariateDoubleFunction {
  
  public double at(double... point);

}
