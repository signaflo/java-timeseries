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
package linear.regression.primitive;

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

import static data.DoubleFunctions.*;

/**
 * A linear regression model using primitive data types, with support for both single and multiple prediction variables.
 * This implementation is immutable and thread-safe.
 */
@EqualsAndHashCode @ToString
public final class MultipleLinearRegression implements LinearRegression {

    private final double[][] predictors;
    private final double[] response;
    private final double[] beta;
    private final double[] standardErrors;
    private final double[] residuals;
    private final double[] fitted;
    private final boolean hasIntercept;
    private final double sigma2;

    private MultipleLinearRegression(Builder builder) {
        this.predictors = builder.predictors;
        this.response = builder.response;
        this.hasIntercept = builder.hasIntercept;
        MatrixFormulation matrixFormulation = new MatrixFormulation();
        this.beta = matrixFormulation.getBetaEstimates();
        this.standardErrors = matrixFormulation.getBetaStandardErrors(this.beta.length);
        this.fitted = matrixFormulation.computeFittedValues();
        this.residuals = matrixFormulation.getResiduals();
        this.sigma2 = matrixFormulation.getSigma2();
    }

    @Override
    public double[][] predictors() {
        double[][] copy = new double[this.predictors.length][];
        for (int i = 0; i < copy.length; i++) {
            copy[i] = this.predictors[i].clone();
        }
        return copy;
    }

    @Override
    public double[] beta() {
        return beta;
    }

    @Override
    public double[] standardErrors() {
        return this.standardErrors.clone();
    }

    @Override
    public double sigma2() {
        return this.sigma2;
    }

    @Override
    public double[] response() {
        return this.response.clone();
    }

    @Override
    public double[] fitted() {
        return fitted.clone();
    }

    @Override
    public double[] residuals() {
        return residuals.clone();
    }

    @Override
    public boolean hasIntercept() {
        return this.hasIntercept;
    }

    /**
     * Create a new linear regression model from this one, using the given boolean to determine whether
     * to fit an intercept or not.
     *
     * @param hasIntercept whether or not the new regression should have an intercept.
     * @return a new linear regression model using the given boolean to determine whether to fit an intercept.
     */
    public MultipleLinearRegression withHasIntercept(boolean hasIntercept) {
        return new Builder().from(this).hasIntercept(hasIntercept).build();
    }

    /**
     * Create a new linear regression model from this one, replacing the current response with the provided one.
     *
     * @param response the response variable of the new regression.
     * @return a new linear regression model with the given response variable in place of the current one.
     */
    public MultipleLinearRegression withResponse(double[] response) {
        return new Builder().from(this).response(response).build();
    }

    /**
     * Create a new linear regression model from this one, with the given predictors fully replacing the current ones.
     *
     * @param predictors The new array of prediction variables to use for the regression.
     * @return a new linear regression model using the given predictors in place of the current ones.
     */
    public MultipleLinearRegression withPredictors(double[]... predictors) {
        return new Builder().from(this).predictors(predictors).build();
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
     * A builder for a primitive multiple linear regression model.
     */
    public static final class Builder {

        private double[][] predictors;
        private double[] response;
        private boolean hasIntercept = true;

        /**
         * Copy the attributes of the given regression object to this builder and return this builder.
         *
         * @param regression the object to copy the attributes from.
         * @return this builder.
         */
        public final Builder from(LinearRegression regression) {
            this.predictors = regression.predictors().clone();
            this.response = regression.response().clone();
            this.hasIntercept = regression.hasIntercept();
            return this;
        }

        public Builder predictors(double[]... predictors) {
            this.predictors = new double[predictors.length][];
            for (int i = 0; i < predictors.length; i++) {
                this.predictors[i] = predictors[i].clone();
            }
            return this;
        }

        public Builder response(double[] response) {
            this.response = response.clone();
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
        private final double[] fitted;
        private final double[] residuals;
        private final double sigma2;
        private final DenseMatrix64F covarianceMatrix;

        MatrixFormulation() {
            int numRows = response.length;
            int numCols = predictors.length + ((hasIntercept) ? 1 : 0);
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
            y.setData(response);
            qrSolver.solve(this.y, this.b);
            DenseMatrix64F R = decomposition.getR(null, true);
            LinearSolver<DenseMatrix64F> linearSolver = LinearSolverFactory.linear(numCols);
            linearSolver.setA(R);
            DenseMatrix64F Rinverse = new DenseMatrix64F(numCols, numCols);
            linearSolver.invert(Rinverse); // stores solver's solution inside of Rinverse.
            CommonOps.multOuter(Rinverse, this.AtAInv);
        }

        private DenseMatrix64F createMatrixA(int numRows, int numCols) {
            double[] data;
            if (hasIntercept) {
                data = fill(numRows, 1.0);
            } else {
                data = arrayFrom();
            }
            for (double[] predictor : predictors) {
                data = combine(data, arrayFrom(predictor));
            }
            boolean isRowMajor = false;
            return new DenseMatrix64F(numRows, numCols, isRowMajor, data);
        }

        private double[] computeFittedValues() {
            D1Matrix64F fitted = new DenseMatrix64F(response.length, 1);
            MatrixVectorMult.mult(A, b, fitted);
            return fitted.getData();
        }

        private double[] computeResiduals() {
            double[] resid = new double[fitted.length];
            for (int i = 0; i < resid.length; i++) {
                resid[i] = (response[i] - fitted[i]);
            }
            return resid;
        }

        private double[] getResiduals() {
            return this.residuals.clone();
        }

        private double estimateSigma2(int df) {
            double ssq = Statistics.sumOfSquared(arrayFrom(this.residuals));
            return ssq / (this.residuals.length - df);
        }

        private double[] getBetaStandardErrors(int numCols) {
            DenseMatrix64F diag = new DenseMatrix64F(numCols, 1);
            CommonOps.extractDiag(this.covarianceMatrix, diag);
            return sqrt(diag.getData());
        }

        private double[] getBetaEstimates() {
            return b.getData().clone();
        }

        private double getSigma2() {
            return this.sigma2;
        }
    }
}
