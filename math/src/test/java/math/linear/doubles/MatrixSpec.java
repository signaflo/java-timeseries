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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public final class MatrixSpec {

    private Matrix A;
    private Matrix B;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void beforeMethod() {
        A = new MatrixOneD(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
        B = new MatrixOneD(3, 2, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
    }

    @Test
    public void whenMatrixProductWithInvalidDimensionsException() {
        exception.expect(IllegalArgumentException.class);
        A.times(B);
    }

    @Test
    public void whenMatrixVectorProductWithInvalidDimensionsException() {
        exception.expect(IllegalArgumentException.class);
        A.times(new GenericVector(5.0, 9.0, 3.5));
    }

    @Test
    public void whenMatrixSumWithInvalidDimensionsException() {
        B = Matrix.create(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
        exception.expect(IllegalArgumentException.class);
        A.plus(B);
    }

    @Test
    public void whenMatrixSumWithInvalidColumnDimensionException() {
        B = new MatrixOneD(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
        exception.expect(IllegalArgumentException.class);
        A.plus(B);
    }

    @Test
    public void whenMatrixDiffWithInvalidDimensionsException() {
        B = new MatrixOneD(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
        exception.expect(IllegalArgumentException.class);
        A.minus(B);
    }

    @Test
    public void whenMatrixDiffWithInvalidColumnDimensionsException() {
        B = new MatrixOneD(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
        exception.expect(IllegalArgumentException.class);
        A.minus(B);
    }

    @Test
    public void whenMatrixDimensionsNotMatchAmountOfDataThenException() {
        exception.expect(IllegalArgumentException.class);
        A = new MatrixOneD(2, 2, 5.0, 9.0, 2.0);
    }

    @Test
    public void whenMatrixSumComputedThenSumIsAccurate() {
        final Matrix sum = A.plus(B);
        final double[] expectedResult = new double[]{9.0, 11.0, 3.5, 7.5, 4.5, 10.5};
        Matrix C = Matrix.create(3, 2, expectedResult);
        assertThat(sum, is(C));
        assertThat(C.minus(B),is(A));
    }

    @Test
    public void whenMatrixProductComputedThenProductIsAccurate() {
        B = new MatrixOneD(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
        final Matrix product = A.times(B);
        final double[] expectedResult = new double[]{30.0, 43.0, 23.0, 20.0, 22.25, 21.75, 20.0, 19.5, 24.5};
        assertThat(product.data(), is(expectedResult));
    }

    @Test
    public void whenMatrixVectorProductComputedThenProductIsAccurate() {
        final Matrix matrix = new MatrixOneD(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
        final Vector vector = Vector.from(5.0, 9.0);
        final Vector product = matrix.times(vector);
        final double[] expectedResult = new double[]{38.0, 30.0, 32.0};
        assertThat(product.elements(), is(expectedResult));
    }

    @Test
    public void whenIdentityBuilderThenCorrectMatrixBuilt() {
        final MatrixOneD.IdentityBuilder builder = new MatrixOneD.IdentityBuilder(3);
        final Matrix matrix = builder.set(1, 2, 3.0)
                                     .setColumn(0, Vector.from(7.5, 4.5, 9.0))
                                     .setRow(2, Vector.from(3.5, 2.5, 0.5)).build();
        final double[][] expectedResult = new double[][]{{7.5, 0.0, 0.0}, {4.5, 1.0, 3.0}, {3.5, 2.5, 0.5}};
        assertThat(matrix.data2D(Matrix.Layout.BY_ROW), is(expectedResult));
    }

    @Test
    public void whenZeroBuilderThenCorrectMatrixBuilt() {
        final MatrixOneD.ZeroBuilder builder = new MatrixOneD.ZeroBuilder(3, 3);
        final Matrix matrix = builder.set(1, 2, 3.0)
                                     .setColumn(0, Vector.from(7.5, 4.5, 9.0))
                                     .setRow(2, Vector.from(3.5, 2.5, 0.5)).build();
        final double[][] expectedResult = new double[][]{{7.5, 0.0, 0.0}, {4.5, 0.0, 3.0}, {3.5, 2.5, 0.5}};
        assertThat(matrix.data2D(Matrix.Layout.BY_ROW), is(expectedResult));
    }

    @Test
    public void whenGetData2DThenCorrectTwoDArray() {
        double[][] expected = {{4.0, 2.0}, {1.5, 2.5}, {1.0, 3.0}};
        assertThat(A.data2D(Matrix.Layout.BY_ROW), is(expected));
    }

    @Test
    public void whenFillConstructorThenCorrectMatrix() {
        final double[][] expectedResult = new double[][]{{3.0, 3.0}, {3.0, 3.0}, {3.0, 3.0}};
        assertThat(Matrix.fill(3, 2, 3.0).data2D(Matrix.Layout.BY_ROW), is(expectedResult));
    }

    @Test
    public void whenTwoDimConstructorThenCorrectMatrix() {
        Matrix expectedResult = new MatrixOneD(2, 3, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
        double[][] data = new double[][]{{4.0, 2.0, 1.5}, {2.5, 1.0, 3.0}};
        assertThat(Matrix.create(Matrix.Layout.BY_ROW, data), is(expectedResult));
    }

    @Test
    public void whenStaticMethodConstructorThenCorrectMatrix() {
        double[] m = new double[]{4.0, 2.0, 1.5, 2.5, 1.0, 3.0};
        Matrix expectedResult = Matrix.create(2, 3, m);
        double[][] data = new double[][]{{4.0, 2.0, 1.5}, {2.5, 1.0, 3.0}};
        assertThat(Matrix.create(Matrix.Layout.BY_ROW, data), is(expectedResult));
    }

    @Test
    public void whenMatrixTransposeThenCorrectMatrixReturned() {
        double[] data = new double[]{4.0, 1.5, 1.0, 2.0, 2.5, 3.0};
        Matrix expected = new MatrixOneD(2, 3, data);
        assertThat(A.transpose(), is(expected));
        data = new double[]{5.0, 2.0, 3.5, 9.0, 5.0, 7.5};
        expected = new MatrixOneD(2, 3, data);
        assertThat(B.transpose(), is(expected));
        A = new MatrixOneD(2, 2, 1.0, 2.5, 10.0, 5.0);
        data = new double[]{1.0, 10.0, 2.5, 5.0};
        expected = new MatrixOneD(2, 2, data);
        assertThat(A.transpose(), is(expected));
        double[][] twoD = {{1.0, 2.5}, {10.0, 5.0}};
        A = Matrix.create(twoD);
        assertThat(A.transpose(), is(expected));
        A = new MatrixOneD(Matrix.Layout.BY_COLUMN, twoD);
    }

    @Test
    public void whenMatrixSquareOrNotThenCorrectBooleanReturned() {
        A = new MatrixOneD(2, 2, 1.0, 2.5, 10.0, 5.0);
        assertThat(A.isSquare(), is(true));
        assertThat(B.isSquare(), is(false));
    }

    @Test
    public void whenGetRowThenCorrectRow() {
        assertThat(A.getRow(0), is(Vector.from(4.0, 2.0)));
    }

    @Test
    public void whenGetColumnThenCorrectColumn() {
        assertThat(A.getColumn(0), is(Vector.from(4.0, 1.5, 1.0)));
    }

    @Test
    public void whenGetRowIColumnJThenCorrectValue() {
        A = new MatrixOneD(2, 2, 1.0, 2.5, 10.0, 5.0);
        assertThat(A.get(1, 1), is(5.0));
        assertThat(A.get(0, 1), is(2.5));
        double[][] data = {{1.0, 2.5}, {10.0, 5.0}};
        A = new MatrixOneD(Matrix.Layout.BY_ROW, data);
        assertThat(A.get(0, 1), is(2.5));
        assertThat(A.get(1, 0),is(10.0));
        data = new double[][] {{1.0, 10.0}, {2.5, 5.0}};
        A = new MatrixOneD(Matrix.Layout.BY_COLUMN, data);
        assertThat(A.get(0, 1), is(2.5));
        assertThat(A.get(1, 0), is(10.0));
    }

    @Test
    public void whenIdentityBuilderThenIdentityMatrixReturned() {
        double[][] identity = {{1.0, 0.0}, {0.0, 1.0}};
        Matrix expected = Matrix.create(identity);
        Matrix idMatrix = Matrix.identityBuilder(2).build();
        assertThat(idMatrix, is(expected));
    }

    @Test
    public void whenBuilderThenAllZeros() {
        double[][] zeros = {{0.0, 0.0}, {0.0, 0.0}};
        Matrix expected = Matrix.create(zeros);
        Matrix zeroMatrix = Matrix.builder(2, 2).build();
        assertThat(zeroMatrix, is(expected));
    }

    @Test
    public void whenGetDiagonalThenDiagonalElementsReturned() {
        double[] expected = new double[] {4.0, 2.5};
        assertThat(A.diagonal(), is(expected));
    }

    @Test
    public void whenRowPushThenNewMatrixWithNewRowAtTop() {
        Matrix expected = new MatrixOneD(4, 2, 7.5, 0.5, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
        Matrix rowPushed = A.pushRow(Vector.from(7.5, 0.5));
        assertThat(rowPushed, is(expected));
    }

    @Test
    public void whenColumnPushThenNewMatrixNewColumnToLeft() {
        double[] newColumn = {6.0, 9.0, 4.5};
        Matrix expected = new MatrixOneD(Matrix.Layout.BY_COLUMN,
                                         new double[][] {newColumn, {4.0, 1.5, 1.0}, {2.0, 2.5, 3.0}});
        Matrix columnPushed = A.pushColumn(Vector.from(newColumn));
        assertThat(columnPushed, is(expected));
    }

    @Test
    public void whenData2DByColumnThenExpectedTwoDArray() {
        double[][] expected = {{4.0, 2.0}, {1.5, 2.5}, {1.0, 3.0}};
        assertThat(A.data2D(), is(expected));
        expected = new double[][] {{4.0, 1.5, 1.0}, { 2.0, 2.5, 3.0}};
        assertThat(A.data2D(Matrix.Layout.BY_COLUMN), is(expected));
        A = Matrix.create(Matrix.Layout.BY_COLUMN, expected);
        assertThat(A.data2D(), is(expected));
    }

    @Test
    public void whenSymmetricPartThenCorrectSymmetricMatrixReturned() {
        MatrixOneD X = (MatrixOneD)Matrix.identityBuilder(2)
                  .set(0, 1, 3.5)
                  .set(1, 0, -2.0)
                  .set(1, 1, 7.0)
                  .build();
        Matrix symmetricPart = Matrix.identityBuilder(2)
                                     .set(0, 1, 0.75)
                                     .set(1, 0, 0.75)
                                     .set(1, 1, 7.0)
                                     .build();
        assertThat(X.getSymmetricPart(), is(symmetricPart));
    }

    @Test
    public void whenMatrixHashCodeAndEqualsThenCorrectResponse() {
        assertThat(A.equals(A), is(true));
        assertThat(A.equals(B), is(false));
        Matrix C = new MatrixOneD(3, 2, 4.0, 2.0, 1.5, 2.5, 1.0, 3.0);
        assertThat(A.equals(C), is(true));
        assertThat(A.hashCode(), is(C.hashCode()));
        assertThat(A.hashCode(), is(not(B.hashCode())));
        //noinspection ObjectEqualsNull
        assertThat(A.equals(null), is(false));
        assertThat(A.equals(new Object()), is(false));
        C = new MatrixOneD(3, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5, 4.0, 1.0, 3.5);
        assertThat(A.equals(C), is(false));
        C = new MatrixOneD(2, 3, 5.0, 9.0, 2.0, 5.0, 3.5, 7.5);
        assertThat(A.equals(C), is(false));
    }

}
