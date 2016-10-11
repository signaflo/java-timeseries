package optim;

/**
 * A scalar-valued function of one variable.
 * @author Jacob Rachiele
 *
 */
@FunctionalInterface
public interface Function {

  /**
   * Compute and return the value of the function at the given point.
   * @param point the point at which to evaluate the function.
   * @return the value of the function at the given point.
   */
  public double at(double point);
}
