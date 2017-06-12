/*
 * Copyright (c) 2016 Jacob Rachiele
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

import data.DoubleFunctions;
import data.operations.Operators;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;
import optim.BFGS;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.models.Model;
import timeseries.operators.LagPolynomial;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static data.DoubleFunctions.*;
import static data.operations.Operators.differenceOf;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.log;
import static stats.Statistics.sumOfSquared;

/**
 * A seasonal autoregressive integrated moving average (ARIMA) model. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class Arima implements Model {

    public enum Constant {
        INCLUDE,
        EXCLUDE
    }

    private static final double EPSILON = Math.ulp(1.0);
    private static final double DEFAULT_TOLERANCE = Math.sqrt(EPSILON);

    private final TimeSeries observations;
    private final TimeSeries differencedSeries;
    private final TimeSeries fittedSeries;
    private final TimeSeries residuals;
    private final int seasonalFrequency;

    private final ModelOrder order;
    private final ModelInformation modelInfo;
    private final ModelCoefficients modelCoefficients;
    private final FittingStrategy fittingStrategy;

    private final double mean;
    private final double[] arSarCoeffs;
    private final double[] maSmaCoeffs;
    private final double[] stdErrors;

    private Arima(final TimeSeries observations, final ModelOrder order, final TimePeriod seasonalCycle,
                  final FittingStrategy fittingStrategy) {
        this.observations = observations;
        this.order = order;
        this.fittingStrategy = fittingStrategy;
        this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
        this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);

        final double meanParScale = (order.constant == 1) ? 10 * differencedSeries.stdDeviation() /
                                                            Math.sqrt(differencedSeries.size()) : 1.0;
        final Vector initParams;
        final Matrix initHessian;

        if (fittingStrategy == FittingStrategy.CSSML || fittingStrategy == FittingStrategy.USSML) {
            final FittingStrategy subStrategy = (fittingStrategy == FittingStrategy.CSSML)
                                                ? FittingStrategy.CSS
                                                : FittingStrategy.USS;
            final Arima firstModel = new Arima(observations, order, seasonalCycle, subStrategy);
            initParams = new Vector(firstModel.coefficients().getAllCoeffs());
            initHessian = getInitialHessian(firstModel);
        } else {
            initParams = new Vector(getInitialParameters(meanParScale));
            initHessian = getInitialHessian(initParams.size());
        }

        final AbstractMultivariateFunction function = new OptimFunction(differencedSeries, order, fittingStrategy,
                                                                        seasonalFrequency, meanParScale);
        final BFGS optimizer = new BFGS(function, initParams, DEFAULT_TOLERANCE, DEFAULT_TOLERANCE, initHessian);
        final Vector optimizedParams = optimizer.parameters();
        final Matrix inverseHessian = optimizer.inverseHessian();

        this.stdErrors = DoubleFunctions.sqrt(Operators.scale(inverseHessian.diagonal(), 1.0 / differencedSeries.size()));
        if (order.constant == 1) {
            this.stdErrors[this.stdErrors.length - 1] *= meanParScale;
        }

        final double[] arCoeffs = getArCoeffs(optimizedParams);
        final double[] maCoeffs = getMaCoeffs(optimizedParams);
        final double[] sarCoeffs = getSarCoeffs(optimizedParams);
        final double[] smaCoeffs = getSmaCoeffs(optimizedParams);

        this.arSarCoeffs = expandArCoefficients(arCoeffs, sarCoeffs, seasonalFrequency);
        this.maSmaCoeffs = expandMaCoefficients(maCoeffs, smaCoeffs, seasonalFrequency);
        this.mean = (order.constant == 1)
                    ? meanParScale * optimizedParams.at(order.p + order.q + order.P + order.Q)
                    : 0.0;
        this.modelCoefficients = new ModelCoefficients(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs, order.d, order.D,
                                                       this.mean);

        if (fittingStrategy == FittingStrategy.CSS) {
            this.modelInfo = fitCSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order.npar());
            final double[] residuals = modelInfo.residuals;
            final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
            this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
            this.residuals = this.observations.minus(this.fittedSeries);
        } else if (fittingStrategy == FittingStrategy.USS) {
            this.modelInfo = fitUSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order.npar());
            final double[] residuals = slice(modelInfo.residuals,
                                             modelInfo.residuals.length - observations.size(),
                                             modelInfo.residuals.length);
            this.residuals = new TimeSeries(observations.timePeriod(), observations.observationTimes(), residuals);
            this.fittedSeries = this.observations.minus(this.residuals);
        } else {
            this.modelInfo = fitML(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order.npar());
            final double[] residuals = modelInfo.residuals;
            final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
            this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
            this.residuals = this.observations.minus(this.fittedSeries);
        }
    }

    private Arima(final TimeSeries observations, final ModelCoefficients coeffs, final TimePeriod seasonalCycle,
                  final FittingStrategy fittingStrategy) {
        this.observations = observations;
        this.modelCoefficients = coeffs;
        this.fittingStrategy = fittingStrategy;
        this.order = coeffs.extractModelOrder();
        this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
        this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);
        this.arSarCoeffs = expandArCoefficients(coeffs.arCoeffs(), coeffs.seasonalARCoeffs(), seasonalFrequency);
        this.maSmaCoeffs = expandMaCoefficients(coeffs.maCoeffs(), coeffs.seasonalMACoeffs(), seasonalFrequency);
        this.mean = coeffs.mean();
        this.stdErrors = DoubleFunctions.fill(order.sumARMA() + order.constant, Double.POSITIVE_INFINITY);

        if (fittingStrategy == FittingStrategy.CSS) {
            this.modelInfo = fitCSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order.npar());
            final double[] residuals = modelInfo.residuals;
            final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
            this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
            this.residuals = this.observations.minus(this.fittedSeries);
        } else if (fittingStrategy == FittingStrategy.USS) {
            this.modelInfo = fitUSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order.npar());
            final double[] residuals = slice(modelInfo.residuals,
                                             modelInfo.residuals.length - observations.size(),
                                             modelInfo.residuals.length);
            this.residuals = new TimeSeries(observations.timePeriod(), observations.observationTimes(), residuals);
            this.fittedSeries = this.observations.minus(this.residuals);
        } else {
            this.modelInfo = fitML(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order.npar());
            final double[] residuals = modelInfo.residuals;
            final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
            this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
            this.residuals = this.observations.minus(this.fittedSeries);
        }
    }

    /**
     * Create a new ARIMA model from the given observations and model order. This constructor sets the
     * model {@link FittingStrategy} to unconditional sum-of-squares and the seasonal cycle to one year.
     *
     * @param observations the time series of observations.
     * @param order        the order of the ARIMA model.
     * @return a new ARIMA model from the given observations and model order.
     */
    public static Arima model(final TimeSeries observations, final ModelOrder order) {
        return new Arima(observations, order, TimePeriod.oneYear(), FittingStrategy.USS);
    }

    /**
     * Create a new ARIMA model from the given observations, model order, and seasonal cycle. This method sets the
     * model {@link FittingStrategy} to unconditional sum-of-squares.
     *
     * @param observations  the time series of observations.
     * @param order         the order of the ARIMA model.
     * @param seasonalCycle the amount of time it takes for the seasonal pattern to complete one cycle. For example,
     *                      monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
     *                      However, a seasonal cycle may be an arbitrary amount of time.
     * @return a new ARIMA model from the given observations, model order, and seasonal cycle.
     */
    public static Arima model(final TimeSeries observations, final ModelOrder order, final TimePeriod seasonalCycle) {
        return new Arima(observations, order, seasonalCycle, FittingStrategy.USS);
    }

    /**
     * Create a new ARIMA model from the given observations, model order, and fitting strategy. This method sets the
     * seasonal cycle to one year.
     *
     * @param observations    the time series of observations.
     * @param order           the order of the ARIMA model.
     * @param fittingStrategy the strategy to use to fit the model to the data. Maximum-likelihood estimates are
     *                        typically preferred for greater precision and accuracy, but take longer to obtain than
     *                        conditional or unconditional sum-of-squares estimates. Unconditional sum-of-squares tends
     *                        to be more accurate, more precise, and quicker to obtain than conditional sum-of-squares.
     * @return a new ARIMA model from the given observations, model order, and fitting strategy.
     */
    public static Arima model(final TimeSeries observations, final ModelOrder order,
                              final FittingStrategy fittingStrategy) {
        return new Arima(observations, order, TimePeriod.oneYear(), fittingStrategy);
    }

    /**
     * Create a new ARIMA model from the given observations, model order, seasonal cycle, and fitting strategy.
     *
     * @param observations    the time series of observations.
     * @param order           the order of the ARIMA model.
     * @param seasonalCycle   the amount of time it takes for the seasonal pattern to complete one cycle. For example,
     *                        monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
     *                        However, a seasonal cycle may be an arbitrary amount of time.
     * @param fittingStrategy the strategy to use to fit the model to the data. Maximum-likelihood estimates are
     *                        typically preferred for greater precision and accuracy, but take longer to obtain than
     *                        conditional or unconditional sum-of-squares estimates. Unconditional sum-of-squares tends
     *                        to be more accurate, more precise, and quicker to obtain than conditional sum-of-squares.
     * @return a new ARIMA model from the given observations, model order, seasonal cycle, and fitting strategy.
     */
    public static Arima model(final TimeSeries observations, final ModelOrder order, final TimePeriod seasonalCycle,
                              final FittingStrategy fittingStrategy) {
        return new Arima(observations, order, seasonalCycle, fittingStrategy);
    }

    /**
     * Create a new ARIMA model from the given observations, model coefficients, and fitting strategy. This constructor
     * sets the seasonal cycle to one year.
     *
     * @param observations    the time series of observations.
     * @param coeffs          the coefficients of the model.
     * @param fittingStrategy the strategy to use to fit the model to the data. Maximum-likelihood estimates are
     *                        typically preferred for greater precision and accuracy, but take longer to obtain than
     *                        conditional or unconditional sum-of-squares estimates. Unconditional sum-of-squares tends
     *                        to be more accurate, more precise, and quicker to obtain than conditional sum-of-squares.
     * @return a new ARIMA model from the given observations, model coefficients, and fitting strategy.
     */
    public static Arima model(final TimeSeries observations, final ModelCoefficients coeffs,
                              final FittingStrategy fittingStrategy) {
        return new Arima(observations, coeffs, TimePeriod.oneYear(), fittingStrategy);
    }

    /**
     * Create a new ARIMA model from the given observations, model coefficients, and seasonal cycle. This constructor
     * sets
     * the model {@link FittingStrategy} to unconditional sum-of-squares.
     *
     * @param observations  the time series of observations.
     * @param coeffs        the coefficients of the model.
     * @param seasonalCycle the amount of time it takes for the seasonal pattern to complete one cycle. For example,
     *                      monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
     *                      However, a seasonal cycle may be an arbitrary amount of time.
     * @return a new ARIMA model from the given observations, model coefficients, and seasonal cycle.
     */
    public static Arima model(final TimeSeries observations, final ModelCoefficients coeffs,
                              final TimePeriod seasonalCycle) {
        return new Arima(observations, coeffs, seasonalCycle, FittingStrategy.USS);
    }

    /**
     * Create a new ARIMA model from the given observations, model coefficients, seasonal cycle, and fitting strategy.
     *
     * @param observations    the time series of observations.
     * @param coeffs          the coefficients of the model.
     * @param seasonalCycle   the amount of time it takes for the seasonal pattern to complete one cycle. For example,
     *                        monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
     *                        However, a seasonal cycle may be an arbitrary amount of time.
     * @param fittingStrategy the strategy to use to fit the model to the data. Maximum-likelihood estimates are
     *                        typically preferred for greater precision and accuracy, but take longer to obtain than
     *                        conditional or unconditional sum-of-squares estimates. Unconditional sum-of-squares tends
     *                        to be more accurate, more precise, and quicker to obtain than conditional sum-of-squares.
     * @return a new ARIMA model from the given observations, model coefficients, seasonal cycle, and fitting strategy.
     */
    public static Arima model(final TimeSeries observations, final ModelCoefficients coeffs,
                              final TimePeriod seasonalCycle, final FittingStrategy fittingStrategy) {
        return new Arima(observations, coeffs, seasonalCycle, fittingStrategy);
    }

    /**
     * Fit an ARIMA model using conditional sum-of-squares.
     *
     * @param differencedSeries the time series of observations to model.
     * @param arCoeffs          the autoregressive coefficients of the model.
     * @param maCoeffs          the moving-average coefficients of the model.
     * @param mean              the model mean.
     * @param npar             the order of the model to be fit.
     * @return information about the fitted model.
     */
    private static ModelInformation fitCSS(final TimeSeries differencedSeries, final double[] arCoeffs,
                                           final double[] maCoeffs, final double mean, final int npar) {
        final int offset = arCoeffs.length;
        final int n = differencedSeries.size();

        final double[] fitted = new double[n];
        final double[] residuals = new double[n];

        for (int t = offset; t < fitted.length; t++) {
            fitted[t] = mean;
            for (int i = 0; i < arCoeffs.length; i++) {
                if (abs(arCoeffs[i]) > 0.0) {
                    fitted[t] += arCoeffs[i] * (differencedSeries.at(t - i - 1) - mean);
                }
            }
            for (int j = 0; j < Math.min(t, maCoeffs.length); j++) {
                if (abs(maCoeffs[j]) > 0.0) {
                    fitted[t] += maCoeffs[j] * residuals[t - j - 1];
                }
            }
            residuals[t] = differencedSeries.at(t) - fitted[t];
        }
        final int m = differencedSeries.size() - npar;
        final double sigma2 = sumOfSquared(residuals) / m;
        final double logLikelihood = (-n / 2.0) * (log(2 * PI * sigma2) + 1);
        return new ModelInformation(npar, sigma2, logLikelihood, residuals, fitted);
    }

    /**
     * Fit the model using unconditional sum-of-squares, utilizing back-forecasting to estimate the residuals for the
     * first few observations. This method, compared to conditional sum-of-squares, often gives estimates much closer to
     * those obtained from maximum-likelihood fitting, especially for shorter series.
     *
     * @param differencedSeries the time series of observations to model.
     * @param arCoeffs          the autoregressive coefficients of the model.
     * @param maCoeffs          the moving-average coefficients of the model.
     * @param mean              the model mean.
     * @param npar             the order of the model to be fit.
     * @return information about the fitted model.
     */
    private static ModelInformation fitUSS(final TimeSeries differencedSeries, final double[] arCoeffs,
                                           final double[] maCoeffs, final double mean, final int npar) {
        final int offset = arCoeffs.length;

        final List<Double> backwardSeries = reverseArrayToList(differencedSeries.asArray());
        final List<Double> backwardFit = new ArrayList<>(backwardSeries.size());
        final List<Double> backwardResiduals = new ArrayList<>(backwardSeries.size());

        for (int t = 0; t < offset; t++) {
            backwardFit.add(0.0);
            backwardResiduals.add(0.0);
        }

        for (int t = offset; t < backwardSeries.size(); t++) {
            backwardFit.add(t, mean);
            for (int i = 0; i < arCoeffs.length; i++) {
                if (abs(arCoeffs[i]) > 0.0) {
                    double x = backwardFit.get(t);
                    backwardFit.set(t, x + arCoeffs[i] * (backwardSeries.get(t - i - 1) - mean));
                }
            }
            for (int j = 0; j < Math.min(t, maCoeffs.length); j++) {
                if (abs(maCoeffs[j]) > 0.0) {
                    double x = backwardFit.get(t);
                    backwardFit.set(t, x + maCoeffs[j] * backwardResiduals.get(t - j - 1));
                }
            }
            double x = backwardFit.get(t);
            backwardResiduals.add(t, backwardSeries.get(t) - x);
        }

        int n = backwardSeries.size();
        int t = n;
        double epsilon = 0.005;
        while (!isIncrementSufficientlySmall(backwardSeries, t - 1, epsilon) && t < 2 * n) {
            backwardSeries.add(mean);
            for (int i = 0; i < arCoeffs.length; i++) {
                if (abs(arCoeffs[i]) > 0.0) {
                    double x = backwardSeries.get(t);
                    backwardSeries.set(t, x + arCoeffs[i] * backwardSeries.get(t - i - 1) - mean);
                }
            }
            int currentStepSize = t - n;
            for (int j = currentStepSize; j < Math.min(t, maCoeffs.length); j++) {
                if (abs(maCoeffs[j]) > 0.0) {
                    double x = backwardSeries.get(t);
                    backwardSeries.set(t, x + maCoeffs[j] * backwardResiduals.get(t - j - 1));
                }
            }
            t++;
        }

        final int extendedLength = backwardSeries.size();
        final double[] forwardFit = new double[extendedLength];
        final double[] forwardSeries = reverseListToArray(backwardSeries);
        final double[] forwardResiduals = new double[extendedLength];

        for (t = offset; t < forwardSeries.length; t++) {
            forwardFit[t] = mean;
            for (int i = 0; i < arCoeffs.length; i++) {
                if (abs(arCoeffs[i]) > 0) {
                    forwardFit[t] += arCoeffs[i] * (forwardSeries[t - i - 1] - mean);
                }
            }
            for (int j = 0; j < Math.min(t, maCoeffs.length); j++) {
                if (abs(maCoeffs[j]) > 0) {
                    forwardFit[t] += maCoeffs[j] * forwardResiduals[t - j - 1];
                }
            }
            forwardResiduals[t] = forwardSeries[t] - forwardFit[t];
        }

        n = differencedSeries.size();
        final double sigma2 = sumOfSquared(forwardResiduals) / (n);
        final double logLikelihood = (-n / 2.0) * (log(2 * PI * sigma2) + 1);
        return new ModelInformation(npar, sigma2, logLikelihood, forwardResiduals, forwardFit);
    }

    private static boolean isIncrementSufficientlySmall(List<Double> series, int t, double epsilon) {
        return backcastIncrement(series, t) < epsilon;
    }

    private static double backcastIncrement(List<Double> series, int t) {
        return abs(series.get(t) - series.get(t - 1));
    }

    private static ModelInformation fitML(final TimeSeries differencedSeries, final double[] arCoeffs,
                                          final double[] maCoeffs, final double mean, int npar) {
        final double[] series = Operators.subtract(differencedSeries.asArray(), mean);
        ArmaKalmanFilter.KalmanOutput output = kalmanFit(differencedSeries, arCoeffs, maCoeffs, mean);
        final double sigma2 = output.sigma2();
        final double logLikelihood = output.logLikelihood();
        final double[] residuals = output.residuals();
        final double[] fitted = differenceOf(series, residuals);
        npar += 1; // Add 1 for the variance estimate.
        return new ModelInformation(npar, sigma2, logLikelihood, residuals, fitted);
    }

    private static ArmaKalmanFilter.KalmanOutput kalmanFit(final TimeSeries differencedSeries, final double[] arCoeffs,
                                                           final double[] maCoeffs, final double mean) {
        final double[] series = Operators.subtract(differencedSeries.asArray(), mean);
        StateSpaceARMA ss = new StateSpaceARMA(series, arCoeffs, maCoeffs);
        ArmaKalmanFilter kalmanFilter = new ArmaKalmanFilter(ss);
        return kalmanFilter.output();
    }

    // Expand the autoregressive coefficients by combining the non-seasonal and seasonal coefficients into a single
    // array, which takes advantage of the fact that a seasonal AR model is a special case of a non-seasonal
    // AR model with zero coefficients at the non-seasonal indices.
    static double[] expandArCoefficients(final double[] arCoeffs, final double[] sarCoeffs,
                                                 final int seasonalFrequency) {
        double[] arSarCoeffs = new double[arCoeffs.length + sarCoeffs.length * seasonalFrequency];

        System.arraycopy(arCoeffs, 0, arSarCoeffs, 0, arCoeffs.length);

        // Note that we take into account the interaction between the seasonal and non-seasonal coefficients,
        // which arises because the model's ar and sar polynomials are multiplied together.
        for (int i = 0; i < sarCoeffs.length; i++) {
            arSarCoeffs[(i + 1) * seasonalFrequency - 1] = sarCoeffs[i];
            for (int j = 0; j < arCoeffs.length; j++) {
                arSarCoeffs[(i + 1) * seasonalFrequency + j] = -sarCoeffs[i] * arCoeffs[j];
            }
        }

        return arSarCoeffs;
    }

    // Expand the moving average coefficients by combining the non-seasonal and seasonal coefficients into a single
    // array, which takes advantage of the fact that a seasonal MA model is a special case of a non-seasonal
    // MA model with zero coefficients at the non-seasonal indices.
    static double[] expandMaCoefficients(final double[] maCoeffs, final double[] smaCoeffs,
                                                 final int seasonalFrequency) {
        double[] maSmaCoeffs = new double[maCoeffs.length + smaCoeffs.length * seasonalFrequency];

        System.arraycopy(maCoeffs, 0, maSmaCoeffs, 0, maCoeffs.length);

        // Note that we take into account the interaction between the seasonal and non-seasonal coefficients,
        // which arises because the model's ma and sma polynomials are multiplied together.
        // In contrast to the ar polynomial, the ma and sma product maintains a positive sign.
        for (int i = 0; i < smaCoeffs.length; i++) {
            maSmaCoeffs[(i + 1) * seasonalFrequency - 1] = smaCoeffs[i];
            for (int j = 0; j < maCoeffs.length; j++) {
                maSmaCoeffs[(i + 1) * seasonalFrequency + j] = smaCoeffs[i] * maCoeffs[j];
            }
        }
        return maSmaCoeffs;
    }

    /**
     * Compute point forecasts for the given number of steps ahead and return the result in a primitive array.
     *
     * @param steps the number of time periods ahead to forecast.
     * @return point forecasts for the given number of steps ahead.
     */
    public double[] fcst(final int steps) {
        final int d = order.d;
        final int D = order.D;
        final int n = differencedSeries.size();
        final int m = observations.size();
        final double[] resid = this.residuals.asArray();
        final double[] diffedFcst = new double[n + steps];
        final double[] fcst = new double[m + steps];
        System.arraycopy(differencedSeries.asArray(), 0, diffedFcst, 0, n);
        System.arraycopy(observations.asArray(), 0, fcst, 0, m);
        LagPolynomial diffPolynomial = LagPolynomial.differences(d);
        LagPolynomial seasDiffPolynomial = LagPolynomial.seasonalDifferences(seasonalFrequency, D);
        LagPolynomial lagPolyomial = diffPolynomial.times(seasDiffPolynomial);
        for (int t = 0; t < steps; t++) {
            diffedFcst[n + t] = mean;
            fcst[m + t] = mean;
            fcst[m + t] += lagPolyomial.fit(fcst, m + t);
            for (int i = 0; i < arSarCoeffs.length; i++) {
                diffedFcst[n + t] += arSarCoeffs[i] * (diffedFcst[n + t - i - 1] - mean);
                fcst[m + t] += arSarCoeffs[i] * (diffedFcst[n + t - i - 1] - mean);
            }
            for (int j = maSmaCoeffs.length; j > 0 && t - j < 0; j--) {
                diffedFcst[n + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
                fcst[m + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
            }
        }
        return slice(fcst, m, m + steps);
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
        return new TimeSeries(timePeriod, startTime, fcst);
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
//      final double meanParScale = 10 * differencedSeries.stdDeviation() / Math.sqrt(differencedSeries.n());
//      return builder.set(n - 1, n - 1, 1.0).build();
//    }
//    return builder.build();
    }

    private Matrix getInitialHessian(final Arima model) {
        double[] stdErrors = model.stdErrors;
        Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(stdErrors.length);
        for (int i = 0; i < stdErrors.length; i++) {
            builder.set(i, i, stdErrors[i] * stdErrors[i] * differencedSeries.size());
        }
        return builder.build();
    }

    private double[] getInitialParameters(final double meanParScale) {
        // Set initial constant to the mean and all other parameters to zero.
        double[] initParams = new double[order.sumARMA() + order.constant];
        if (order.constant == 1 && abs(meanParScale) > Math.ulp(1.0)) {
            initParams[initParams.length - 1] = differencedSeries.mean() / meanParScale;
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
     * by the
     * number of observations.
     */
    public double sigma2() {
        return modelInfo.sigma2;
    }

    /**
     * Get the frequency of observations per seasonal cycle.
     *
     * @return the frequency of observations per seasonal cycle.
     */
    public int seasonalFrequency() {
        return this.seasonalFrequency;
    }

    /**
     * Get the standard errors of the model parameters.
     *
     * @return the standard errors of the model parameters.
     */
    public double[] stdErrors() {
        return this.stdErrors.clone();
    }

    /**
     * Get the coefficients of this ARIMA model.
     *
     * @return the coefficients of this ARIMA model.
     */
    public ModelCoefficients coefficients() {
        return this.modelCoefficients;
    }

    /**
     * Get the order of this ARIMA model.
     *
     * @return the order of this ARIMA model.
     */
    public ModelOrder order() {
        return this.order;
    }

    /**
     * Get the natural logarithm of the likelihood of the model parameters given the data.
     *
     * @return the natural logarithm of the likelihood of the model parameters given the data.
     */
    public double logLikelihood() {
        return modelInfo.logLikelihood;
    }

    /**
     * Get the Akaike Information Criterion (AIC) for this model. The AIC is defined as 2k &minus;
     * 2L where k is the number of parameters in the model and L is the logarithm of the likelihood.
     *
     * @return the Akaike Information Criterion (AIC) for this model.
     */
    public double aic() {
        return modelInfo.aic;
    }

    double[] arSarCoefficients() {
        return this.arSarCoeffs.clone();
    }

    double[] maSmaCoefficients() {
        return this.maSmaCoeffs.clone();
    }

    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        return newLine + order +
               newLine + modelInfo +
               newLine + modelCoefficients +
               newLine + newLine + "fit using " + fittingStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Arima arima = (Arima) o;

        if (seasonalFrequency != arima.seasonalFrequency) return false;
        if (Double.compare(arima.mean, mean) != 0) return false;
        if (!observations.equals(arima.observations)) return false;
        if (!differencedSeries.equals(arima.differencedSeries)) return false;
        if (!fittedSeries.equals(arima.fittedSeries)) return false;
        if (!residuals.equals(arima.residuals)) return false;
        if (!order.equals(arima.order)) return false;
        if (!modelInfo.equals(arima.modelInfo)) return false;
        if (!modelCoefficients.equals(arima.modelCoefficients)) return false;
        if (!Arrays.equals(arSarCoeffs, arima.arSarCoeffs)) return false;
        if (!Arrays.equals(maSmaCoeffs, arima.maSmaCoeffs)) return false;
        return Arrays.equals(stdErrors, arima.stdErrors);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = observations.hashCode();
        result = 31 * result + differencedSeries.hashCode();
        result = 31 * result + fittedSeries.hashCode();
        result = 31 * result + residuals.hashCode();
        result = 31 * result + seasonalFrequency;
        result = 31 * result + order.hashCode();
        result = 31 * result + modelInfo.hashCode();
        result = 31 * result + modelCoefficients.hashCode();
        temp = Double.doubleToLongBits(mean);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + Arrays.hashCode(arSarCoeffs);
        result = 31 * result + Arrays.hashCode(maSmaCoeffs);
        result = 31 * result + Arrays.hashCode(stdErrors);
        return result;
    }

    /**
     * The strategy to be used for fitting an ARIMA model.
     *
     * @author Jacob Rachiele
     */
    public enum FittingStrategy {

        CSS("conditional sum-of-squares"),

        USS("unconditional sum-of-squares"),

        ML("maximum likelihood"),

        CSSML("conditional sum-of-squares, then maximum likelihood"),

        USSML("unconditional sum-of-squares, then maximum likelihood");

        private final String description;

        FittingStrategy(final String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }

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
        ModelInformation(final int npar, final double sigma2, final double logLikelihood, final double[] residuals,
                         final double[] fitted) {
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
            return newLine + "sigma2: " + numFormatter.format(sigma2) +
                   newLine + "logLikelihood: " + numFormatter.format(logLikelihood) +
                   newLine + "AIC: " + numFormatter.format(aic);
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

        private final TimeSeries differencedSeries;
        private final ModelOrder order;
        private final FittingStrategy fittingStrategy;
        private final int seasonalFrequency;
        private final double[] arParams;
        private final double[] maParams;
        private final double[] sarParams;
        private final double[] smaParams;
        private final double meanScale;

        private OptimFunction(final TimeSeries differencedSeries, final ModelOrder order,
                              final FittingStrategy fittingStrategy, final int seasonalFrequency,
                              final double meanScale) {
            this.differencedSeries = differencedSeries;
            this.order = order;
            this.fittingStrategy = fittingStrategy;
            this.seasonalFrequency = seasonalFrequency;
            this.arParams = new double[order.p];
            this.maParams = new double[order.q];
            this.sarParams = new double[order.P];
            this.smaParams = new double[order.Q];
            this.meanScale = meanScale;
        }

        @Override
        public final double at(final Vector point) {
            functionEvaluations++;

            final double[] params = point.elements();
            System.arraycopy(params, 0, arParams, 0, order.p);
            System.arraycopy(params, order.p, maParams, 0, order.q);
            System.arraycopy(params, order.p + order.q, sarParams, 0, order.P);
            System.arraycopy(params, order.p + order.q + order.P, smaParams, 0, order.Q);

            final double[] arCoeffs = Arima.expandArCoefficients(arParams, sarParams, seasonalFrequency);
            final double[] maCoeffs = Arima.expandMaCoefficients(maParams, smaParams, seasonalFrequency);
            final double mean = (order.constant == 1) ? meanScale * params[params.length - 1] : 0.0;

            if (fittingStrategy == FittingStrategy.ML || fittingStrategy == FittingStrategy.CSSML ||
                fittingStrategy == FittingStrategy.USSML) {
                final int n = differencedSeries.size();
                ArmaKalmanFilter.KalmanOutput output = Arima.kalmanFit(differencedSeries, arCoeffs, maCoeffs, mean);
                return 0.5 * (log(output.sigma2()) + output.sumLog() / n);
            }

            final ModelInformation info = (fittingStrategy == FittingStrategy.CSS)
                                          ? Arima.fitCSS(differencedSeries, arCoeffs, maCoeffs, mean, order.npar())
                                          : Arima.fitUSS(differencedSeries, arCoeffs, maCoeffs, mean, order.npar());
            return 0.5 * log(info.sigma2);
        }

        @Override
        public String toString() {
            String newLine = System.lineSeparator();
            return order +
                   newLine + "fittingStrategy: " + fittingStrategy +
                   newLine + "seasonalFrequency: " + seasonalFrequency +
                   newLine + "arParams: " + Arrays.toString(arParams) +
                   newLine + "maParams: " + Arrays.toString(maParams) +
                   newLine + "sarParams: " + Arrays.toString(sarParams) +
                   newLine + "smaParams:" + " " + Arrays.toString(smaParams);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OptimFunction that = (OptimFunction) o;

            if (seasonalFrequency != that.seasonalFrequency) return false;
            if (Double.compare(that.meanScale, meanScale) != 0) return false;
            if (!differencedSeries.equals(that.differencedSeries)) return false;
            if (!order.equals(that.order)) return false;
            if (fittingStrategy != that.fittingStrategy) return false;
            if (!Arrays.equals(arParams, that.arParams)) return false;
            if (!Arrays.equals(maParams, that.maParams)) return false;
            if (!Arrays.equals(sarParams, that.sarParams)) return false;
            return Arrays.equals(smaParams, that.smaParams);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = differencedSeries.hashCode();
            result = 31 * result + order.hashCode();
            result = 31 * result + fittingStrategy.hashCode();
            result = 31 * result + seasonalFrequency;
            result = 31 * result + Arrays.hashCode(arParams);
            result = 31 * result + Arrays.hashCode(maParams);
            result = 31 * result + Arrays.hashCode(sarParams);
            result = 31 * result + Arrays.hashCode(smaParams);
            temp = Double.doubleToLongBits(meanScale);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

}
