/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package math.linear;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import math.Complex;

public class VectorsSpec {

  private FieldVector<Complex> vec1;
  private FieldVector<Complex> vec2;
  
  @Before
  public void beforeMethod() {
    Complex c1 = new Complex(3, 5);
    Complex c2 = new Complex(2.5, 4.5);
    Complex c3 = new Complex(2.4, 4.0);
    Complex c4 = new Complex(6, 2);

    List<Complex> l1 = new ArrayList<>(2);
    l1.add(c1); l1.add(c2);
    List<Complex> l2 = new ArrayList<>(2);
    l2.add(c3); l2.add(c4);
    
    vec1 = new FieldVector<>(l1);
    vec2 = new FieldVector<>(l2);
  }
  
  @Test
  public void whenAxpyThenCorrectVectorReturned() {
    Complex alpha = new Complex(2, 3.5);
    Complex c5 = new Complex(-9.1, 24.5);
    Complex c6 = new Complex(-4.75, 19.75);
    List<Complex> l3 = new ArrayList<>(2);
    l3.add(c5); l3.add(c6);
    FieldVector<Complex> vec3 = new FieldVector<>(l3);
    assertThat(ComplexVectors.axpy(vec1, vec2, alpha), is(equalTo(vec3)));
  }

  @Test
  public void whenZeroVectorThenVectorOfZerosCreated() {
    Complex zero = new Complex(0.0, 0.0);
    FieldVector<Complex> zeros = new FieldVector<>(zero, zero, zero);
    assertThat(ComplexVectors.zeroVector(3), is(equalTo(zeros)));
  }
  
  @Test
  public void whenLinearCombinationThenCorrectVectorReturned() {
    Complex alpha = new Complex(2, 3.5);
    Complex beta = new Complex(3, 1.0);
    List<Complex> scalars = new ArrayList<>(2);
    scalars.add(alpha); scalars.add(beta);
    List<FieldVector<Complex>> vectors = new ArrayList<>(2);
    vectors.add(vec1); vectors.add(vec2);
    FieldVector<Complex> expected = new FieldVector<>(new Complex(-8.3, 34.9), new Complex(5.25, 29.75));
    assertThat(ComplexVectors.linearCombination(vectors, scalars), is(equalTo(expected)));
  }
}
