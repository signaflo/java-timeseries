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

import com.google.common.testing.EqualsTester;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RowMatrixSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RowMatrix A = getMatrixA();

    @Test
    public void whenNullVectorArrayThenNPE() {
        exception.expect(NullPointerException.class);
        List<Vector> vectors = null;
        new RowMatrix(vectors);
    }

    @Test
    public void whenNullVectorArrayThenNPE2() {
        exception.expect(NullPointerException.class);
        List<Vector> vectors = null;
        new RowMatrix(RowMatrix.VectorType.ROW, vectors);
    }

    @Test
    public void whenNullVectorTypeThenNPE2() {
        exception.expect(NullPointerException.class);
        new RowMatrix(null, getColumnVectors());
    }

    @Test
    public void whenMultiplyIncompatibleDimensionsThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        this.A.times(getMatrixC());
    }

    @Test
    public void whenMultiplyVectorIncompatibleDimensionsThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        this.A.times(Vector.from(1, 2));
    }

    @Test
    public void whenEmptyListOfVectorsThen0x0MatrixCreated() {
        List<Vector> vectors = new ArrayList<>();
        RowMatrix A = new RowMatrix(vectors);
        assertThat(A.nrow(), is(0));
        assertThat(A.ncol(), is(0));
    }

    @Test
    public void whenNonEmptyListWithZeroElementVectorsThenCorrectDimensions() {
        List<Vector> vectors = new ArrayList<>();
        vectors.add(Vector.from());
        vectors.add(Vector.from());
        RowMatrix A = new RowMatrix(vectors);
        assertThat(A.nrow(), is(2));
        assertThat(A.ncol(), is(0));
    }

    @Test
    public void whenZeroRowsAndNonZeroColumnsSpecifiedThenCorrectDimensions() {
        List<Vector> vectors = new ArrayList<>();
        RowMatrix A = new RowMatrix(0, 2);
        assertThat(A.nrow(), is(0));
        assertThat(A.ncol(), is(2));
    }

    @Test
    public void whenZeroRowTimesAZeroColumnMatrixThenZeroByZeroMatrix() {
        RowMatrix A = new RowMatrix(0, 2);
        RowMatrix B = new RowMatrix(2, 0);
        RowMatrix AB = A.times(B);
        assertThat(AB.nrow(), is(0));
        assertThat(AB.ncol(), is(0));
    }

    @Test
    public void whenZeroRowTimesNonZeroColumnMatrixThenZeroByMMatrix() {
        RowMatrix A = new RowMatrix(0, 2);
        RowMatrix B = new RowMatrix(2, 2);
        RowMatrix AB = A.times(B);
        assertThat(AB.nrow(), is(0));
        assertThat(AB.ncol(), is(2));
    }

    @Test
    public void whenGetRowVectorsThenReturnWhatWasPassedIn() {
        assertThat(A.getRows(), is(getMatrixA().getRows()));
    }

    @Test
    public void whenMatrixFromColumnsThenEquivalentToMatrixFromRows() {
        RowMatrix matrix = RowMatrix.fromColumnVectors(getColumnVectors());
        assertThat(matrix, is(A));
    }

    @Test
    public void whenGetColumnsThenCorrectColumnsReturned() {
        List<Vector> expected = getColumnVectors();
        assertThat(A.getColumns(), is(expected));
    }

    @Test
    public void whenMultiplyVectorThenCorrectProduct() {
        Vector expected = Vector.from(52.0, 60.0, 78.0);
        assertThat(A.times(getVectorX()), is(expected));
    }

    @Test
    public void whenMultiplyMatrixThenCorrectProduct() {
        RowMatrix expected = getMatrixAB();
        assertThat(A.times(getMatrixB()), is(expected));
    }

    @Test
    public void whenMultiplySquareWithNonSquareThenCorrectProduct() {
        RowMatrix D = getMatrixD();
        RowMatrix expected = RowMatrix.columnBuilder(2)
                .addColumn(Vector.from(17.5, 25.0, 57.5))
                .addColumn(Vector.from(44.5, 61.0, 129.5))
                .build();
        assertThat(A.times(D), is(expected));
    }

    private RowMatrix getMatrixA() {
        List<Vector> rows = new ArrayList<>(3);
        rows.add(Vector.from(1.0, 3.0, 5.0));
        rows.add(Vector.from(2.0, 4.0, 6.0));
        rows.add(Vector.from(7.0, 8.0, 9.0));
        return new RowMatrix(rows);
    }

    private RowMatrix getMatrixB() {
        List<Vector> rows = new ArrayList<>(3);
        rows.add(Vector.from(3.0, 4.5, 1.0));
        rows.add(Vector.from(2.5, 8.0, 4.0));
        rows.add(Vector.from(3.5, 7.0, 9.5));
        return new RowMatrix(rows);
    }

    private RowMatrix getMatrixC() {
        List<Vector> rows = new ArrayList<>(2);
        rows.add(Vector.from(4.5, 1.0, 2.0));
        rows.add(Vector.from(8.0, 3.0, 5.5));
        return new RowMatrix(rows);
    }

    private RowMatrix getMatrixD() {
        List<Vector> columns = new ArrayList<>(2);
        columns.add(Vector.from(4.5, 1.0, 2.0));
        columns.add(Vector.from(8.0, 3.0, 5.5));
        return new RowMatrix(RowMatrix.VectorType.COLUMN, columns);
    }

    private RowMatrix getMatrixAB() {
        List<Vector> rows = new ArrayList<>(3);
        rows.add(Vector.from(28.0, 63.5, 60.5));
        rows.add(Vector.from(37.0, 83.0, 75.0));
        rows.add(Vector.from(72.5, 158.5, 124.5));
        return RowMatrix.fromRowVectors(rows);
    }

    private List<Vector> getColumnVectors() {
        List<Vector> columns = new ArrayList<>(3);
        columns.add(Vector.from(1.0, 2.0, 7.0));
        columns.add(Vector.from(3.0, 4.0, 8.0));
        columns.add(Vector.from(5.0, 6.0, 9.0));
        return columns;
    }

    private Vector getVectorX() {
        return Vector.from(-4.0, 2.0, 10.0);
    }

    @Test
    public void equalsContract() {
        new EqualsTester()
                .addEqualityGroup(this.A, getMatrixA())
                .addEqualityGroup(getMatrixB(), getMatrixB())
                .addEqualityGroup(getMatrixC(), getMatrixC())
                .addEqualityGroup(getMatrixD(), getMatrixD())
                .testEquals();
    }
}
