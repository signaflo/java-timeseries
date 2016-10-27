package linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class MatrixSpec {

  private Matrix matrix1;
  private Matrix matrix2;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void beforeMethod() {
    matrix1 = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    matrix2 = new Matrix(3, 2, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
  }
  
  @Test
  public void whenMatrixProductWithInvalidDimensionsExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    matrix1.times(matrix2);
  }

  @Test
  public void whenMatrixVectorProductWithInvalidDimensionsExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    matrix1.times(new Vector(5.0, 9.0, 3.5));
  }
  
  @Test
  public void whenMatrixSumWithInvalidDimensionsExceptionThrown() {
    matrix2 = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    exception.expect(IllegalArgumentException.class);
    matrix1.plus(matrix2);
  }

  @Test
  public void whenMatrixSumWithInvalidColumnDimensionExceptionThrown() {
    matrix2 = new Matrix(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
    exception.expect(IllegalArgumentException.class);
    matrix1.plus(matrix2);
  }

  @Test
  public void whenMatrixDiffWithInvalidDimensionsExceptionThrown() {
    matrix2 = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    exception.expect(IllegalArgumentException.class);
    matrix1.minus(matrix2);
  }

  @Test
  public void whenMatrixDiffWithInvalidColumnDimensionsExceptionThrown() {
    matrix2 = new Matrix(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
    exception.expect(IllegalArgumentException.class);
    matrix1.minus(matrix2);
  }

  @Test
  public void whenMatrixDimensionsNotMatchAmountOfDataThenExceptionThrown() {
    exception.expect(IllegalArgumentException.class);
    matrix1 = new Matrix(2, 2, 5.0, 9.0, 2.0);
  }
  
  @Test
  public void whenMatrixSumComputedResultCorrect() {
    final Matrix sum = matrix1.plus(matrix2);
    final double[] expectedResult = new double[] {9.0, 11.0, 3.5, 7.5, 4.5, 10.5};
    assertThat(sum.data(), is(expectedResult));
  }
  
  @Test
  public void whenMatrixProductComputedResultCorrect() {
    matrix2 = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    final Matrix product = matrix1.times(matrix2);
    final double[] expectedResult = new double[] {30.0, 43.0, 23.0, 20.0, 22.25, 21.75, 20.0, 19.5, 24.5};
    assertThat(product.data(), is(expectedResult));
  }
  
  @Test
  public void whenMatrixVectorProductComputedResultCorrect() {
    final Matrix matrix = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Vector vector = new Vector(5.0, 9.0);
    final Vector product = matrix.times(vector);
    final double[] expectedResult = new double[] {38.0, 30.0, 32.0};
    assertThat(product.elements(), is(expectedResult));
  }

  @Test
  public void whenIdentityBuilderThenBuiltMatrixCorrect() {
    final Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(3);
    final Matrix matrix = builder.set(1, 2, 3.0).set(1, 0, 4.5).build();
    final double[][] expectedResult = new double[][] {{1.0, 0.0, 0.0}, {4.5, 1.0, 3.0}, {0.0, 0.0, 1.0}};
    assertThat(matrix.data2D(), is(expectedResult));
  }

  @Test
  public void whenFillConstructorThenNewMatrixCorrect() {
    final double[][] expectedResult = new double[][] {{3.0, 3.0}, {3.0, 3.0}, {3.0, 3.0}};
    assertThat(new Matrix(3, 2, 3.0).data2D(), is(expectedResult));
  }

  @Test
  public void whenTwoDimConstructorThenNewMatrixCorrect() {
    matrix1 = new Matrix(2, 3, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    Matrix expectedResult = matrix1;
    double[][] data = new double[][] {{4.0, 2.0, 1.5}, {2.5, 1.0, 3.0}};
    assertThat(new Matrix(data), is(expectedResult));
  }

  @Test
  public void whenMatrixHashCodeAndEqualsThenValuesCorrect() {
    assertThat(matrix1.equals(matrix1), is(true));
    assertThat(matrix1.equals(matrix2), is(false));
    Matrix newMatrix = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    assertThat(matrix1.equals(newMatrix), is(true));
    assertThat(matrix1.hashCode(), is(newMatrix.hashCode()));
    assertThat(matrix1.hashCode(), is(not(matrix2.hashCode())));
    assertThat(matrix1.equals(null), is(false));
    assertThat(matrix1.equals(new Object()), is(false));
    newMatrix = new Matrix(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
    assertThat(matrix1.equals(newMatrix), is(false));
    newMatrix = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    assertThat(matrix1.equals(newMatrix), is(false));
  }

}
