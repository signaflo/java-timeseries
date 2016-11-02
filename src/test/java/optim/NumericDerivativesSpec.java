package optim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import math.function.Function;
import math.function.MultivariateDoubleFunction;
import math.function.MultivariateFunction;
import org.junit.Test;

import linear.doubles.Vector;

public class NumericDerivativesSpec {

  @Test
  public void whenForwardDerivCalculatedApproximationClose() {
    Function f = (x) -> x * x * x;
    final double h = 1E-4;
    final double point = 4.0;
    final double forwardDeriv = NumericalDerivatives.forwardDifferenceApproximation(f, point, h);
    assertThat(forwardDeriv, is(closeTo(48.0, 1E-2)));
  }
  
  @Test
  public void whenCentralDerivCalculatedApproximationClose() {
    Function f = (x) -> x * x * x;
    final double h = 1E-4;
    final double point = 4.0;
    final double centralDeriv = NumericalDerivatives.centralDifferenceApproximation(f, point, h);
    assertThat(centralDeriv, is(closeTo(48.0, 1E-8)));
  }
  
  @Test
  public void whenForwardGradientCalculatedApproximationClose() {
    MultivariateDoubleFunction f = (point) -> point[0] * point[0] + point[1] * point[1];
    final double h = 1E-4;
    final double[] point = new double[] {3, 4};
    final double[] forwardGradient = NumericalDerivatives.forwardDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, forwardGradient, 1E-4);
  }
  
  @Test
  public void whenCentralGradientCalculatedApproximationClose() {
    MultivariateDoubleFunction f = (point) -> point[0] * point[0] + point[1] * point[1];
    final double h = 1E-4;
    final double[] point = new double[] {3, 4};
    final double[] centralGradient= NumericalDerivatives.centralDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, centralGradient, 1E-10);
  }
  
  @Test
  public void whenForwardGradientVectorCalculatedApproximationClose() {
    MultivariateFunction f = (point) -> point.at(0) * point.at(0) + point.at(1) * point.at(1);
    final double h = 1E-4;
    final Vector point = Vector.newVector(3.0, 4.0);
    final Vector forwardGradient = NumericalDerivatives.forwardDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, forwardGradient.elements(), 1E-4);
  }
  
  @Test
  public void whenCentralGradientVectorCalculatedApproximationClose() {
    MultivariateFunction f = (point) -> point.at(0) * point.at(0) + point.at(1) * point.at(1);
    final double h = 1E-4;
    final Vector point = Vector.newVector(3.0, 4.0);
    final Vector centralGradient= NumericalDerivatives.centralDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, centralGradient.elements(), 1E-10);
  } 
}
