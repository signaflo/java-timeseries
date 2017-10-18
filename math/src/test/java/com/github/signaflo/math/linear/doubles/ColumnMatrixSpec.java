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

package com.github.signaflo.math.linear.doubles;

import com.google.common.testing.EqualsTester;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ColumnMatrixSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Matrix2 A = getMatrixA();

    @Test
    public void whenNullVectorArrayThenNPE() {
        exception.expect(NullPointerException.class);
        List<Vector> vectors = null;
        new ColumnMatrix(vectors);
    }

    @Test
    public void whenNullVectorArrayThenNPE2() {
        exception.expect(NullPointerException.class);
        List<Vector> vectors = null;
        new ColumnMatrix(1, 1, vectors);
    }

    @Test
    public void whenMultiplyIncompatibleDimensionsThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        getMatrixC().times(A);
    }

    @Test
    public void whenMultiplyVectorIncompatibleDimensionsThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        this.A.times(Vector.from(1, 2));
    }

    @Test
    public void whenEmptyListOfVectorsThen0x0MatrixCreated() {
        List<Vector> vectors = new ArrayList<>();
        Matrix2 A = new ColumnMatrix(vectors);
        assertThat(A.nrow(), is(0));
        assertThat(A.ncol(), is(0));
    }

    @Test
    public void whenNonEmptyListWithZeroElementVectorsThenCorrectDimensions() {
        List<Vector> vectors = new ArrayList<>();
        vectors.add(Vector.from());
        vectors.add(Vector.from());
        Matrix2 A = new ColumnMatrix(vectors);
        assertThat(A.nrow(), is(0));
        assertThat(A.ncol(), is(2));
    }

    @Test
    public void whenZeroRowsAndNonZeroColumnsSpecifiedThenCorrectDimensions() {
        Matrix2 A = new ColumnMatrix(0, 2);
        assertThat(A.nrow(), is(0));
        assertThat(A.ncol(), is(2));
    }

    @Test
    public void whenZeroRowTimesAZeroColumnMatrixThenZeroByZeroMatrix() {
        ColumnMatrix A = new ColumnMatrix(0, 3);
        ColumnMatrix B = new ColumnMatrix(3, 0);
        Matrix2 AB = A.times(B);
        assertThat(AB.nrow(), is(0));
        assertThat(AB.ncol(), is(0));
    }

    @Test
    public void whenZeroInnerDimAndNonZeroOuterDimThenFilledMatrix() {
        ColumnMatrix A = new ColumnMatrix(2, 0);
        ColumnMatrix B = new ColumnMatrix(0, 2);
        Vector zeros = Vector.zeros(2);
        Matrix2 expected = Matrix2.columnBuilder().addColumn(zeros).addColumn(zeros).build();
        Matrix2 AB = A.times(B);
        assertThat(AB, is(expected));
    }

    @Test
    public void whenZeroRowTimesNonZeroColumnMatrixThenZeroByNColMatrix() {
        ColumnMatrix A = new ColumnMatrix(0, 2);
        ColumnMatrix B = new ColumnMatrix(2, 2);
        Matrix2 AB = A.times(B);
        assertThat(AB.nrow(), is(0));
        assertThat(AB.ncol(), is(2));
    }

    @Test
    public void whenGetColumnVectorsThenReturnWhatWasPassedIn() {
        assertThat(A.getRows(), is(getMatrixA().getRows()));
    }

    @Test
    public void whenGetRowsThenCorrectRowsReturned() {
        List<Vector> expected = getRowVectorsMatrixA();
        assertThat(A.getRows(), is(expected));
    }

    @Test
    public void whenMultiplyVectorThenCorrectProduct() {
        Vector expected = Vector.from(52.0, 60.0, 78.0);
        assertThat(A.times(getVectorX()), is(expected));
    }

    @Test
    public void whenMultiplyMatrixThenCorrectProduct() {
        Matrix2 expected = getMatrixAB();
        assertThat(A.times(getMatrixB()), is(expected));
    }

    @Test
    public void whenMultiplySquareWithNonSquareThenCorrectProduct() {
        Matrix2 D = getMatrixD();
        Matrix2 expected = Matrix2.columnBuilder(2)
                                  .addColumn(Vector.from(17.5, 25.0, 57.5))
                                  .addColumn(Vector.from(44.5, 61.0, 129.5))
                                  .build();
        assertThat(A.times(D), is(expected));
    }

    private Matrix2 getMatrixA() {
        List<Vector> columns = new ArrayList<>(3);
        columns.add(Vector.from(1.0, 2.0, 7.0));
        columns.add(Vector.from(3.0, 4.0, 8.0));
        columns.add(Vector.from(5.0, 6.0, 9.0));
        return new ColumnMatrix(columns);
    }

    private Matrix2 getMatrixB() {
        List<Vector> columns = new ArrayList<>(3);
        columns.add(Vector.from(3.0, 2.5, 3.5));
        columns.add(Vector.from(4.5, 8.0, 7.0));
        columns.add(Vector.from(1.0, 4.0, 9.5));
        return new ColumnMatrix(columns);
    }

    private Matrix2 getMatrixC() {
        List<Vector> columns = new ArrayList<>(2);
        columns.add(Vector.from(4.5, 1.0, 2.0));
        columns.add(Vector.from(8.0, 3.0, 5.5));
        return new ColumnMatrix(columns);
    }

    private Matrix2 getMatrixD() {
        List<Vector> columns = new ArrayList<>(2);
        columns.add(Vector.from(4.5, 1.0, 2.0));
        columns.add(Vector.from(8.0, 3.0, 5.5));
        return new ColumnMatrix(columns);
    }

    private Matrix2 getMatrixAB() {
        List<Vector> columns = new ArrayList<>(3);
        columns.add(Vector.from(28.0, 63.5, 60.5));
        columns.add(Vector.from(37.0, 83.0, 75.0));
        columns.add(Vector.from(72.5, 158.5, 124.5));
        return Matrix2.fromRows(columns);
    }

    private List<Vector> getRowVectorsMatrixA() {
        List<Vector> rows = new ArrayList<>(3);
        rows.add(Vector.from(1.0, 3.0, 5.0));
        rows.add(Vector.from(2.0, 4.0, 6.0));
        rows.add(Vector.from(7.0, 8.0, 9.0));
        return rows;
    }

    private Vector getVectorX() {
        return Vector.from(-4.0, 2.0, 10.0);
    }

    @Test
    public void equalsContract() {
        Matrix2 B = new RowMatrix(getRowVectorsMatrixA());
        assertThat(A, is(B));
        assertThat(B, is(A));
        assertThat(A.hashCode(), is(B.hashCode()));
        Matrix2 C = Matrix2.columnBuilder(2)
                .addColumn(3.0, 2.3, 1.75E-7)
                .addColumn(4.0, 5.0, 6.0)
                .build();
        Matrix2 D = Matrix2.rowBuilder(3)
                .addRow(3.0, 4.0)
                .addRow(2.3, 5.0)
                .addRow(1.75E-7, 6.0)
                .build();
        assertThat(C, is(D));
        assertThat(D, is(C));
        assertThat(C.hashCode(), is(D.hashCode()));
        new EqualsTester()
                .addEqualityGroup(C, D)
                .addEqualityGroup(getMatrixB(), getMatrixB())
                .addEqualityGroup(getMatrixC(), getMatrixC())
                .testEquals();
    }
}
