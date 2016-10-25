package linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

public final class VectorSpec {

  private Vector vec1;
  private Vector vec2;
  private Vector vec3;

  @Before
  public void beforeMethod() {
    vec1 = new Vector(3.0, 4.0, 7.5);
    vec2 = new Vector(4.5, 5.0, 2.0, 5.5);
    vec3 = new Vector(6.0, 2.5, 10.0);
  }
  
  @Test
  public void whenOuterProductComputedResultingMatrixCorrect() {
    Matrix matrix = vec1.outerProduct(vec2);
    double[] expected = new double[] { 13.5, 15.0, 6.0, 16.5, 18.0, 20.0,
        8.0, 22.0, 33.75, 37.5, 15.0, 41.25};
    assertThat(matrix.data(), is(equalTo(expected)));
  }

  @Test
  public void whenVectorsAddedThenSumCorrect() {
    Vector sum = vec1.plus(vec3);
    Vector expected = new Vector(9.0, 6.5, 17.5);
    assertThat(sum, is(equalTo(expected)));
  }

  @Test
  public void whenVectorsSubtractedThenDiffCorrect() {
    Vector diff = vec3.minus(vec1);
    Vector expected = new Vector(3.0, -1.5, 2.5);
    assertThat(diff, is(equalTo(expected)));
  }

  @Test
  public void whenVectorSubtractedByScalarThenDiffCorrect() {
    Vector diff = vec3.minus(2.0);
    Vector expected = new Vector(4.0, 0.5, 8.0);
    assertThat(diff, is(equalTo(expected)));
  }

}
