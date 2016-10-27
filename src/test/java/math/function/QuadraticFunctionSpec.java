/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package math.function;

import math.Complex;
import math.Real;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Jacob Rachiele
 */
public class QuadraticFunctionSpec {

  @Test
  public void whenZerosComputedThenValuesCorrect() {
    QuadraticFunction function = new QuadraticFunction(new Real(1.5), new Real(2.0), new Real(-4.0));
    Complex[] expectedZeros = new Complex[] {new Complex(1.0971675407097272),
        new Complex(-2.4305008740430605)};
    //assertThat(function.zeros(), is(expectedZeros));
  }
}
