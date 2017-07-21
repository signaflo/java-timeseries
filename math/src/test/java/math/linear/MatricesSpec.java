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

package math.linear;

import math.Real;
import math.linear.Matrices.MatrixOrder;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

public class MatricesSpec {

    private double[] array1 = {1.5, 3.0, 5.0};
    private double[] array2 = {4.0, 0.5, 2.0};
    private FieldVector<Real> vector1 = Vectors.vectorFrom(array1);
    private FieldVector<Real> vector2 = Vectors.vectorFrom(array2);
    private FieldMatrix<Real> matrix1 = new FieldMatrix<>(Arrays.asList(vector1, vector2));

    @Test
    public void whenMatrixFromThenCorrectMatrixCreated() {
        FieldMatrix<Real> matrix = Matrices.matrixFrom(array1, array2);
        assertThat(matrix, is(matrix1));
    }

    @Test
    public void whenMatrixFromOneDimArrayColumnMajorOrderCorrectMatrixCreated() {
        int nrows = 3;
        int ncols = 2;
        FieldMatrix<Real> matrix = Matrices.matrixFrom(MatrixOrder.COLUMN_MAJOR, nrows, ncols,
                                                       combine(array1, array2));
        assertThat(matrix, is(matrix1));
    }

    @Test
    public void whenMatrixFromOneDimArrayRowMajorOrderCorrectMatrixCreated() {
        int nrows = 3;
        int ncols = 2;
        double[] matrixArray = {1.5, 4.0, 3.0, 0.5, 5.0, 2.0};
        FieldMatrix<Real> matrix = Matrices.matrixFrom(nrows, ncols, matrixArray);
        assertThat(matrix, is(matrix1));
    }

    private static double[] combine(double[]... arrays) {
        int newArrayLength = 0;
        for (double[] array : arrays) {
            newArrayLength += array.length;
        }
        double[] newArray = new double[newArrayLength];
        newArrayLength = 0;
        for (double[] array : arrays) {
            System.arraycopy(array, 0, newArray, newArrayLength, array.length);
            newArrayLength += array.length;
        }
        return newArray;
    }
}
