package optim;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

public class NumericDifferentiationSpec {

  @Test
  public void whenForwardDerivCalculatedApproximationClose() {
    Function f = (x) -> x * x * x;
    final double h = 1E-4;
    final double point = 4.0;
    final double forwardDeriv = NumericalDerivatives.forwardDifferenceApproximation(f, point, h);
    assertThat(forwardDeriv, is(closeTo(48.0, 1E-2)));
    System.out.println(forwardDeriv);
  }
  
  @Test
  public void whenCentralDerivCalculatedApproximationClose() {
    Function f = (x) -> x * x * x;
    final double h = 1E-4;
    final double point = 4.0;
    final double centralDeriv = NumericalDerivatives.centralDifferenceApproximation(f, point, h);
    assertThat(centralDeriv, is(closeTo(48.0, 1E-8)));
    System.out.println(centralDeriv);
  }
  
  @Test
  public void whenForwardGradientCalculatedApproximationClose() {
    MultivariateDoubleFunction f = (point) -> point[0] * point[0] + point[1] * point[1];
    final double h = 1E-4;
    final double[] point = new double[] {3, 4};
    final double[] forwardGradient = NumericalDerivatives.forwardDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, forwardGradient, 1E-4);
    System.out.println(Arrays.toString(forwardGradient));
  }
  
  @Test
  public void whenCentralGradientCalculatedApproximationClose() {
    MultivariateDoubleFunction f = (point) -> point[0] * point[0] + point[1] * point[1];
    final double h = 1E-4;
    final double[] point = new double[] {3, 4};
    final double[] centralGradient= NumericalDerivatives.centralDifferenceGradient(f, point, h);
    final double[] expected = new double[] {6.0, 8.0};
    assertArrayEquals(expected, centralGradient, 1E-10);
    System.out.println(Arrays.toString(centralGradient));
    
  }
  
  
}
