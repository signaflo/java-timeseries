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

import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ToString
final class RowMatrix implements Matrix2 {

    private final List<Vector> rowVectors;
    private final int nrow;
    private final int ncol;

    RowMatrix(@NonNull List<Vector> vectors) {
        this.rowVectors = new ArrayList<>(vectors);
        this.nrow = this.rowVectors.size();
        if (this.nrow > 0) {
            this.ncol = rowVectors.get(0).size();
        } else {
            // allow creation of a 0x0 Matrix.
            this.ncol = 0;
        }
    }

    RowMatrix(int nrow, int ncol) {
        rowVectors = new ArrayList<>(nrow);
        this.nrow = nrow;
        this.ncol = ncol;
    }

    RowMatrix(int nrow, int ncol, @NonNull List<Vector> vectors) {
        if (nrow != vectors.size()) {
            throw new IllegalArgumentException("The number of rows must equal the number of vectors.");
        }
        this.nrow = nrow;
        this.ncol = ncol;
        this.rowVectors = new ArrayList<>(vectors);
    }

    @Override
    public List<Vector> getRows() {
        return new ArrayList<>(rowVectors);
    }

    @Override
    public List<Vector> getColumns() {
        List<Vector> columns = new ArrayList<>(this.ncol);
        if (this.rowVectors.isEmpty()) {
            return columns;
        }
        double[] columnElements = new double[this.nrow];
        for (int j = 0; j < this.ncol; j++) {
            for (int i = 0; i < this.nrow; i++) {
                columnElements[i] = this.rowVectors.get(i).at(j);
            }
            columns.add(Vector.from(columnElements));
        }
        return columns;
    }

    @Override
    public int nrow() {
        return this.nrow;
    }

    @Override
    public int ncol() {
        return this.ncol;
    }

    @Override
    public Matrix2 times(Matrix2 other) {
        if (this.ncol != other.nrow()) {
            throw new IllegalArgumentException("The number of columns of this matrix must equal the " +
                                               "number of rows of the other matrix.");
        }
        if (this.nrow == 0 || other.ncol() == 0) {
            // return an empty matrix with correct dimensions
            return new RowMatrix(this.nrow, other.ncol());
        }
        if (this.ncol == 0) {
            // At this point we know that this matrix and the other matrix are empty with a non-zero
            // number of rows and columns respectively.
            return filledMatrixFromEmptyMatrices(this.nrow, other.ncol());
        }
        List<Vector> otherColumns = other.getColumns();
        List<Vector> newRows = new ArrayList<>(this.nrow);
        for (int i = 0; i < this.nrow; i++) {
            double[] newRowElements = new double[other.ncol()];
            for (int j = 0; j < otherColumns.size(); j++) {
                newRowElements[j] = this.rowVectors.get(i).dotProduct(otherColumns.get(j));
            }
            newRows.add(Vector.from(newRowElements));
        }
        return new RowMatrix(this.nrow, other.ncol(), newRows);
    }

    private Matrix2 filledMatrixFromEmptyMatrices(int nrow, int ncol) {
        List<Vector> rows = new ArrayList<>(nrow);
        for (int i = 0; i < nrow; i++) {
            rows.add(Vector.zeros(ncol));
        }
        return new RowMatrix(rows);
    }

    @Override
    public Vector times(Vector vector) {
        if (vector.size() != this.ncol) {
            throw new IllegalArgumentException("The number of elements of the given vector must equal " +
                                               "the number of columns of this matrix.");
        }
        double[] newVectorElements = new double[this.nrow];
        for (int i = 0; i < this.nrow; i++) {
            newVectorElements[i] = rowVectors.get(i).dotProduct(vector);
        }
        return Vector.from(newVectorElements);
    }

//    private List<Vector> columnsToRows(List<Vector> columnVectors) {
//        final int nrow = columnVectors.get(0).size();
//        final int ncol = columnVectors.size();
//        List<Vector> rowVectors = new ArrayList<>(nrow);
//        for (int i = 0; i < nrow; i++) {
//            double[] rowElements = new double[ncol];
//            for (int j = 0; j < ncol; j++) {
//                rowElements[j] = columnVectors.get(j).at(i);
//            }
//            rowVectors.add(Vector.from(rowElements));
//        }
//        return rowVectors;
//    }

    // Equals and hashCode are a bit different than what you would normally see since a
    // ColumnMatrix and RowMatrix should be equal if they have the same mathematical structure.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null ||
            !Arrays.equals(o.getClass().getInterfaces(), this.getClass().getInterfaces())) return false;

        Matrix2 matrix2 = (Matrix2) o;

        if (nrow != matrix2.nrow()) return false;
        if (ncol != matrix2.ncol()) return false;
        if (!rowVectors.equals(matrix2.getRows())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = getColumns().hashCode();
        result = 31 * result + nrow;
        result = 31 * result + ncol;
        return result;
    }

}
