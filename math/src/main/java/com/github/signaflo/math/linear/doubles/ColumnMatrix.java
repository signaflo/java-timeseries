package com.github.signaflo.math.linear.doubles;

import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Aug. 02, 2017
 */
@ToString
class ColumnMatrix implements Matrix2 {

    private final List<Vector> columnVectors;
    private final int nrow;
    private final int ncol;

    ColumnMatrix(@NonNull List<Vector> columnVectors) {
        this.columnVectors = new ArrayList<>(columnVectors);
        this.ncol = this.columnVectors.size();
        if (this.ncol > 0) {
            this.nrow = columnVectors.get(0).size();
        } else {
            // allow creation of a 0x0 Matrix.
            this.nrow = 0;
        }
    }

    ColumnMatrix(int nrow, int ncol) {
        columnVectors = new ArrayList<>(nrow);
        this.nrow = nrow;
        this.ncol = ncol;
    }

    ColumnMatrix(int nrow, int ncol, @NonNull List<Vector> vectors) {
        if (ncol != vectors.size()) {
            throw new IllegalArgumentException("The number of columns must equal the number of vectors.");
        }
        this.nrow = nrow;
        this.ncol = ncol;
        this.columnVectors = new ArrayList<>(vectors);
    }

    @Override
    public List<Vector> getRows() {
        List<Vector> rows = new ArrayList<>(this.ncol);
        if (this.columnVectors.isEmpty()) {
            return rows;
        }
        double[] rowElements = new double[this.ncol];
        for (int i = 0; i < this.nrow; i++) {
            for (int j = 0; j < this.ncol; j++) {
                rowElements[j] = this.columnVectors.get(j).at(i);
            }
            rows.add(Vector.from(rowElements));
        }
        return rows;
    }

    @Override
    public List<Vector> getColumns() {
        return new ArrayList<>(this.columnVectors);
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
            return new ColumnMatrix(this.nrow, other.ncol());
        }
        if (this.ncol == 0) {
            // At this point we know that this matrix and the other matrix are empty with a non-zero
            // number of rows and columns respectively.
            return filledMatrixFromEmptyMatrices(this.nrow, other.ncol());
        }
        List<Vector> otherColumns = other.getColumns();
        List<Vector> product = new ArrayList<>(this.ncol);
        for (int i = 0; i < otherColumns.size(); i++) {
            product.add(this.times(otherColumns.get(i)));
        }
        return new ColumnMatrix(this.nrow, other.ncol(), product);
    }

    private Matrix2 filledMatrixFromEmptyMatrices(int nrow, int ncol) {
        List<Vector> columns = new ArrayList<>(ncol);
        for (int i = 0; i < ncol; i++) {
            columns.add(Vector.zeros(nrow));
        }
        return new ColumnMatrix(columns);
    }

    @Override
    public Vector times(Vector vector) {
        if (vector.size() != this.ncol) {
            throw new IllegalArgumentException("The number of elements of the given vector must equal " +
                                               "the number of columns of this matrix.");
        }
        Vector product = Vector.zeros(this.nrow);
        for (int i = 0; i < this.ncol; i++) {
            product = product.plus(this.columnVectors.get(i).scaledBy(vector.at(i)));
        }
        return product;
    }

    // Equals and hashCode are a bit different than what you would normally see since a
    // ColumnMatrix and RowMatrix should be equal if they have the same mathematical structure.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !Arrays.equals(o.getClass().getInterfaces(), this.getClass().getInterfaces())) return false;

        Matrix2 that = (Matrix2) o;

        if (nrow != that.nrow()) return false;
        if (ncol != that.ncol()) return false;
        return columnVectors.equals(that.getColumns());
    }

    @Override
    public int hashCode() {
        int result = columnVectors.hashCode();
        result = 31 * result + nrow;
        result = 31 * result + ncol;
        return result;
    }
}
