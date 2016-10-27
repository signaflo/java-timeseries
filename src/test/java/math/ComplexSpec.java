package math;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public class ComplexSpec {
  
  @Test
  public void whenComplexNumberCreatedModulusCorrect() {
    assertThat(new Complex(3, 4).abs(), is(equalTo(5.0)));
  }

  @Test
  public void whenNoArgConstructorThenComplexZero() {
    Complex shouldBeZero = new Complex();
    assertThat(shouldBeZero.real(), is(0.0));
    assertThat(shouldBeZero.im(), is(0.0));
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
  public void whenComplexNumbersSubtractedCorrectDiffGiven() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.4, 3.7);
    Complex diff = c1.minus(c2);
    assertThat(diff, is(equalTo(new Complex(0.6, 1.3))));
  }
  
  @Test
  public void whenComplexNumbersMultipliedCorrectProductGiven() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.4, 3.7);
    Complex product = c1.times(c2);
    assertThat(product.real(), is(equalTo(-11.3)));
    assertThat(product.im(), is(equalTo(23.1)));
  }
  
  @Test
  public void whenSqrtComputedThenResultCorrect() {
    Complex c1 = new Complex(3, 5);
    assertThat(c1.sqrt(), is(equalTo(new Complex(2.101303392521568, 1.189737764140758))));
  }
  
  @Test
  public void whenComplexPlusDoubleThenResultCorrect() {
    Complex z = new Complex(3, 5);
    double r = 5.830951894845301;
    assertThat(z.plus(r), is(equalTo(new Complex(8.8309518948453, 5))));
  }
  
  @Test
  public void whenComplexDividedByDoubleThenResultCorrect() {
    Complex z = new Complex(3, 5);
    double r = 5.830951894845301;
    Complex zr = z.plus(r);
    assertThat(zr.dividedBy(zr.abs()), is(equalTo(new Complex(0.8701999067534788, 0.49269881498359286))));
  }

  @Test
  public void whenHashCodeAndEqualsThenValuesCorrect() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.4, 3.7);
    Complex c3 = new Complex(3, 5);
    assertThat(c1.hashCode(), is(not(c2.hashCode())));
    assertThat(c1.hashCode(), is(c3.hashCode()));
    assertThat(c1.equals(c1), is(true));
    assertThat(c1.equals(c2), is(false));
    assertThat(c1.equals(c3), is(true));
    assertThat(c1.equals(null), is(false));
    assertThat(c1.equals(new Object()), is(false));
    assertThat(c1.equals(new Complex(2.0, 5.0)), is(false));
  }

}
