package optim;

import linear.doubles.Vector;
import math.function.Function;
import math.function.MultivariateDoubleFunction;
import math.function.MultivariateFunction;

import linear.doubles.Vector;

/**
 * Static methods for computing numerical derivatives.
 * @author Jacob Rachiele
 *
 */
public final class NumericalDerivatives {
  
  private NumericalDerivatives(){}
  
  public static double forwardDifferenceApproximation(final Function f, final double point, final double h) {
    return (f.at(point) - f.at(point - h)) / h;
  }
  
  public static double centralDifferenceApproximation(final Function f, final double point, final double h) {
    return (f.at(point + 0.5*h) - f.at(point - 0.5*h)) / h;
  }

  public static double forwardDifferenceApproximation(final Function f, final double point, final double h,
                                                      final double functionValue) {
    return (functionValue - f.at(point - h)) / h;
  }
  
  public static double[] forwardDifferenceGradient(final MultivariateDoubleFunction f, final double[] point,
                                                   final double h) {
    double[] newPoints = point.clone();
    final double[] partials = new double[point.length];
    final double functionValue = f.at(point);
    for (int i = 0; i < partials.length; i++) {
      newPoints[i] = point[i] + h;
      partials[i] = (f.at(newPoints) - functionValue) / h;
      newPoints = point.clone();
    }
    return partials;
  }
  
  public static double[] centralDifferenceGradient(final MultivariateDoubleFunction f, final double[] point,
                                                   final double h) {
    double[] forwardPoints = point.clone();
    double[] backwardPoints = point.clone();
    final double[] partials = new double[point.length];
    for (int i = 0; i < partials.length; i++) {
      forwardPoints[i] = point[i] + 0.5*h;
      backwardPoints[i] = point[i] - 0.5*h;
      partials[i] = (f.at(forwardPoints) - f.at(backwardPoints)) / h;
      forwardPoints = point.clone();
      backwardPoints = point.clone();
    }
    return partials;
  }
  
  public static Vector forwardDifferenceGradient(final MultivariateFunction f, final Vector point,
                                                 final double h) {
    double[] newPoints = point.elements().clone();
    final double[] partials = new double[point.size()];
    final double functionValue = f.at(point);
    for (int i = 0; i < partials.length; i++) {
      newPoints[i] = point.at(i) + h;
      partials[i] = (f.at(Vector.from(newPoints)) - functionValue) / h;
      newPoints = point.elements().clone();
    }
    return Vector.from(partials);
  }
  
  public static Vector forwardDifferenceGradient(final MultivariateFunction f, final Vector point,
                                                 final double h, final double functionValue) {
    double[] newPoints = point.elements().clone();
    final double[] partials = new double[point.size()];
    for (int i = 0; i < partials.length; i++) {
      newPoints[i] = point.at(i) + h;
      partials[i] = (f.at(Vector.from(newPoints)) - functionValue) / h;
      newPoints = point.elements().clone();
    }
    return Vector.from(partials);
  }
  
  public static Vector centralDifferenceGradient(final MultivariateFunction f, final Vector point,
                                                 final double h) {
    double[] forwardPoints = point.elements().clone();
    double[] backwardPoints = point.elements().clone();
    final double[] partials = new double[point.size()];
    for (int i = 0; i < partials.length; i++) {
      forwardPoints[i] = point.at(i) + h;
      backwardPoints[i] = point.at(i) - h;
      partials[i] = (f.at(Vector.from(forwardPoints)) - f.at(Vector.from(backwardPoints))) / (2 * h);
      forwardPoints = point.elements().clone();
      backwardPoints = point.elements().clone();
    }
    return Vector.from(partials);
  }
}
