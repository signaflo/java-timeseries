package optim;

/**
 * A scalar-valued function of several variables that uses primitive doubles as input and output values.
 * @author Jacob Rachiele
 *
 */
@FunctionalInterface
public interface MultivariateDoubleFunction {
  
  /**
   * Compute and return the value of the function at the given point.
   * @param point the point at which to evaluate the function.
   * @return the value of the function at the given point.
   */
  public double at(double... point);

}
