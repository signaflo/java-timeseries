/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.polynomial;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import math.function.CubicFunction;
import math.function.QuadraticFunction;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Jacob Rachiele
 */
public class NewtonPolynomialSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenPointAndValuesLengthDifferThenException() {
    exception.expect(IllegalArgumentException.class);
    new NewtonPolynomial(new double[] {0.3}, new double[] {0.4, 1.1});
  }

  @Test
  public void whenOnePointThenCoefficientEqualsFunctionValue() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0}, new double[] {4.0});
    assertThat(np.getCoefficient(0), is(equalTo(4.0)));
  }

  @Test
  public void whenMultiplePointsThenFirstCoefficientEqualsFirstFunctionValue() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0, 3.0}, new double[] {4.0, 9.0});
    assertThat(np.getCoefficient(0), is(equalTo(4.0)));
  }

  @Test
  public void whenTwoPointsThenSecondCoefficientEqualsLinearSlopeValue() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0, 3.0}, new double[] {4.0, 9.0});
    assertThat(np.getCoefficient(1), is(equalTo(5.0)));
  }

  @Test
  public void whenThreePointsThenThirdCoefficientEqualsThirdDividedDifference() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0, 4.0, 7.0}, new double[] {4.0, 16.0, 49.0});
    assertThat(np.getCoefficient(2), is(equalTo(1.0)));
  }

  @Test
  public void whenThreePointPolyEvalutatedThenResultCorrect() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0, 4.0, 7.0}, new double[] {4.0, 16.0, 49.0});
    assertThat(np.evaluateAt(3.0), is(equalTo(9.0)));
  }

  @Test
  public void whenConvertedThenQuadraticFunctionCorrect() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0, 4.0, 7.0}, new double[] {4.0, 16.0, 49.0});
    QuadraticFunction function = np.toQuadratic();
    double[] expected = new double[] {1.0, 0.0, 0.0};
    assertThat(function.coefficientsPrimitive(), is(equalTo(expected)));
  }

  @Test
  public void whenConvertedThenCubicFunctionCorrect() {
    NewtonPolynomial np = new NewtonPolynomial(new double[] {2.0, 4.0, 5.0, 7.0}, new double[]
        {8.0, 64.0, 125.0, 343.0});
    CubicFunction function = np.toCubic();
    double[] expected = new double[] {1.0, 0.0, 0.0, 0.0};
    assertThat(function.coefficientsPrimitive(), is(equalTo(expected)));
  }
}
