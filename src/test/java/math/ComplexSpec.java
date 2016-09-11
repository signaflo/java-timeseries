package math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class ComplexSpec {
  
  @Test
  public void whenComplexNumberCreatedModulusCorrect() {
    assertThat(new Complex(3, 4).abs(), is(equalTo(5.0)));
  }
  
  @Test
  public void whenComplexNumbersAddedCorrectSumGiven() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.4, 3.7);
    Complex sum = c1.plus(c2);
    assertThat(sum.real(), is(equalTo(5.4)));
    assertThat(sum.im(), is(equalTo(8.7)));
  }
  
  @Test
  public void whenComplexNumbersMultipliedCorrectProductGiven() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.4, 3.7);
    Complex product = c1.times(c2);
    assertThat(product.real(), is(equalTo(-11.3)));
    assertThat(product.im(), is(equalTo(23.1)));
  }

}
