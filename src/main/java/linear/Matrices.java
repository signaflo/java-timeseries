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

package linear;

import math.Real;

import java.util.ArrayList;
import java.util.List;

public class Matrices {

    enum MatrixOrder {
        ROW_MAJOR,
        COLUMN_MAJOR
    }

    private Matrices() {}

    static FieldMatrix<Real> matrixFrom(double[]... matrix) {
        List<FieldVector<Real>> realVectors = new ArrayList<>(matrix.length);
        for (double[] column : matrix) {
            realVectors.add(Vectors.vectorFrom(column));
        }
        return new FieldMatrix<>(realVectors);
    }

    static FieldMatrix<Real> matrixFrom(int nrows, int ncols, double... data) {
        return matrixFrom(MatrixOrder.ROW_MAJOR, nrows, ncols, data);
    }

    static FieldMatrix<Real> matrixFrom(MatrixOrder order, int nrows, int ncols, double... data) {
        switch (order) {
            case ROW_MAJOR:
                return rowMajorMatrix(nrows, ncols, data);
            case COLUMN_MAJOR:
            default:
                return columnMajorMatrix(nrows, ncols, data);
        }
    }

    private static FieldMatrix<Real> rowMajorMatrix(int nrows, int ncols, double... data) {
        List<FieldVector<Real>> realVectors = new ArrayList<>(ncols);
        double[] column;
        for (int i = 0; i < ncols; i++) {
            column = new double[nrows];
            for (int j = 0; j < nrows; j++) {
                column[j] = data[i + j * ncols];
            }
            realVectors.add(Vectors.vectorFrom(column));
        }
        return new FieldMatrix<>(realVectors);
    }

    private static FieldMatrix<Real> columnMajorMatrix(int nrows, int ncols, double... data) {
        List<FieldVector<Real>> realVectors = new ArrayList<>(ncols);
        double[] column;
        for (int i = 0; i < ncols; i++) {
            column = new double[nrows];
            for (int j = 0; j < nrows; j++) {
                column[j] = data[i * nrows + j];
            }
            realVectors.add(Vectors.vectorFrom(column));
        }
        return new FieldMatrix<>(realVectors);
    }
}
