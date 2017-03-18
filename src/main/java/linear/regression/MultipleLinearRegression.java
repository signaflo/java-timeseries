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
package linear.regression;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.ejml.alg.dense.mult.MatrixVectorMult;
import org.ejml.data.D1Matrix64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.interfaces.decomposition.QRDecomposition;
import org.ejml.interfaces.linsol.LinearSolver;
import org.ejml.ops.CommonOps;
import stats.Statistics;

import java.util.ArrayList;
import java.util.List;

import static data.DoubleFunctions.*;

/**
 * Linear regression with support for both single and multiple prediction variables.
 * This implementation is immutable and thread-safe.
 */
@EqualsAndHashCode @ToString
public final class MultipleLinearRegression implements LinearRegression {

    private final List<List<Double>> predictors;
    private final List<Double> response;
    private final List<Double> beta;
    private final List<Double> standardErrors;
    private final List<Double> fitted;
    private final List<Double> residuals;
    private final double sigma2;
    private final boolean hasIntercept;

    private MultipleLinearRegression(Builder builder) {
        this.predictors = builder.listBuilder.build();
        this.response = builder.response;
        this.hasIntercept = builder.hasIntercept;
        MatrixFormulation matrixFormulation = new MatrixFormulation();
        this.beta = matrixFormulation.getBetaEstimates();
        this.fitted = matrixFormulation.getFittedvalues();
        this.residuals = matrixFormulation.getResiduals();
        this.sigma2 = matrixFormulation.getSigma2();
        this.standardErrors = matrixFormulation.getBetaStandardErrors(beta.size());
    }

    @Override
    public List<List<Double>> predictors() {
        return this.predictors;
    }

    @Override
    public List<Double> beta() {
        return beta;
    }

    @Override
    public List<Double> standardErrors() {
        return ImmutableList.copyOf(this.standardErrors);
    }

    @Override
    public List<Double> response() {
        return this.response;
    }

    @Override
    public List<Double> fitted() {
        return ImmutableList.copyOf(this.fitted);
    }

    @Override
    public List<Double> residuals() {
        return ImmutableList.copyOf(this.residuals);
    }

    @Override
    public double sigma2() {
        return this.sigma2;
    }

    @Override
    public boolean hasIntercept() {
        return this.hasIntercept;
    }

    public MultipleLinearRegression withHasIntercept(boolean hasIntercept) {
        return new Builder().from(this).hasIntercept(hasIntercept).build();
    }

    public MultipleLinearRegression withResponse(List<Double> response) {
        return new Builder().from(this).response(response).build();
    }

    public MultipleLinearRegression withPredictor(List<Double> predictor) {
        return new Builder().from(this).predictor(predictor).build();
    }

    /**
     * Create and return a new builder for this class.
     *
     * @return a new builder for this class.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * A builder for a multiple linear regression model.
     */
    public static final class Builder {
        private ImmutableList.Builder<List<Double>> listBuilder;
        private List<Double> response;
        private boolean hasIntercept = true;

        /**
         * Copy the attributes of the given regression object to this builder and return this builder.
         *
         * @param regression the object to copy the attributes from.
         * @return this builder.
         */
        public Builder from(LinearRegression regression) {
            this.listBuilder = ImmutableList.builder();
            for (List<Double> predictor : regression.predictors()) {
                this.listBuilder.add(ImmutableList.copyOf(predictor));
            }
            this.response = ImmutableList.copyOf(regression.response());
            this.hasIntercept = regression.hasIntercept();
            return this;
        }

        Builder predictors(List<List<Double>> predictors) {
            if (this.listBuilder == null) {
                this.listBuilder = ImmutableList.builder();
            }
            for (List<Double> predictor : predictors) {
                this.listBuilder.add(ImmutableList.copyOf(predictor));
            }
            return this;
        }

        public Builder predictor(List<Double> predictor) {
            if (this.listBuilder == null) {
                this.listBuilder = ImmutableList.builder();
            }
            this.listBuilder.add(ImmutableList.copyOf(predictor));
            return this;
        }

        public Builder response(List<Double> response) {
            this.response = ImmutableList.copyOf(response);
            return this;
        }

        public Builder hasIntercept(boolean hasIntercept) {
            this.hasIntercept = hasIntercept;
            return this;
        }

        public MultipleLinearRegression build() {
            return new MultipleLinearRegression(this);
        }
    }

    private class MatrixFormulation {

        private final DenseMatrix64F A; // The design matrix.
        private final DenseMatrix64F At; // The transpose of A.
        private final DenseMatrix64F AtAInv; // The inverse of At times A.
        private final DenseMatrix64F b; // The parameter estimate vector.
        private final DenseMatrix64F y; // The response vector.
        private final D1Matrix64F fitted;
        private final List<Double> residuals;
        private final double sigma2;
        private final DenseMatrix64F covarianceMatrix;

        private MatrixFormulation() {
            int numRows = response.size();
            int numCols = predictors.size() + ((hasIntercept()) ? 1 : 0);
            this.A = createMatrixA(numRows, numCols);
            this.At = new DenseMatrix64F(numCols, numRows);
            CommonOps.transpose(A, At);
            this.AtAInv = new DenseMatrix64F(numCols, numCols);
            this.b = new DenseMatrix64F(numCols, 1);
            this.y = new DenseMatrix64F(numRows, 1);
            solveSystem(numRows, numCols);
            this.fitted = computeFittedValues();
            this.residuals = computeResiduals();
            this.sigma2 = estimateSigma2(numCols);
            this.covarianceMatrix = new DenseMatrix64F(numCols, numCols);
            CommonOps.scale(sigma2, AtAInv, covarianceMatrix);
        }

        private void solveSystem(int numRows, int numCols) {
            LinearSolver<DenseMatrix64F> qrSolver = LinearSolverFactory.qr(numRows, numCols);
            QRDecomposition<DenseMatrix64F> decomposition = qrSolver.getDecomposition();
            qrSolver.setA(A);
            y.setData(arrayFrom(response));
            qrSolver.solve(this.y, this.b);
            DenseMatrix64F R = decomposition.getR(null, true);
            LinearSolver<DenseMatrix64F> linearSolver = LinearSolverFactory.linear(numCols);
            linearSolver.setA(R);
            DenseMatrix64F Rinv = new DenseMatrix64F(numCols, numCols);
            linearSolver.invert(Rinv);
            CommonOps.multOuter(Rinv, this.AtAInv);
        }

        private DenseMatrix64F createMatrixA(int numRows, int numCols) {
            double[] data = hasIntercept ? fill(numRows, 1.0) : arrayFrom();
            for (List<Double> predictor : predictors) {
                data = combine(data, arrayFrom(predictor));
            }
            boolean isRowMajor = false;
            return new DenseMatrix64F(numRows, numCols, isRowMajor, data);
        }

        private D1Matrix64F computeFittedValues() {
            D1Matrix64F fitted = new DenseMatrix64F(response.size(), 1);
            MatrixVectorMult.mult(A, b, fitted);
            return fitted;
        }

        private List<Double> computeResiduals() {
            List<Double> fitted = getFittedvalues();
            List<Double> residuals = new ArrayList<>(fitted.size());
            for (int i = 0; i < fitted.size(); i++) {
                residuals.add(response.get(i) - fitted.get(i));
            }
            return residuals;
        }

        private double estimateSigma2(int df) {
            double ssq = Statistics.sumOfSquared(arrayFrom(this.residuals));
            return ssq / (this.residuals.size() - df);
        }

        private List<Double> getFittedvalues() {
            return listFrom(fitted.getData());
        }

        private List<Double> getResiduals() {
            return residuals;
        }

        private List<Double> getBetaEstimates() {
            return listFrom(b.getData());
        }

        private List<Double> getBetaStandardErrors(int numCols) {
            DenseMatrix64F diag = new DenseMatrix64F(numCols, 1);
            CommonOps.extractDiag(this.covarianceMatrix, diag);
            return listFrom(sqrt(diag.getData()));
        }

        private double getSigma2() {
            return this.sigma2;
        }
    }
}
