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

package math.linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class VectorSpec {

  private Vector vec1;
  private Vector vec2;
  private Vector vec3;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void beforeMethod() {
    vec1 = new Vector(3.0, 4.0, 7.5);
    vec2 = new Vector(4.5, 5.0, 2.0, 5.5);
    vec3 = new Vector(6.0, 2.5, 10.0);
  }

  @Test
  public void whenAxpyThenResultCorrect() {
    Vector result = vec1.axpy(vec3, 5.0);
    Vector expected = new Vector(21.0, 22.5, 47.5);
    MatcherAssert.assertThat(result, is(equalTo(expected)));
  }

  @Test
  public void whenSumThenResultCorrect() {
    double result = vec1.sum();
    MatcherAssert.assertThat(result, is(equalTo(14.5)));
    result = vec1.sumOfSquares();
    MatcherAssert.assertThat(result, is(equalTo(81.25)));
  }

  @SuppressWarnings("EqualsWithItself")
  @Test
  public void whenEqualsAndHashCodeThenValuesCorrect() {
    assertThat(vec1.equals(vec2), is(false));
    assertThat(vec1.hashCode(), is(not(vec2.hashCode())));
    Vector newVec = new Vector(3.0, 4.0, 7.5);
    assertThat(vec1.equals(newVec), is(true));
    assertThat(vec1.hashCode(), is(newVec.hashCode()));
    assertThat(vec1.equals(new Object()), is(false));
    //noinspection ObjectEqualsNull
    assertThat(vec1.equals(null), is(false));
    assertThat(vec1.equals(vec1), is(true));
  }
  
  @Test
  public void whenOuterProductComputedResultingMatrixCorrect() {
    Matrix matrix = vec1.outerProduct(vec2);
    double[] expected = new double[] { 13.5, 15.0, 6.0, 16.5, 18.0, 20.0,
        8.0, 22.0, 33.75, 37.5, 15.0, 41.25};
    assertThat(matrix.data(), is(equalTo(expected)));
  }

  @Test
  public void whenDotProductZeroLengthVectorExceptionThrown() {
    Vector empty = new Vector();
    exception.expect(IllegalArgumentException.class);
    vec1.dotProduct(empty);
  }


  @Test
  public void whenVectorsAddedThenSumCorrect() {
    Vector sum = vec1.plus(vec3);
    Vector expected = new Vector(9.0, 6.5, 17.5);
    MatcherAssert.assertThat(sum, is(equalTo(expected)));
  }

  @Test
  public void whenVectorsSubtractedThenDiffCorrect() {
    Vector diff = vec3.minus(vec1);
    Vector expected = new Vector(3.0, -1.5, 2.5);
    MatcherAssert.assertThat(diff, is(equalTo(expected)));
  }

  @Test
  public void whenVectorSubtractedByScalarThenDiffCorrect() {
    Vector diff = vec3.minus(2.0);
    Vector expected = new Vector(4.0, 0.5, 8.0);
    MatcherAssert.assertThat(diff, is(equalTo(expected)));
  }

}
