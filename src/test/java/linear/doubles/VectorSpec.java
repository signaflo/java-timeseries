package linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public final class VectorSpec {
  
  @Test
  public void whenOuterProductComputedResultingMatrixCorrect() {
    Vector vec1 = new Vector(3.0, 4.0, 7.5);
    Vector vec2 = new Vector(4.5, 5.0, 2.0, 5.5);
    Matrix matrix = vec1.outerProduct(vec2);
    double[] expected = new double[] { 13.5, 15.0, 6.0, 16.5, 18.0, 20.0,
        8.0, 22.0, 33.75, 37.5, 15.0, 41.25};
    assertThat(matrix.data(), is(equalTo(expected)));
  }

}
