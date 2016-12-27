package math.function;

/**
 * Created by jacob on 12/27/16.
 */
@FunctionalInterface
public interface SlopeFunction {

  double at(double point, double functionValue);
}
