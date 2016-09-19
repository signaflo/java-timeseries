package linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class MatricesSpec {

  @Test
  public void whenIdentityMatrixCalledCorrectDataSet() {
    Matrix eye = Matrices.identity(3);
    double[] expected = new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0};
    assertThat(eye.data(), is(equalTo(expected)));
  }
}
