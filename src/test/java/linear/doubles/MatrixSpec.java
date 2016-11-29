package linear.doubles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public final class MatrixSpec {

  private Matrix A;
  private Matrix B;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Before
  public void beforeMethod() {
    A = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    B = new Matrix(3, 2, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
  }
  
  @Test
  public void whenMatrixProductWithInvalidDimensionsException() {
    exception.expect(IllegalArgumentException.class);
    A.times(B);
  }

  @Test
  public void whenMatrixVectorProductWithInvalidDimensionsException() {
    exception.expect(IllegalArgumentException.class);
    A.times(new Vector(5.0, 9.0, 3.5));
  }
  
  @Test
  public void whenMatrixSumWithInvalidDimensionsException() {
    B = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    exception.expect(IllegalArgumentException.class);
    A.plus(B);
  }

  @Test
  public void whenMatrixSumWithInvalidColumnDimensionException() {
    B = new Matrix(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
    exception.expect(IllegalArgumentException.class);
    A.plus(B);
  }

  @Test
  public void whenMatrixDiffWithInvalidDimensionsException() {
    B = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    exception.expect(IllegalArgumentException.class);
    A.minus(B);
  }

  @Test
  public void whenMatrixDiffWithInvalidColumnDimensionsException() {
    B = new Matrix(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
    exception.expect(IllegalArgumentException.class);
    A.minus(B);
  }

  @Test
  public void whenMatrixDimensionsNotMatchAmountOfDataThenException() {
    exception.expect(IllegalArgumentException.class);
    A = new Matrix(2, 2, 5.0, 9.0, 2.0);
  }
  
  @Test
  public void whenMatrixSumComputedThenSumIsAccurate() {
    final Matrix sum = A.plus(B);
    final double[] expectedResult = new double[] {9.0, 11.0, 3.5, 7.5, 4.5, 10.5};
    assertThat(sum.data(), is(expectedResult));
  }
  
  @Test
  public void whenMatrixProductComputedThenProductIsAccurate() {
    B = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    final Matrix product = A.times(B);
    final double[] expectedResult = new double[] {30.0, 43.0, 23.0, 20.0, 22.25, 21.75, 20.0, 19.5, 24.5};
    assertThat(product.data(), is(expectedResult));
  }
  
  @Test
  public void whenMatrixVectorProductComputedThenProductIsAccurate() {
    final Matrix matrix = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    final Vector vector = new Vector(5.0, 9.0);
    final Vector product = matrix.times(vector);
    final double[] expectedResult = new double[] {38.0, 30.0, 32.0};
    assertThat(product.elements(), is(expectedResult));
  }

  @Test
  public void whenIdentityBuilderThenCorrectMatrixBuilt() {
    final Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(3);
    final Matrix matrix = builder.set(1, 2, 3.0).set(1, 0, 4.5).build();
    final double[][] expectedResult = new double[][] {{1.0, 0.0, 0.0}, {4.5, 1.0, 3.0}, {0.0, 0.0, 1.0}};
    assertThat(matrix.data2D(), is(expectedResult));
  }

  @Test
  public void whenFillConstructorThenCorrectMatrix() {
    final double[][] expectedResult = new double[][] {{3.0, 3.0}, {3.0, 3.0}, {3.0, 3.0}};
    assertThat(new Matrix(3, 2, 3.0).data2D(), is(expectedResult));
  }

  @Test
  public void whenTwoDimConstructorThenCorrectMatrixt() {
    Matrix expectedResult = new Matrix(2, 3, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    double[][] data = new double[][] {{4.0, 2.0, 1.5}, {2.5, 1.0, 3.0}};
    assertThat(new Matrix(data), is(expectedResult));
  }

  @Test
  public void whenStaticMethodConstructorThenCorrectMatrix() {
    double[] m = new double[] {4.0, 2.0, 1.5, 2.5, 1.0, 3.0};
    Matrix expectedResult = Matrix.create(2, 3, m);
    double[][] data = new double[][] {{4.0, 2.0, 1.5}, {2.5, 1.0, 3.0}};
    assertThat(new Matrix(data), is(expectedResult));
  }

  @Test
  public void whenMatrixHashCodeAndEqualsThenCorrectResponse() {
    assertThat(A.equals(B), is(false));
    Matrix C = new Matrix(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
    assertThat(A.equals(C), is(true));
    assertThat(A.hashCode(), is(C.hashCode()));
    assertThat(A.hashCode(), is(not(B.hashCode())));
    //noinspection ObjectEqualsNull
    assertThat(A.equals(null), is(false));
    assertThat(A.equals(new Object()), is(false));
    C = new Matrix(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
    assertThat(A.equals(C), is(false));
    C = new Matrix(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    assertThat(A.equals(C), is(false));
  }

}
