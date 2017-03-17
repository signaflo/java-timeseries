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

import static data.DoubleFunctions.*;

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

/**
 * Linear regression with multiple predictors. This class is immutable and thread-safe.
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
        this.predictors = builder.predictors;
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
        private List<List<Double>> predictors;
        private List<Double> response;
        private boolean hasIntercept = true;

        /**
         * Copy the attributes of the given regression object to this builder and return this builder.
         *
         * @param regression the object to copy the attributes from.
         * @return this builder.
         */
        public final Builder from(LinearRegression regression) {
            this.predictors = ImmutableList.copyOf(regression.predictors());
            this.response = ImmutableList.copyOf(regression.response());
            this.hasIntercept = regression.hasIntercept();
            return this;
        }

        Builder predictors(List<List<Double>> predictors) {
            this.predictors = ImmutableList.copyOf(predictors);
            return this;
        }

        public Builder predictor(List<Double> predictor) {
            this.predictors = ImmutableList.of(predictor);
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

        public LinearRegression build() {
            return new MultipleLinearRegression(this);
        }
    }

    private class MatrixFormulation {

        private final DenseMatrix64F A;
        private final DenseMatrix64F At;
        private final DenseMatrix64F AtAInv;
        private final DenseMatrix64F b;
        private final DenseMatrix64F y;
        private final D1Matrix64F fitted;
        private final List<Double> residuals;
        private final double sigma2;
        private final DenseMatrix64F covarianceMatrix;

        MatrixFormulation() {
            int numRows = response.size();
            int numCols = predictors.size() + ((hasIntercept)? 1 : 0);
            this.A = createMatrixA(numRows, numCols);
            this.At = new DenseMatrix64F(numCols, numRows);
            CommonOps.transpose(A, At);
            this.AtAInv = new DenseMatrix64F(numCols, numCols);
            this.b = new DenseMatrix64F(numCols, 1);
            this.y = new DenseMatrix64F(numRows, 1);
            solveAtA(numRows, numCols);
            solveForB(numRows, numCols);
            this.fitted = computeFittedValues();
            this.residuals = computeResiduals();
            this.sigma2 = estimateSigma2(numCols);
            this.covarianceMatrix = new DenseMatrix64F(numCols, numCols);
            CommonOps.scale(sigma2, AtAInv, covarianceMatrix);
        }

        private void solveForB(int numRows, int numCols) {
            DenseMatrix64F AtAInvAt = new DenseMatrix64F(numCols, numRows);
            CommonOps.mult(AtAInv, At, AtAInvAt);
            y.setData(arrayFrom(response));
            MatrixVectorMult.mult(AtAInvAt, y, b);
        }

        private void solveAtA(int numRows, int numCols) {
            LinearSolver<DenseMatrix64F> solver = LinearSolverFactory.qr(numRows, numCols);
            QRDecomposition<DenseMatrix64F> decomposition = solver.getDecomposition();
            solver.setA(A);
            y.setData(arrayFrom(response));
            solver.solve(this.y, this.b);
            DenseMatrix64F R = decomposition.getR(null, true);
            solver = LinearSolverFactory.linear(numCols);
            solver.setA(R);
            DenseMatrix64F Rinv = new DenseMatrix64F(numCols, numCols);
            solver.invert(Rinv);
            CommonOps.multOuter(Rinv, this.AtAInv);
        }

        private DenseMatrix64F createMatrixA(int numRows, int numCols) {
            double[] data;
            if (hasIntercept) {
                data = fill(numRows, 1.0);
            } else {
                data = arrayFrom();
            }
            for (List<Double> predictor : predictors) {
                data = combine(data, arrayFrom(predictor));
            }
            return new DenseMatrix64F(numRows, numCols, false, data);
        }

        private D1Matrix64F computeFittedValues() {
            D1Matrix64F fitted = new DenseMatrix64F(response.size(), 1);
            MatrixVectorMult.mult(A, b, fitted);
            return fitted;
        }

        private List<Double> computeResiduals() {
            List<Double> fitted = getFittedvalues();
            List<Double> resid = new ArrayList<>(fitted.size());
            for (int i = 0; i < fitted.size(); i++) {
                resid.add(response.get(i) - fitted.get(i));
            }
            return resid;
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
