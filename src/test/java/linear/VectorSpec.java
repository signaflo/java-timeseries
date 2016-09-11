package linear;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import math.Complex;

public class VectorSpec {

  @Test
  public void whenVectorsAddedSumCorrect() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.5, 4.5);
    Complex c3 = new Complex(2.4, 4.0);
    Complex c4 = new Complex(6, 2);
    
    List<Complex> l1 = new ArrayList<>(2);
    l1.add(c1); l1.add(c2);
    List<Complex> l2 = new ArrayList<>(2);
    l2.add(c3); l2.add(c4);
    
    Vector<Complex> vec1 = new Vector<>(l1);
    Vector<Complex> vec2 = new Vector<>(l2);
    Vector<Complex> sum = vec1.plus(vec2);
    
    List<Complex> expectedL = new ArrayList<>(2);
    expectedL.add(new Complex(5.4, 9.0));
    expectedL.add(new Complex(8.5, 6.5));
    Vector<Complex> expected = new Vector<>(expectedL);
    assertThat(sum, is(equalTo(expected)));
  }
}
