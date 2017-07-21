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
package timeseries.models.arima;

import data.Range;
import data.regression.LinearRegressionModel;
import lombok.EqualsAndHashCode;
import timeseries.models.arima.ArimaKalmanFilter.KalmanOutput;

import data.DoubleFunctions;
import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;
import math.optim.BFGS;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.EigenDecomposition;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.models.regression.TimeSeriesLinearRegressionModel;
import timeseries.operators.LagPolynomial;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.Arrays;

import static data.DoubleFunctions.combine;
import static data.DoubleFunctions.fill;
import static data.DoubleFunctions.slice;
import static math.operations.Operators.differenceOf;
import static math.operations.Operators.scale;
import static java.lang.Math.*;
import static math.stats.Statistics.sumOfSquared;

/**
 * A seasonal autoregressive integrated moving average (ARIMA) model. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
@EqualsAndHashCode
final class ArimaModel implements Arima {

    private static final double EPSILON = Math.ulp(1.0);
    private static final double DEFAULT_TOLERANCE = Math.sqrt(EPSILON);
    private final TimeSeries observations;
    private final TimeSeries differencedSeries;
    private final TimeSeries fittedSeries;
    private final TimeSeries residuals;
    private final ArimaOrder order;
    private final ModelInformation modelInfo;
    private final ArimaCoefficients arimaCoefficients;
    private final FittingStrategy fittingStrategy;
    private final int seasonalFrequency;
    private final double[] arSarCoeffs;
    private final double[] maSmaCoeffs;
    private final double[] stdErrors;

    ArimaModel(final TimeSeries observations, final ArimaOrder order, final TimePeriod seasonalCycle,
               final FittingStrategy fittingStrategy) {
        this(observations, order, seasonalCycle, fittingStrategy, null);
    }

    private ArimaModel(final TimeSeries observations, final ArimaOrder order, final TimePeriod seasonalCycle,
                       final FittingStrategy fittingStrategy, LinearRegressionModel regression) {
        this.observations = observations;
        this.order = order;
        this.fittingStrategy = fittingStrategy;
        this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
        this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);

        final Vector initParams;
        final Matrix initHessian;
        ArimaParameters parameters = ArimaParameters.initializePars(order.p, order.q, order.P, order.Q);
        Matrix regressionMatrix = getRegressionMatrix(observations.size(), order);
        if (regression == null) {
            regression = getLinearRegression(differencedSeries, regressionMatrix);
        }
        if (order.constant.include()) {
            parameters.setMean(regression.beta()[0]);
            parameters.setMeanParScale(10 * regression.standardErrors()[0]);
        }
        if (order.drift.include()) {
            parameters.setDrift(regression.beta()[order.constant.asInt()]);
            parameters.setDriftParScale(10 * regression.standardErrors()[order.constant.asInt()]);
        }
        if (fittingStrategy == FittingStrategy.CSSML) {
            final FittingStrategy subStrategy = FittingStrategy.CSS;
            final ArimaModel firstModel = new ArimaModel(observations, order, seasonalCycle, subStrategy, regression);
            double meanParScale = parameters.getMeanParScale();
            double driftParScale = parameters.getDriftParScale();
            parameters = ArimaParameters.fromCoefficients(firstModel.coefficients());
            parameters.setMeanParScale(meanParScale);
            parameters.setDriftParScale(driftParScale);
            //parameters.setMean(firstModel.coefficients().mean());
            //parameters.setDrift(firstModel.coefficients().drift());
            initParams = new Vector(parameters.getAllScaled(order));
            initHessian = getInitialHessian(firstModel);
        } else {
            initParams = new Vector(parameters.getAllScaled(order));
            initHessian = getInitialHessian(initParams.size());
        }

        final AbstractMultivariateFunction function = new OptimFunction(observations, order, parameters,
                                                                        fittingStrategy, regressionMatrix,
                                                                        seasonalFrequency);
        final BFGS optimizer = new BFGS(function, initParams, DEFAULT_TOLERANCE, DEFAULT_TOLERANCE, initHessian);
        final Vector optimizedParams = optimizer.parameters();
        final Matrix inverseHessian = optimizer.inverseHessian();

        this.stdErrors = DoubleFunctions.sqrt(scale(inverseHessian.diagonal(), 1.0 / differencedSeries.size()));
        if (order.constant.include()) {
            this.stdErrors[order.sumARMA()] *= parameters.getMeanParScale();
        }
        if (order.drift.include()) {
            this.stdErrors[order.sumARMA() + order.constant.asInt()] *= parameters.getDriftParScale();
        }

        final double[] arCoeffs = getArCoeffs(optimizedParams);
        final double[] maCoeffs = getMaCoeffs(optimizedParams);
        final double[] sarCoeffs = getSarCoeffs(optimizedParams);
        final double[] smaCoeffs = getSmaCoeffs(optimizedParams);

        this.arSarCoeffs = ArimaCoefficients.expandArCoefficients(arCoeffs, sarCoeffs, seasonalFrequency);
        this.maSmaCoeffs = ArimaCoefficients.expandMaCoefficients(maCoeffs, smaCoeffs, seasonalFrequency);
        if (order.constant.include()) {
            parameters.setAndScaleMean(optimizedParams.at(order.sumARMA()));
        }
        if (order.drift.include()) {
            parameters.setAndScaleDrift(optimizedParams.at(order.sumARMA() + order.constant.asInt()));
        }
        this.arimaCoefficients = new ArimaCoefficients(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs, order.d,
                                                       order.D, parameters.getMean(), parameters.getDrift(),
                                                       this.seasonalFrequency);
        Vector regressionParameters = Vector.from(parameters.getRegressors(order));
        Vector regressionEffects = regressionMatrix.times(regressionParameters);
        TimeSeries armaSeries = this.observations.minus(regressionEffects.elements());
        TimeSeries differencedSeries = armaSeries.difference(1, order.d).difference(seasonalFrequency, order.D);
        if (fittingStrategy == FittingStrategy.CSS) {
            this.modelInfo = fitCSS(differencedSeries, arSarCoeffs, maSmaCoeffs, order.npar());
            final double[] residuals = combine(
                    new double[order.d + order.D * seasonalFrequency], modelInfo.residuals);
            this.fittedSeries = observations.minus(TimeSeries.from(residuals));
            this.residuals = observations.minus(this.fittedSeries);
        } else {
            double[] delta = getDelta(this.order, this.seasonalFrequency);
            this.modelInfo = fitML(armaSeries, arSarCoeffs, maSmaCoeffs, delta, order.npar());
            final double[] residuals = modelInfo.residuals;
            this.fittedSeries = observations.minus(TimeSeries.from(residuals));
            this.residuals = observations.minus(this.fittedSeries);
        }
    }

    ArimaModel(final TimeSeries observations, final ArimaCoefficients coeffs, final TimePeriod seasonalCycle,
               final FittingStrategy fittingStrategy) {
        this.observations = observations;
        this.arimaCoefficients = coeffs;
        this.fittingStrategy = fittingStrategy;
        this.order = coeffs.extractModelOrder();
        this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
        this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);
        this.arSarCoeffs = ArimaCoefficients.expandArCoefficients(coeffs.arCoeffs(), coeffs.seasonalARCoeffs(),
                                                                  seasonalFrequency);
        this.maSmaCoeffs = ArimaCoefficients.expandMaCoefficients(coeffs.maCoeffs(), coeffs.seasonalMACoeffs(),
                                                                  seasonalFrequency);
        this.stdErrors = DoubleFunctions.fill(order.sumARMA() + order.constant.asInt() + order.drift.asInt(), 0.0);

        ArimaParameters parameters = ArimaParameters.fromCoefficients(coeffs);
        Matrix regressionMatrix = getRegressionMatrix(observations.size(), order);
        Vector regressionParameters = Vector.from(parameters.getRegressors(order));
        Vector regressionEffects = regressionMatrix.times(regressionParameters);
        TimeSeries armaSeries = this.observations.minus(regressionEffects.elements());
        TimeSeries differencedSeries = armaSeries.difference(1, order.d).difference(seasonalFrequency, order.D);
        if (fittingStrategy == FittingStrategy.CSS) {
            this.modelInfo = fitCSS(differencedSeries, arSarCoeffs, maSmaCoeffs, order.npar());
            final double[] residuals = combine(new double[arSarCoeffs.length], modelInfo.residuals);
            this.fittedSeries = observations.minus(TimeSeries.from(residuals));
            this.residuals = observations.minus(this.fittedSeries);
        } else {
            double[] delta = getDelta(this.order, this.seasonalFrequency);
            this.modelInfo = fitML(armaSeries, arSarCoeffs, maSmaCoeffs, delta, order.npar());
            final double[] residuals = modelInfo.residuals;
            this.fittedSeries = observations.minus(TimeSeries.from(residuals));
            this.residuals = observations.minus(this.fittedSeries);
        }
    }

    private Matrix getRegressionMatrix(int size, ArimaOrder order) {
        double[][] matrix = new double[order.numRegressors()][size];
        if (order.constant.include()) {
            matrix[0] = fill(size, 1.0);
        }
        if (order.drift.include()) {
            matrix[order.constant.asInt()] = Range.inclusiveRange(1, size).asArray();
        }
        return Matrix.create(matrix, Matrix.StorageMode.BY_COLUMM);
    }

    private Matrix getForecastRegressionMatrix(int steps, ArimaOrder order) {
        double[][] matrix = new double[order.numRegressors()][steps];
        if (order.constant.include()) {
            matrix[0] = fill(steps, 1.0);
        }
        if (order.drift.include()) {
            int startTime = this.observations.size() + 1;
            matrix[order.constant.asInt()] = Range.inclusiveRange(startTime, startTime + steps).asArray();
        }
        return Matrix.create(matrix, Matrix.StorageMode.BY_COLUMM);
    }

    private LinearRegressionModel getLinearRegression(TimeSeries differencedSeries, Matrix designMatrix) {
        double[][] diffedMatrix = new double[designMatrix.ncol()][];
        double[][] designMatrixTwoD = designMatrix.data2D(Matrix.StorageMode.BY_COLUMM);
        for (int i = 0; i < diffedMatrix.length; i++) {
            diffedMatrix[i] = TimeSeries.difference(designMatrixTwoD[i], order.d);
        }
        for (int i = 0; i < diffedMatrix.length; i++) {
            diffedMatrix[i] = TimeSeries.difference(diffedMatrix[i], seasonalFrequency, order.D);
        }
        TimeSeriesLinearRegressionModel.Builder regressionBuilder = TimeSeriesLinearRegressionModel.builder();
        regressionBuilder.response(differencedSeries);
        regressionBuilder.hasIntercept(TimeSeriesLinearRegressionModel.Intercept.EXCLUDE);
        regressionBuilder.timeTrend(TimeSeriesLinearRegressionModel.TimeTrend.EXCLUDE);
        regressionBuilder.externalRegressors(Matrix.create(diffedMatrix, Matrix.StorageMode.BY_COLUMM));
        return regressionBuilder.build();
    }

    /**
     * Fit an ARIMA model using conditional sum-of-squares.
     *
     * @param differencedSeries the time series of observations to model.
     * @param arCoeffs          the autoregressive coefficients of the model.
     * @param maCoeffs          the moving-average coefficients of the model.
     * @param npar              the order of the model to be fit.
     * @return information about the fitted model.
     */
    private static ModelInformation fitCSS(final TimeSeries differencedSeries, final double[] arCoeffs,
                                           final double[] maCoeffs, final int npar) {
        final int offset = arCoeffs.length;
        final int n = differencedSeries.size();

        final double[] fitted = new double[n];
        final double[] residuals = new double[n];

        for (int t = offset; t < fitted.length; t++) {
            //fitted[t] = mean;
            for (int i = 0; i < arCoeffs.length; i++) {
                if (abs(arCoeffs[i]) > 0.0) {
                    fitted[t] += arCoeffs[i] * differencedSeries.at(t - i - 1);
                }
            }
            for (int j = 0; j < Math.min(t, maCoeffs.length); j++) {
                if (abs(maCoeffs[j]) > 0.0) {
                    fitted[t] += maCoeffs[j] * residuals[t - j - 1];
                }
            }
            residuals[t] = differencedSeries.at(t) - fitted[t];
        }
        final int m = differencedSeries.size() - arCoeffs.length;
        final double sigma2 = sumOfSquared(residuals) / m;
        final double logLikelihood = (-n / 2.0) * (log(2 * PI * sigma2) + 1);
        return new ModelInformation(npar, sigma2, logLikelihood, residuals, fitted);
    }

    private static ModelInformation fitML(final TimeSeries observations, final double[] arCoeffs,
                                          final double[] maCoeffs, final double[] delta, int npar) {
        final double[] series = observations.asArray();
        ArimaKalmanFilter.KalmanOutput output = kalmanFit(observations, arCoeffs, maCoeffs, delta);
        final double sigma2 = output.sigma2();
        final double logLikelihood = output.logLikelihood();
        final double[] residuals = output.residuals();
        final double[] fitted = differenceOf(series, residuals);
        npar += 1; // Add 1 for the variance estimate.
        return new ModelInformation(npar, sigma2, logLikelihood, residuals, fitted);
    }

    private static KalmanOutput kalmanFit(final TimeSeries observations, final double[] arCoeffs,
                                          final double[] maCoeffs, final double[] delta) {
        final double[] series = observations.asArray();
        ArimaStateSpace ss = new ArimaStateSpace(series, arCoeffs, maCoeffs, delta);
        ArimaKalmanFilter kalmanFilter = new ArimaKalmanFilter(ss);
        return kalmanFilter.output();
    }

    static double[] getDelta(ArimaOrder order, int observationFrequency) {
        LagPolynomial differencesPolynomial = LagPolynomial.differences(order.d);
        LagPolynomial seasonalDifferencesPolynomial = LagPolynomial.seasonalDifferences(observationFrequency, order.D);

        final LagPolynomial finalPolynomial = differencesPolynomial.times(seasonalDifferencesPolynomial);
        return scale(finalPolynomial.parameters(), -1.0);
    }

    private static boolean isInvertible(double[] ma) {
        if (ma.length > 0) {
            double[] maCoeffs = new double[ma.length + 1];
            maCoeffs[0] = 1.0;
            System.arraycopy(ma, 0, maCoeffs, 1, ma.length);
            final double[] roots = roots(maCoeffs);
            for (double root : roots) {
                if (root <= 1.0) return false;
            }
            return true;
        }
        return true;
    }

    private static boolean isStationary(double[] ar) {
        if (ar.length > 0) {
            double[] arCoeffs = new double[ar.length + 1];
            arCoeffs[0] = 1.0;
            for (int i = 0; i < ar.length; i++) {
                arCoeffs[i + 1] = -ar[i];
            }
            final double[] roots = roots(arCoeffs);
            for (double root : roots) {
                if (root <= 1.0) return false;
            }
            return true;
        }
        // If ar.length == 0 then it is stationary.
        return true;
    }

    private static double[] roots(double[] arCoeffs) {
        final Complex64F[] complexRoots = findRoots(arCoeffs);
        final double[] absoluteRoots = new double[complexRoots.length];
        for (int i = 0; i < complexRoots.length; i++) {
            absoluteRoots[i] = complexRoots[i].getMagnitude();
        }
        return absoluteRoots;
    }

    // Source: https://stackoverflow.com/questions/13805644/finding-roots-of-polynomial-in-java
    private static Complex64F[] findRoots(double... coefficients) {
        int N = coefficients.length - 1;

        // Construct the companion matrix. This is a square N x N matrix.
        final DenseMatrix64F c = new DenseMatrix64F(N, N);

        double a = coefficients[N];
        for (int i = 0; i < N; i++) {
            c.set(i, N - 1, -coefficients[i] / a);
        }
        for (int i = 1; i < N; i++) {
            c.set(i, i - 1, 1);
        }

        // Use generalized eigenvalue decomposition to find the roots.
        EigenDecomposition<DenseMatrix64F> evd = DecompositionFactory.eig(N, false);

        evd.decompose(c);

        final Complex64F[] roots = new Complex64F[N];

        for (int i = 0; i < N; i++) {
            roots[i] = evd.getEigenvalue(i);
        }

        return roots;
    }

    /**
     * Compute point forecasts for the given number of steps ahead and return the result in a primitive array.
     *
     * @param steps the number of time periods ahead to forecast.
     * @return point forecasts for the given number of steps ahead.
     */
    @Override
    public double[] fcst(final int steps) {
        final int d = order.d;
        final int D = order.D;
        final int n = differencedSeries.size();
        final int m = observations.size();
        final double[] resid = this.residuals.asArray();
        final double[] diffedFcst = new double[n + steps];
        final double[] fcst = new double[m + steps];

        Vector regressionParameters = Vector.from(coefficients().getRegressors(order));
        Matrix regressionMatrix = getRegressionMatrix(this.observations.size(), this.order);
        Vector regressionEffects = regressionMatrix.times(regressionParameters);
        TimeSeries armaSeries = this.observations.minus(regressionEffects.elements());
        TimeSeries differencedSeries = armaSeries.difference(1, order.d).difference(seasonalFrequency, order.D);
        System.arraycopy(differencedSeries.asArray(), 0, diffedFcst, 0, n);
        System.arraycopy(armaSeries.asArray(), 0, fcst, 0, m);
        LagPolynomial diffPolynomial = LagPolynomial.differences(d);
        LagPolynomial seasDiffPolynomial = LagPolynomial.seasonalDifferences(seasonalFrequency, D);
        LagPolynomial lagPolynomial = diffPolynomial.times(seasDiffPolynomial);
        for (int t = 0; t < steps; t++) {
            fcst[m + t] = lagPolynomial.fit(fcst, m + t);
            for (int i = 0; i < arSarCoeffs.length; i++) {
                diffedFcst[n + t] += arSarCoeffs[i] * diffedFcst[n + t - i - 1];
                fcst[m + t] += arSarCoeffs[i] * diffedFcst[n + t - i - 1];
            }
            for (int j = maSmaCoeffs.length; j > 0 && t < j; j--) {
                diffedFcst[n + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
                fcst[m + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
            }
        }
        Matrix forecastRegressionMatrix = getForecastRegressionMatrix(steps, this.order);
        Vector forecastRegressionEffects = forecastRegressionMatrix.times(regressionParameters);
        Vector forecast = Vector.from(slice(fcst, m, m + steps));
        return forecast.plus(forecastRegressionEffects).elements();
    }

    @Override
    public TimeSeries pointForecast(final int steps) {
        final int n = observations.size();
        double[] fcst = fcst(steps);
        TimePeriod timePeriod = observations.timePeriod();
        final OffsetDateTime startTime = observations.observationTimes()
                                                     .get(n - 1)
                                                     .plus(timePeriod.periodLength() *
                                                           timePeriod.timeUnit().unitLength(),
                                                           timePeriod.timeUnit().temporalUnit());
        return TimeSeries.from(timePeriod, startTime, fcst);
    }

    @Override
    public Forecast forecast(int steps, double alpha) {
        return ArimaForecast.forecast(this, steps, alpha);
    }

    /**
     * Create a forecast with 95% prediction intervals for the given number of steps ahead.
     *
     * @param steps the number of time periods ahead to forecast.
     * @return a forecast with 95% prediction intervals for the given number of steps ahead.
     */
    @Override
    public Forecast forecast(final int steps) {
        final double defaultAlpha = 0.05;
        return forecast(steps, defaultAlpha);
    }

    /*
     * Start with the difference equation form of the model (Cryer and Chan 2008, 5.2): diffedSeries_t = ArmaProcess_t.
     * Then, subtracting diffedSeries_t from both sides, we have ArmaProcess_t - diffedSeries_t = 0. Now add Y_t to both
     * sides and rearrange terms to obtain Y_t = Y_t - diffedSeries_t + ArmaProcess_t. Thus, our in-sample estimate of
     * Y_t, the "integrated" series, is Y_t(hat) = Y_t - diffedSeries_t + fittedArmaProcess_t.
     */
    private double[] integrate(final double[] fitted) {
        final int offset = this.order.d + this.order.D * this.seasonalFrequency;
        final double[] integrated = new double[this.observations.size()];
        for (int t = 0; t < offset; t++) {
            integrated[t] = observations.at(t);
        }
        for (int t = offset; t < observations.size(); t++) {
            integrated[t] = observations.at(t) - differencedSeries.at(t - offset) + fitted[t - offset];
        }
        return integrated;
    }

    private Matrix getInitialHessian(final int n) {
        return new Matrix.IdentityBuilder(n).build();
//    if (order.constant == 1) {
//      final double meanParScale = 10 * observations.stdDeviation() / Math.sqrt(observations.n());
//      return builder.set(n - 1, n - 1, 1.0).build();
//    }
//    return builder.build();
    }

    private Matrix getInitialHessian(final ArimaModel model) {
        double[] stdErrors = model.stdErrors;
        Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(stdErrors.length);
        for (int i = 0; i < stdErrors.length; i++) {
            builder.set(i, i, stdErrors[i] * stdErrors[i] * observations.size());
        }
        return builder.build();
    }

    private double[] getInitialParameters(final ArimaParameters parameters) {
        // Set initial constant to the mean and all other parameters to zero.
        double[] initParams = new double[order.sumARMA() + order.constant.asInt() + order.drift.asInt()];
        if (order.constant.include() && abs(parameters.getMeanParScale()) > EPSILON) {
            initParams[initParams.length - 1] = parameters.getMean() / parameters.getMeanParScale();
        }
        return initParams;
    }

    private double[] getSarCoeffs(final Vector optimizedParams) {
        final double[] sarCoeffs = new double[order.P];
        for (int i = 0; i < order.P; i++) {
            sarCoeffs[i] = optimizedParams.at(i + order.p + order.q);
        }
        return sarCoeffs;
    }

    private double[] getSmaCoeffs(final Vector optimizedParams) {
        final double[] smaCoeffs = new double[order.Q];
        for (int i = 0; i < order.Q; i++) {
            smaCoeffs[i] = optimizedParams.at(i + order.p + order.q + order.P);
        }
        return smaCoeffs;
    }

    private double[] getArCoeffs(final Vector optimizedParams) {
        final double[] arCoeffs = new double[order.p];
        for (int i = 0; i < order.p; i++) {
            arCoeffs[i] = optimizedParams.at(i);
        }
        return arCoeffs;
    }

    private double[] getMaCoeffs(final Vector optimizedParams) {
        final double[] arCoeffs = new double[order.q];
        for (int i = 0; i < order.q; i++) {
            arCoeffs[i] = optimizedParams.at(i + order.p);
        }
        return arCoeffs;
    }

    @Override
    public TimeSeries timeSeries() {
        return this.observations;
    }

    @Override
    public TimeSeries fittedSeries() {
        return this.fittedSeries;
    }

    @Override
    public TimeSeries residuals() {
        return this.residuals;
    }

    /**
     * Get the maximum-likelihood estimate of the model variance, equal to the sum of squared residuals divided by the
     * number of observations.
     *
     * @return the maximum-likelihood estimate of the model variance, equal to the sum of squared residuals divided
     * by the number of observations.
     */
    @Override
    public double sigma2() {
        return modelInfo.sigma2;
    }

    @Override
    public int seasonalFrequency() {
        return this.seasonalFrequency;
    }

    @Override
    public double[] stdErrors() {
        return this.stdErrors.clone();
    }

    @Override
    public ArimaCoefficients coefficients() {
        return this.arimaCoefficients;
    }

    @Override
    public ArimaOrder order() {
        return this.order;
    }

    @Override
    public double logLikelihood() {
        return modelInfo.logLikelihood;
    }

    @Override
    public double aic() {
        return modelInfo.aic;
    }

    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        return newLine + order + newLine + modelInfo + newLine + arimaCoefficients +
               newLine + newLine + "fit using " + fittingStrategy;
    }

    /**
     * A numerical description of an ARIMA model.
     *
     * @author Jacob Rachiele
     */
    static class ModelInformation {
        private final double sigma2;
        private final double logLikelihood;
        private final double aic;
        private final double[] residuals;
        private final double[] fitted;

        /**
         * Create new model information with the given data.
         *
         * @param npar          the number of parameters estimated in the model.
         * @param sigma2        an estimate of the model variance.
         * @param logLikelihood the natural logarithms of the likelihood of the model parameters.
         * @param residuals     the difference between the observations and the fitted values.
         * @param fitted        the values fitted by the model to the data.
         */
        ModelInformation(final int npar, final double sigma2, final double logLikelihood,
                         final double[] residuals, final double[] fitted) {
            this.sigma2 = sigma2;
            this.logLikelihood = logLikelihood;
            this.aic = 2 * npar - 2 * logLikelihood;
            this.residuals = residuals.clone();
            this.fitted = fitted.clone();
        }

        @Override
        public String toString() {
            String newLine = System.lineSeparator();
            NumberFormat numFormatter = new DecimalFormat("#0.0000");
            return newLine + "sigma2: " + numFormatter.format(sigma2) + newLine + "logLikelihood: " +
                   numFormatter.format(logLikelihood) + newLine + "AIC: " + numFormatter.format(aic);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ModelInformation that = (ModelInformation) o;

            if (Double.compare(that.sigma2, sigma2) != 0) return false;
            if (Double.compare(that.logLikelihood, logLikelihood) != 0) return false;
            if (Double.compare(that.aic, aic) != 0) return false;
            if (!Arrays.equals(residuals, that.residuals)) return false;
            return Arrays.equals(fitted, that.fitted);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(sigma2);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(logLikelihood);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(aic);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + Arrays.hashCode(residuals);
            result = 31 * result + Arrays.hashCode(fitted);
            return result;
        }
    }

    private static class OptimFunction extends AbstractMultivariateFunction {

        private final TimeSeries observations;
        private final ArimaOrder order;
        private final ArimaParameters parameters;
        private final FittingStrategy fittingStrategy;
        private final int seasonalFrequency;
        private final Matrix externalRegressors;

        private OptimFunction(TimeSeries observations, ArimaOrder order, ArimaParameters parameters,
                              FittingStrategy fittingStrategy, Matrix externalRegressors, int seasonalFrequency) {
            this.observations = observations;
            this.order = order;
            this.parameters = parameters;
            this.fittingStrategy = fittingStrategy;
            this.externalRegressors = externalRegressors;
            this.seasonalFrequency = seasonalFrequency;
        }

        @Override
        public final double at(final Vector point) {
            functionEvaluations++;

            final double[] params = point.elements();
            parameters.setAutoRegressivePars(slice(params, 0, order.p));
            parameters.setMovingAveragePars(slice(params, order.p, order.p + order.q));
            parameters.setSeasonalAutoRegressivePars(slice(params, order.p + order.q, order.p + order.q + order.P));
            parameters.setSeasonalMovingAveragePars(slice(params, order.p + order.q + order.P, order.p + order.q +
                                                         order.P + order.Q));

            if (order.constant.include()) {
                parameters.setAndScaleMean(params[order.sumARMA()]);
            }
            if (order.drift.include()) {
                parameters.setAndScaleDrift(params[order.sumARMA() + order.constant.asInt()]);
            }
            final double[] arCoeffs = ArimaCoefficients.expandArCoefficients(parameters.getAutoRegressivePars(),
                                                                             parameters.getSeasonalAutoRegressivePars(),
                                                                             seasonalFrequency);
            final double[] maCoeffs = ArimaCoefficients.expandMaCoefficients(parameters.getMovingAveragePars(),
                                                                             parameters.getSeasonalMovingAveragePars(),
                                                                             seasonalFrequency);

            Vector regressionParameters = Vector.from(parameters.getRegressors(order));
            Vector regressionEffects = externalRegressors.times(regressionParameters);
            TimeSeries armaSeries = this.observations.minus(regressionEffects.elements());

            if (fittingStrategy == FittingStrategy.ML || fittingStrategy == FittingStrategy.CSSML) {
                double[] delta = getDelta(this.order, this.seasonalFrequency);
                ArimaKalmanFilter.KalmanOutput output = ArimaModel.kalmanFit(armaSeries, arCoeffs, maCoeffs, delta);
                return 0.5 * (log(output.sigma2()) + output.sumLog() / output.n());
            }

            TimeSeries differencedSeries = armaSeries.difference(1, order.d).difference(seasonalFrequency, order.D);
            final ModelInformation info = ArimaModel.fitCSS(differencedSeries, arCoeffs, maCoeffs, order.npar());
            return 0.5 * log(info.sigma2);
        }

        @Override
        public String toString() {
            String newLine = System.lineSeparator();
            return order + newLine + "fittingStrategy: " + fittingStrategy + newLine + "seasonalFrequency: " +
                   seasonalFrequency + newLine + "parameters" + parameters;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OptimFunction that = (OptimFunction) o;

            if (seasonalFrequency != that.seasonalFrequency) return false;
            if (!observations.equals(that.observations)) return false;
            if (!order.equals(that.order)) return false;
            if (fittingStrategy != that.fittingStrategy) return false;
            return (parameters == that.parameters);
        }

        @Override
        public int hashCode() {
            int result;
            result = observations.hashCode();
            result = 31 * result + order.hashCode();
            result = 31 * result + fittingStrategy.hashCode();
            result = 31 * result + seasonalFrequency;
            result = 31 * result + parameters.hashCode();
            return result;
        }
    }

}
