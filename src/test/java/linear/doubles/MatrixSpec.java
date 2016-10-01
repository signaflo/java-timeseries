package linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class MatrixSpec {
  
  @Rule
  public ExpectedException exception = ExpectedException.none();
  
  @Test
  public void whenMatrixProductWithInvalidDimensionsExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    final Matrix matrix1 = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Matrix matrix2 = new Matrix(3, 2, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    matrix1.times(matrix2);
  }
  
  @Test
  public void whenMatrixSumWithInvalidDimensionsExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    final Matrix matrix1 = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Matrix matrix2 = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    matrix1.plus(matrix2);
  }
  
  @Test
  public void whenMatrixSumComputedResultCorrect() {
    final Matrix matrix1 = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Matrix matrix2 = new Matrix(3, 2, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    final Matrix sum = matrix1.plus(matrix2);
    final double[] expectedData = new double[] {9.0, 11.0, 3.5, 7.5, 4.5, 10.5};
    assertThat(sum.data(), is(equalTo(expectedData)));
  }
  
  @Test
  public void whenMatrixProductComputedResultCorrect() {
    final Matrix matrix1 = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Matrix matrix2 = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    final Matrix product = matrix1.times(matrix2);
    final double[] expectedData = new double[] {30.0, 43.0, 23.0, 20.0, 22.25, 21.75, 20.0, 19.5, 24.5};
    assertThat(product.data(), is(equalTo(expectedData)));
  }
  
  @Test
  public void whenMatrixVectorProductComputedResultCorrect() {
    final Matrix matrix = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Vector vector = new Vector(5.0, 9.0);
    final Vector product = matrix.times(vector);
    final double[] expectedData = new double[] {38.0, 30.0, 32.0};
    assertThat(product.elements(), is(equalTo(expectedData)));
  }
  
  @Test
  public void testMatrixCreation() {
    final Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(3);
    final Matrix matrix = builder.set(1, 2, 3.0).set(1, 0, 4.5).build();
    System.out.println(matrix);
  }

}
