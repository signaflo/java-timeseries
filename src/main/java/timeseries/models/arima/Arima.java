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
import data.Operators;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;
import optim.BFGS;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.None;
import stats.distributions.Distribution;
import stats.distributions.Normal;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.models.Model;
import timeseries.operators.LagPolynomial;

import javax.swing.*;
import java.awt.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static data.DoubleFunctions.slice;
import static data.Operators.differenceOf;
import static stats.Statistics.sumOf;
import static stats.Statistics.sumOfSquared;

/**
 * A seasonal autoregressive integrated moving average (ARIMA) model. This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class Arima implements Model {

  private static final double MACHINE_EPSILON = Math.ulp(1.0);
  private static final double DEFAULT_TOLERANCE = Math.sqrt(MACHINE_EPSILON);

  private final TimeSeries observations;
  private final TimeSeries differencedSeries;
  private final TimeSeries fittedSeries;
  private final TimeSeries residuals;
  private final int seasonalFrequency;

  private final ModelOrder order;
  private final ModelInformation modelInfo;
  private final ModelCoefficients modelCoefficients;

  private final double mean;
  private final double[] arSarCoeffs;
  private final double[] maSmaCoeffs;
  private final double[] stdErrors;

  /**
   * Create a new ARIMA model from the given observations and model order. This constructor sets the
   * model {@link FittingStrategy} to unconditional sum-of-squares and the seasonal cycle to
   * one year.
   *
   * @param observations the time asArray of observations.
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
   * @param observations  the time asArray of observations.
   * @param order         the order of the ARIMA model.
   * @param seasonalCycle the amount of time it takes for the seasonal pattern to complete one cycle. For example,
   *                      monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
   *                      However, a seasonal cycle may be an arbitrary amount of time.
   * @return a new ARIMA model from the given observations, model order, and seasonal cycle.
   */
  public static Arima model(final TimeSeries observations, final ModelOrder order,
                            final TimePeriod seasonalCycle) {
    return new Arima(observations, order, seasonalCycle, FittingStrategy.USS);
  }

  /**
   * Create a new ARIMA model from the given observations, model order, and fitting strategy. This method sets the
   * seasonal cycle to one year.
   *
   * @param observations    the time asArray of observations.
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
   * @param observations    the time asArray of observations.
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
  public static Arima model(final TimeSeries observations, final ModelOrder order,
                            final TimePeriod seasonalCycle, final FittingStrategy fittingStrategy) {
    return new Arima(observations, order, seasonalCycle, fittingStrategy);
  }

  private Arima(final TimeSeries observations, final ModelOrder order, final TimePeriod seasonalCycle,
                final FittingStrategy fittingStrategy) {
    this.observations = observations;
    this.order = order;
    this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
    this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);

    final double meanParScale = (order.constant == 1) ?
        10 * differencedSeries.stdDeviation() / Math.sqrt(differencedSeries.n()) : 1.0;
    final Vector initParams;
    final Matrix initHessian;

    if (fittingStrategy == FittingStrategy.CSSML || fittingStrategy == FittingStrategy.USSML) {
      final FittingStrategy subStrategy = (fittingStrategy == FittingStrategy.CSSML)? FittingStrategy.CSS :
          FittingStrategy.USS;
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
//    System.out.println(function.functionEvaluations());
//    System.out.println(function.gradientEvaluations());
    this.stdErrors = DoubleFunctions.sqrt(Operators.scale(inverseHessian.diagonal(), 1.0 / differencedSeries.n()));
    if (order.constant == 1) {
      this.stdErrors[this.stdErrors.length - 1] *= meanParScale;
    }

    final double[] arCoeffs = getArCoeffs(optimizedParams);
    final double[] maCoeffs = getMaCoeffs(optimizedParams);
    final double[] sarCoeffs = getSarCoeffs(optimizedParams);
    final double[] smaCoeffs = getSmaCoeffs(optimizedParams);

    this.arSarCoeffs = expandArCoefficients(arCoeffs, sarCoeffs, seasonalFrequency);
    this.maSmaCoeffs = expandMaCoefficients(maCoeffs, smaCoeffs, seasonalFrequency);
    this.mean = (order.constant == 1) ? meanParScale * optimizedParams.at(order.p + order.q + order.P + order.Q) : 0.0;
    this.modelCoefficients = new ModelCoefficients(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs, order.d, order.D,
        this.mean);

    if (fittingStrategy == FittingStrategy.CSS) {
      this.modelInfo = fitCSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
    } else if (fittingStrategy == FittingStrategy.USS) {
      this.modelInfo = fitUSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(
          differenceOf(differencedSeries.asArray(), slice(residuals, 2 * arSarCoeffs.length, residuals.length)));
      final int diffs = order.d + order.D * seasonalFrequency;
      final int offset = 2 * arSarCoeffs.length;
      for (int i = 0; i < diffs && i >= (diffs - offset); i++) {
        fittedArray[i] -= residuals[offset - diffs + i];
      }
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
    } else {
      this.modelInfo = fitML(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
    }
  }

  /**
   * Create a new ARIMA model from the given observations, model coefficients, and fitting strategy. This constructor
   * sets the seasonal cycle to one year.
   *
   * @param observations    the time asArray of observations.
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
   * Create a new ARIMA model from the given observations, model coefficients, and seasonal cycle. This constructor sets
   * the model {@link FittingStrategy} to unconditional sum-of-squares.
   *
   * @param observations  the time asArray of observations.
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
   * @param observations    the time asArray of observations.
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

  private Arima(final TimeSeries observations, final ModelCoefficients coeffs, final TimePeriod seasonalCycle,
                final FittingStrategy fittingStrategy) {
    this.observations = observations;
    this.modelCoefficients = coeffs;
    this.order = coeffs.extractModelOrder();
    this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
    this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);
    this.arSarCoeffs = expandArCoefficients(coeffs.arCoeffs, coeffs.sarCoeffs, seasonalFrequency);
    this.maSmaCoeffs = expandMaCoefficients(coeffs.maCoeffs, coeffs.smaCoeffs, seasonalFrequency);
    this.mean = coeffs.mean;
    this.stdErrors = DoubleFunctions.fill(order.sumARMA() + order.constant, Double.POSITIVE_INFINITY);

    if (fittingStrategy == FittingStrategy.CSS) {
      this.modelInfo = fitCSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
    } else if (fittingStrategy == FittingStrategy.USS) {
      this.modelInfo = fitUSS(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(
          differenceOf(differencedSeries.asArray(), slice(residuals, 2 * arSarCoeffs.length, residuals.length)));
      final int diffs = order.d + order.D * seasonalFrequency;
      final int offset = 2 * arSarCoeffs.length;
      for (int i = 0; i < diffs && i >= (diffs - offset); i++) {
        fittedArray[i] -= residuals[offset - diffs + i];
      }
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
    } else {
      this.modelInfo = fitML(differencedSeries, arSarCoeffs, maSmaCoeffs, mean, order);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(differenceOf(differencedSeries.asArray(), residuals));
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
    }
  }

  /**
   * Fit an ARIMA model using conditional sum-of-squares.
   *
   * @param differencedSeries the time asArray of observations to model.
   * @param arCoeffs          the autoregressive coefficients of the model.
   * @param maCoeffs          the moving-average coefficients of the model.
   * @param mean              the model mean.
   * @param order             the order of the model to be fit.
   *
   * @return information about the fitted model.
   */
  private static ModelInformation fitCSS(final TimeSeries differencedSeries, final double[] arCoeffs,
                                         final double[] maCoeffs, final double mean, final ModelOrder order) {
    final int offset = arCoeffs.length;
    final int n = differencedSeries.n();

    final double[] fitted = new double[n];
    final double[] residuals = new double[n];

    for (int t = offset; t < fitted.length; t++) {
      fitted[t] = mean;
      for (int i = 0; i < arCoeffs.length; i++) {
        fitted[t] += arCoeffs[i] * (differencedSeries.at(t - i - 1) - mean);
      }
      for (int j = 0; j < Math.min(t, maCoeffs.length); j++) {
        fitted[t] += maCoeffs[j] * residuals[t - j - 1];
      }
      residuals[t] = differencedSeries.at(t) - fitted[t];
    }
    final int npar = order.sumARMA() + order.constant;
    final int m = differencedSeries.n() - npar;
    final double sigma2 = sumOfSquared(residuals) / m;
    final double logLikelihood = (-n / 2.0) * (Math.log(2 * Math.PI * sigma2) + 1);
    return new ModelInformation(npar, sigma2, logLikelihood, residuals, fitted);
  }

  /**
   * Fit the model using unconditional sum-of-squares, utilizing back-forecasting to estimate the residuals for the
   * first few observations. This method, compared to conditional sum-of-squares, often gives estimates much closer to
   * those obtained from maximum-likelihood fitting, especially for shorter asArray.
   *
   * @param differencedSeries the time asArray of observations to model.
   * @param arCoeffs          the autoregressive coefficients of the model.
   * @param maCoeffs          the moving-average coefficients of the model.
   * @param mean              the model mean.
   * @param order             the order of the model to be fit.
   *
   * @return information about the fitted model.
   */
  private static ModelInformation fitUSS(final TimeSeries differencedSeries, final double[] arCoeffs,
                                         final double[] maCoeffs, final double mean, final ModelOrder order) {
    int n = differencedSeries.n();
    final int m = arCoeffs.length;
    final double[] extendedFit = new double[2 * m + n];
    final double[] extendedSeries = new double[2 * m + n];
    final double[] residuals = new double[2 * m + n];
    for (int i = 0; i < differencedSeries.n(); i++) {
      extendedSeries[i] = differencedSeries.at(--n);
    }

    n = differencedSeries.n();
    for (int t = n; t < n + 2 * m; t++) {
      extendedSeries[t] = mean;
      for (int i = 0; i < arCoeffs.length; i++) {
        if (Math.abs(arCoeffs[i]) > 0) {
          extendedSeries[t] += arCoeffs[i] * (extendedSeries[t - i - 1] - mean);
        }
      }
    }

    n = extendedSeries.length;
    for (int i = 0; i < m; i++) {
      extendedFit[i] = extendedSeries[n - i - 1];
    }
    for (int t = m; t < n; t++) {
      extendedFit[t] = mean;
      for (int i = 0; i < arCoeffs.length; i++) {
        if (Math.abs(arCoeffs[i]) > 0) {
          extendedFit[t] += arCoeffs[i] * (extendedSeries[n - t + i] - mean);
        }
      }
      for (int j = 0; j < Math.min(t, maCoeffs.length); j++) {
        if (Math.abs(maCoeffs[j]) > 0) {
          extendedFit[t] += maCoeffs[j] * residuals[t - j - 1];
        }
      }
      residuals[t] = extendedSeries[n - t - 1] - extendedFit[t];
    }

    //n = differencedSeries.n();
    n = residuals.length;
    final int npar = order.sumARMA() + order.constant;
    final double sigma2 = sumOfSquared(residuals) / (n - npar);
    final double logLikelihood = (-n / 2.0) * (Math.log(2 * Math.PI * sigma2) + 1);
    return new ModelInformation(npar, sigma2, logLikelihood, residuals, extendedFit);
  }

  private static ModelInformation fitML(final TimeSeries differencedSeries, final double[] arCoeffs,
                                        final double[] maCoeffs, final double mean, final ModelOrder order) {
    final double[] series = Operators.subtract(differencedSeries.asArray(), mean);
    ArmaKalmanFilter.KalmanOutput output = kalmanFit(differencedSeries, arCoeffs, maCoeffs, mean);
    final double sigma2 = output.sigma2();
    final double logLikelihood = output.logLikelihood();
    final double[] residuals = output.residuals();
    final double[] fitted = differenceOf(series, residuals);
    final int npar = order.sumARMA() + order.constant + 1; // Add 1 for the variance estimate.
    return new ModelInformation(npar, sigma2, logLikelihood, residuals, fitted);
  }

  private static ArmaKalmanFilter.KalmanOutput kalmanFit(final TimeSeries differencedSeries, final double[] arCoeffs,
                                                         final double[] maCoeffs, final double mean) {
    final double[] series = Operators.subtract(differencedSeries.asArray(), mean);
    StateSpaceARMA ss = new StateSpaceARMA(series, arCoeffs, maCoeffs);
    ArmaKalmanFilter kalmanFilter = new ArmaKalmanFilter(ss);
    return kalmanFilter.output();
  }

  /**
   * Create and return a new non-seasonal model order with the given number of coefficients. A constant will be fit
   * only if d is equal to 0.
   *
   * @param p the number of non-seasonal autoregressive coefficients.
   * @param d the degree of non-seasonal differencing.
   * @param q the number of non-seasonal moving-average coefficients.
   * @return a new ARIMA model order.
   */
  public static ModelOrder order(final int p, final int d, final int q) {
    return new ModelOrder(p, d, q, 0, 0, 0, d == 0);
  }

  /**
   * Create and return a new non-seasonal model order with the given number of coefficients and indication of
   * whether or not to fit a constant.
   *
   * @param p        the number of non-seasonal autoregressive coefficients.
   * @param d        the degree of non-seasonal differencing.
   * @param q        the number of non-seasonal moving-average coefficients.
   * @param constant whether or not to fit a constant to the model.
   * @return a new ARIMA model order.
   */
  public static ModelOrder order(final int p, final int d, final int q, final boolean constant) {
    return new ModelOrder(p, d, q, 0, 0, 0, constant);
  }

  /**
   * Create a new ModelOrder using the provided number of autoregressive and moving-average parameters, as well as the
   * degrees of differencing. A constant will be fit only if both d and D are equal to 0.
   *
   * @param p the number of non-seasonal autoregressive coefficients.
   * @param d the degree of non-seasonal differencing.
   * @param q the number of non-seasonal moving-average coefficients.
   * @param P the number of seasonal autoregressive coefficients.
   * @param D the degree of seasonal differencing.
   * @param Q the number of seasonal moving-average coefficients.
   * @return a new ARIMA model order.
   */
  public static ModelOrder order(final int p, final int d, final int q, final int P, final int D, final int Q) {
    return new ModelOrder(p, d, q, P, D, Q, d == 0 && D == 0);
  }

  /**
   * Create a new ModelOrder using the provided number of autoregressive and moving-average parameters, as well as the
   * degrees of differencing and indication of whether or not to fit a constant.
   *
   * @param p        the number of non-seasonal autoregressive coefficients.
   * @param d        the degree of non-seasonal differencing.
   * @param q        the number of non-seasonal moving-average coefficients.
   * @param P        the number of seasonal autoregressive coefficients.
   * @param D        the degree of seasonal differencing.
   * @param Q        the number of seasonal moving-average coefficients.
   * @param constant determines whether or not a constant is fitted with the model.
   * @return a new ARIMA model order.
   */
  public static ModelOrder order(final int p, final int d, final int q, final int P, final int D, final int Q,
                                 final boolean constant) {
    return new ModelOrder(p, d, q, P, D, Q, constant);
  }

  // Expand the autoregressive coefficients by combining the non-seasonal and seasonal coefficients into a single
  // array, which takes advantage of the fact that a seasonal AR model is a special case of a non-seasonal
  // AR model with zero coefficients at the non-seasonal indices.
  private static double[] expandArCoefficients(final double[] arCoeffs, final double[] sarCoeffs,
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
  private static double[] expandMaCoefficients(final double[] maCoeffs, final double[] smaCoeffs,
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
   *
   * @return point forecasts for the given number of steps ahead.
   */
  public double[] fcst(final int steps) {
    final int d = order.d;
    final int D = order.D;
    final int n = differencedSeries.n();
    final int m = observations.n();
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
    final int n = observations.n();
    double[] fcst = fcst(steps);
    TimePeriod timePeriod = observations.timePeriod();
    final OffsetDateTime startTime = observations.observationTimes().get(n - 1).plus(
        timePeriod.periodLength() * timePeriod.timeUnit().unitLength(), timePeriod.timeUnit().temporalUnit());
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
    return forecast(steps, 0.05);
  }

  /*
   * Start with the difference equation form of the model (Cryer and Chan 2008, 5.2): diffedSeries_t = ArmaProcess_t.
   * Then, subtracting diffedSeries_t from both sides, we have ArmaProcess_t - diffedSeries_t = 0. Now add Y_t to both
   * sides and rearrange terms to obtain Y_t = Y_t - diffedSeries_t + ArmaProcess_t. Thus, our in-sample estimate of
   * Y_t, the "integrated" asArray, is Y_t(hat) = Y_t - diffedSeries_t + fittedArmaProcess_t.
   */
  private double[] integrate(final double[] fitted) {
    final int offset = this.order.d + this.order.D * this.seasonalFrequency;
    final double[] integrated = new double[this.observations.n()];
    for (int t = 0; t < offset; t++) {
      integrated[t] = observations.at(t);
    }
    for (int t = offset; t < observations.n(); t++) {
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
      builder.set(i, i, stdErrors[i] * stdErrors[i] * differencedSeries.n());
    }
    return builder.build();
  }

  private double[] getInitialParameters(final double meanParScale) {
    // Set initial constant to the mean and all other parameters to zero.
    double[] initParams = new double[order.sumARMA() + order.constant];
    if (order.constant == 1 && Math.abs(meanParScale) > Math.ulp(1.0)) {
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
   * @return the maximum-likelihood estimate of the model variance, equal to the sum of squared residuals divided by the
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

  // ********** Plots **********//
  @Override
  public final void plotFit() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(observations.asArray());
      List<Double> fittedList = com.google.common.primitives.Doubles.asList(fittedSeries.asArray());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
                                                .title("ARIMA Fitted vs Actual").build();
      XYSeries fitSeries = chart.addSeries("Fitted Values", xAxis, fittedList);
      XYSeries observedSeries = chart.addSeries("Actual Values", xAxis, seriesList);
      XYStyler styler = chart.getStyler();
      styler.setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
      observedSeries.setLineWidth(0.75f);
      observedSeries.setMarker(new None()).setLineColor(Color.RED);
      fitSeries.setLineWidth(0.75f);
      fitSeries.setMarker(new None()).setLineColor(Color.BLUE);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("ARIMA Fit");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }

  @Override
  public final void plotResiduals() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(residuals.asArray());
      final XYChart chart = new XYChartBuilder().theme(ChartTheme.GGPlot2).height(600).width(800)
                                                .title("ARIMA Residuals").build();
      XYSeries residualSeries = chart.addSeries("Residuals", xAxis, seriesList);
      residualSeries.setXYSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
      residualSeries.setMarker(new Circle()).setMarkerColor(Color.RED);

      JPanel panel = new XChartPanel<>(chart);
      JFrame frame = new JFrame("ARIMA Residuals");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.add(panel);
      frame.pack();
      frame.setVisible(true);
    }).start();
  }
  // ********** Plots **********//

  @Override
  public String toString() {
    return "\norder: " + order + "\nmodelInfo: " + modelInfo + "\nmodelCoefficients: " + modelCoefficients;
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
   * The order of an ARIMA model, consisting of the number of autoregressive and moving average parameters, along with
   * the degree of differencing and a flag indicating whether or not the model includes a constant, or intercept, term.
   * This class is immutable and thread-safe.
   *
   * @author Jacob Rachiele
   */
  public static class ModelOrder {

    final int p;
    final int d;
    final int q;
    final int P;
    final int D;
    final int Q;
    final int constant;

    private ModelOrder(final int p, final int d, final int q, final int P, final int D, final int Q,
                       final boolean constant) {
      this.p = p;
      this.d = d;
      this.q = q;
      this.P = P;
      this.D = D;
      this.Q = Q;
      this.constant = (constant) ? 1 : 0;
    }

    // This returns the total number of nonseasonal and seasonal ARMA parameters.
    private int sumARMA() {
      return this.p + this.q + this.P + this.Q;
    }

    @Override
    public String toString() {
      boolean isSeasonal = P > 0 || Q > 0 || D > 0;
      StringBuilder builder = new StringBuilder();
      if (isSeasonal) {
        builder.append("Seasonal ");
      }
      builder.append("ARIMA (").append(p).append(", ").append(d).append(", ").append(q);
      if (isSeasonal) {
        builder.append(") x (").append(P).append(", ").append(D).append(", ").append(Q);
      }
      builder.append(") with").append((constant == 1) ? " a constant" : " no constant");
      return builder.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + D;
      result = prime * result + P;
      result = prime * result + Q;
      result = prime * result + constant;
      result = prime * result + d;
      result = prime * result + p;
      result = prime * result + q;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ModelOrder other = (ModelOrder) obj;
      if (D != other.D) return false;
      if (P != other.P) return false;
      if (Q != other.Q) return false;
      if (constant != other.constant) return false;
      if (d != other.d) return false;
      return p == other.p && q == other.q;
    }
  }

  /**
   * Consists of the autoregressive and moving-average coefficients for a seasonal ARIMA model, along with the
   * degrees of differencing and the model mean.
   *
   * @author Jacob Rachiele
   */
  public static class ModelCoefficients {

    private final double[] arCoeffs;
    private final double[] maCoeffs;
    private final double[] sarCoeffs;
    private final double[] smaCoeffs;
    private final int d;
    private final int D;
    private final double mean;
    // The intercept is equal to mean * (1 - (sum of AR coefficients))
    private final double intercept;

    /**
     * Create a structure holding the coefficients, the degrees of differencing, and the mean of a seasonal ARIMA model.
     *
     * @param arCoeffs  the non-seasonal autoregressive coefficients.
     * @param maCoeffs  the non-seasonal moving-average coefficients.
     * @param sarCoeffs the seasonal autoregressive coefficients.
     * @param smaCoeffs the seasonal moving-average coefficients.
     * @param d         the non-seasonal degree of differencing.
     * @param D         the seasonal degree of differencing.
     * @param mean      the process mean.
     */
    ModelCoefficients(final double[] arCoeffs, final double[] maCoeffs, final double[] sarCoeffs,
                      final double[] smaCoeffs, final int d, final int D, final double mean) {
      this.arCoeffs = arCoeffs.clone();
      this.maCoeffs = maCoeffs.clone();
      this.sarCoeffs = sarCoeffs.clone();
      this.smaCoeffs = smaCoeffs.clone();
      this.d = d;
      this.D = D;
      this.mean = mean;
      this.intercept = this.mean * (1 - sumOf(arCoeffs) - sumOf(sarCoeffs));
    }

    private ModelCoefficients(Builder builder) {
      this.arCoeffs = builder.arCoeffs.clone();
      this.maCoeffs = builder.maCoeffs.clone();
      this.sarCoeffs = builder.sarCoeffs.clone();
      this.smaCoeffs = builder.smaCoeffs.clone();
      this.d = builder.d;
      this.D = builder.D;
      this.mean = builder.mean;
      this.intercept = this.mean * (1 - sumOf(arCoeffs) - sumOf(sarCoeffs));
    }

    /**
     * Create a new builder for a ModelCoefficients object.
     *
     * @return a new builder for a ModelCoefficients object.
     */
    public static Builder newBuilder() {
      return new Builder();
    }

    /**
     * Get the autoregressive coefficients.
     *
     * @return the autoregressive coefficients.
     */
    public final double[] arCoeffs() {
      return arCoeffs.clone();
    }

    /**
     * Get the moving-average coefficients.
     *
     * @return the moving-average coefficients.
     */
    public final double[] maCoeffs() {
      return maCoeffs.clone();
    }

    /**
     * Get the seasonal autoregressive coefficients.
     *
     * @return the seasonal autoregressive coefficients.
     */
    public final double[] seasonalARCoeffs() {
      return sarCoeffs.clone();
    }

    /**
     * Get the seasonal moving-average coefficients.
     *
     * @return the seasonal moving-average coefficients.
     */
    public final double[] seasonalMACoeffs() {
      return smaCoeffs.clone();
    }

    /**
     * Get the degree of non-seasonal differencing.
     *
     * @return the degree of non-seasonal differencing.
     */
    public final int d() {
      return d;
    }

    /**
     * Get the degree of seasonal differencing.
     *
     * @return the degree of seasonal differencing.
     */
    public final int D() {
      return D;
    }

    /**
     * Get the model mean.
     *
     * @return the model mean.
     */
    public final double mean() {
      return mean;
    }

    /**
     * Get the model intercept term. Note that this is <i>not</i> the model mean, as in R, but the actual intercept. The
     * intercept is equal to &mu; &times; (1 - sum(AR)), where &mu; is the model mean and AR is a vector containing
     * the non-seasonal and seasonal autoregressive coefficients.
     *
     * @return the model intercept term.
     */
    public final double intercept() {
      return this.intercept;
    }

    public final double[] getAllCoeffs() {
      if (Math.abs(mean) < 1E-16) {
        return DoubleFunctions.combine(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs);
      }
      return DoubleFunctions.append(DoubleFunctions.combine(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs), mean);
    }

    final boolean isSeasonal() {
      return this.D > 0 || this.sarCoeffs.length > 0 || this.smaCoeffs.length > 0;
    }

    /**
     * Computes and returns the order of the ARIMA model corresponding to the model coefficients.
     *
     * @return the order of the ARIMA model corresponding to the model coefficients.
     */
    private ModelOrder extractModelOrder() {
      return new ModelOrder(arCoeffs.length, d, maCoeffs.length, sarCoeffs.length, D, smaCoeffs.length,
          (Math.abs(mean) > 1E-16));
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      if (arCoeffs.length > 0) {
        sb.append("\nautoregressive: ").append(Arrays.toString(arCoeffs));
      }
      if (maCoeffs.length > 0) {
        sb.append("\nmoving-average: ").append(Arrays.toString(maCoeffs));
      }
      if (sarCoeffs.length > 0) {
        sb.append("\nseasonal autoregressive: ").append(Arrays.toString(sarCoeffs));
      }
      if (smaCoeffs.length > 0) {
        sb.append("\nseasonal moving-average: ").append(Arrays.toString(smaCoeffs));
      }
      sb.append("\nmean: ").append(mean);
      sb.append("\nintercept: ").append(intercept);
      return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ModelCoefficients that = (ModelCoefficients) o;

      if (d != that.d) return false;
      if (D != that.D) return false;
      if (Double.compare(that.mean, mean) != 0) return false;
      if (Double.compare(that.intercept, intercept) != 0) return false;
      if (!Arrays.equals(arCoeffs, that.arCoeffs)) return false;
      if (!Arrays.equals(maCoeffs, that.maCoeffs)) return false;
      if (!Arrays.equals(sarCoeffs, that.sarCoeffs)) return false;
      return Arrays.equals(smaCoeffs, that.smaCoeffs);
    }

    @Override
    public int hashCode() {
      int result;
      long temp;
      result = Arrays.hashCode(arCoeffs);
      result = 31 * result + Arrays.hashCode(maCoeffs);
      result = 31 * result + Arrays.hashCode(sarCoeffs);
      result = 31 * result + Arrays.hashCode(smaCoeffs);
      result = 31 * result + d;
      result = 31 * result + D;
      temp = Double.doubleToLongBits(mean);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(intercept);
      result = 31 * result + (int) (temp ^ (temp >>> 32));
      return result;
    }

    /**
     * A builder class for ARIMA model coefficients.
     *
     * @author Jacob Rachiele
     */
    public static class Builder {
      private double[] arCoeffs = new double[]{};
      private double[] maCoeffs = new double[]{};
      private double[] sarCoeffs = new double[]{};
      private double[] smaCoeffs = new double[]{};
      private int d = 0;
      private int D = 0;
      private double mean = 0.0;

      private Builder() {
      }

      public Builder setARCoeffs(double... arCoeffs) {
        this.arCoeffs = arCoeffs;
        return this;
      }

      public Builder setSeasonalARCoeffs(double... sarCoeffs) {
        this.sarCoeffs = sarCoeffs;
        return this;
      }

      public Builder setMACoeffs(double... maCoeffs) {
        this.maCoeffs = maCoeffs;
        return this;
      }

      public Builder setSeasonalMACoeffs(double... smaCoeffs) {
        this.smaCoeffs = smaCoeffs;
        return this;
      }

      public Builder setDifferences(int d) {
        this.d = d;
        return this;
      }

      public Builder setSeasonalDifferences(int D) {
        this.D = D;
        return this;
      }

      public Builder setMean(double mean) {
        this.mean = mean;
        return this;
      }

      public ModelCoefficients build() {
        return new ModelCoefficients(this);
      }
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
     * @param npar          the number of parameters in the model.
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
      return "sigma2: " + sigma2 + "\nlogLikelihood: " + logLikelihood + "\nAIC: " + aic;
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

  static class OptimFunction extends AbstractMultivariateFunction {

    private final TimeSeries differencedSeries;
    private final ModelOrder order;
    private final FittingStrategy fittingStrategy;
    private final int seasonalFrequency;
    private final double[] arParams;
    private final double[] maParams;
    private final double[] sarParams;
    private final double[] smaParams;
    private final double meanScale;

    OptimFunction(final TimeSeries differencedSeries, final ModelOrder order, final FittingStrategy
        fittingStrategy, final int seasonalFrequency, final double meanScale) {
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

      if (fittingStrategy == FittingStrategy.ML || fittingStrategy == FittingStrategy.CSSML
          || fittingStrategy == FittingStrategy.USSML) {
        final int n = differencedSeries.n();
        ArmaKalmanFilter.KalmanOutput output = Arima.kalmanFit(differencedSeries, arCoeffs, maCoeffs, mean);
        return 0.5 * (Math.log(output.sigma2()) + output.sumLog() / n);
      }

      final ModelInformation info = (fittingStrategy == FittingStrategy.CSS)
          ? Arima.fitCSS(differencedSeries, arCoeffs, maCoeffs, mean, order)
          : Arima.fitUSS(differencedSeries, arCoeffs, maCoeffs, mean, order);
      return 0.5 * Math.log(info.sigma2);
    }

//    static double[] transformParameters(final double[] arParams) {
//      double phi;
//      int p = arParams.length;
//      double[] work = new double[p];
//      double[] transformed = new double[p];
//      for (int i = 0; i < p; i++) {
//        transformed[i] = Math.tanh(arParams[i]);
//      }
//      for (int j = 1; j < p; j++) {
//        phi = transformed[j];
//        for (int k = 0; k < j; k++) {
//          work[k] -= phi * transformed[j - k  - 1];
//        }
//        System.arraycopy(work, 0, transformed, 0, j);
//      }
//      return transformed;
//    }

    @Override
    public String toString() {
      return "differencedSeries: " + differencedSeries + "\norder: " + order + "\nfittingStrategy: " +
             fittingStrategy + "\nseasonalFrequency: " + seasonalFrequency + "\narParams: " + Arrays.toString(arParams)
             + "\nmaParams: " + Arrays.toString(maParams) + "\nsarParams: " + Arrays.toString(sarParams) +
             "\nsmaParams:" +
             " " + Arrays.toString(smaParams);
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

  /**
   * An ARIMA model simulation.
   */
  public static class Simulation {

    private final ModelCoefficients coefficients;
    private final Distribution distribution;
    private final TimePeriod period;
    private final TimePeriod seasonalCycle;
    private final int n;

    private Simulation(Builder builder) {
      this.coefficients = builder.coefficients;
      this.distribution = builder.distribution;
      this.period = builder.period;
      this.seasonalCycle = builder.seasonalCycle;
      this.n = builder.n;
    }

    /**
     * Simulate the ARIMA model and return the resulting time asArray.
     *
     * @return the simulated time asArray.
     */
    public TimeSeries sim() {
      final int burnin = (int) (n / 2.0);
      final int seasonalFrequency = (int) period.frequencyPer(seasonalCycle);
      double[] arSarCoeffs = expandArCoefficients(coefficients.arCoeffs, coefficients.sarCoeffs, seasonalFrequency);
      double[] maSmaCoeffs = expandMaCoefficients(coefficients.maCoeffs, coefficients.smaCoeffs, seasonalFrequency);
      int diffOffset = coefficients.d + coefficients.D * seasonalFrequency;
      int offset = Math.min(n, arSarCoeffs.length);
      double[] series = new double[n + burnin];
      double[] errors = new double[n + burnin];
      for (int t = 0; t < offset; t++) {
        series[t] = errors[t] = distribution.rand();
        series[t] += coefficients.mean;
        for (int j = 0; j < Math.min(t, maSmaCoeffs.length); j++) {
          series[t] += maSmaCoeffs[j] * errors[t - j - 1];
        }
      }

      int end;
      for (int t = offset; t < n + burnin; t++) {
        series[t] = errors[t] = distribution.rand();
        series[t] += coefficients.mean;
        end = Math.min(t, arSarCoeffs.length);
        for (int j = 0; j < end; j++) {
          series[t] += arSarCoeffs[j] * (series[t - j - 1] - coefficients.mean);
        }
        end = Math.min(t, maSmaCoeffs.length);
        for (int j = 0; j < end; j++) {
          series[t] += maSmaCoeffs[j] * errors[t - j - 1];
        }
      }

      LagPolynomial poly = LagPolynomial.differences(coefficients.d)
          .times(LagPolynomial.seasonalDifferences(seasonalFrequency, coefficients.D));
      end = n + burnin;
      for (int t = diffOffset; t < end; t++) {
        series[t] += poly.fit(series, t);
      }
      series = DoubleFunctions.slice(series, burnin, n + burnin);
      return new TimeSeries(period, OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0)), series);
    }

    /**
     * Get a new builder for an ARIMA simulation.
     * @return a new builder for an ARIMA simulation.
     */
    public static Builder newBuilder() {
      return new Builder();
    }

    public static class Builder {

      private ModelCoefficients coefficients = ModelCoefficients.newBuilder().build();
      private Distribution distribution = new Normal();
      private TimePeriod period = (coefficients.isSeasonal())? TimePeriod.oneMonth() : TimePeriod.oneYear();
      private TimePeriod seasonalCycle = TimePeriod.oneYear();
      private int n = 500;
      private boolean periodSet = false;

      /**
       * Set the model coefficients to be used in simulating the ARIMA model.
       *
       * @param coefficients the model coefficients for the simulation.
       * @return this builder.
       */
      public Builder setCoefficients(ModelCoefficients coefficients) {
        if (coefficients == null) {
          throw new NullPointerException("The model coefficients cannot be null.");
        }
        this.coefficients = coefficients;
        if (!periodSet) {
          this.period = (coefficients.isSeasonal())? TimePeriod.oneMonth() : TimePeriod.oneYear();
        }
        return this;
      }

      /**
       * Set the probability distribution to draw the ARIMA process random errors from.
       *
       * @param distribution the probability distribution to draw the random errors from.
       * @return this builder.
       */
      public Builder setDistribution(Distribution distribution) {
        if (distribution == null) {
          throw new NullPointerException("The distribution cannot be null.");
        }
        this.distribution = distribution;
        return this;
      }

      /**
       * Set the time period between simulated observations. The default is one year for
       * non-seasonal model coefficients and one month for seasonal model coefficients.
       * @param period the time period between simulated observations.
       * @return this builder.
       */
      public Builder setPeriod(TimePeriod period) {
        if (period == null) {
          throw new NullPointerException("The time period cannot be null.");
        }
        this.periodSet = true;
        this.period = period;
        return this;
      }

      /**
       * Set the time cycle at which the seasonal pattern of the simulated time asArray repeats. This defaults
       * to one year.
       * @param seasonalCycle the time cycle at which the seasonal pattern of the simulated time asArray repeats.
       * @return this builder.
       */
      public Builder setSeasonalCycle(TimePeriod seasonalCycle) {
        if (seasonalCycle == null) {
          throw new NullPointerException("The seasonal cycle cannot be null.");
        }
        this.seasonalCycle = seasonalCycle;
        return this;
      }

      /**
       * Set the number of observations to be simulated.
       *
       * @param n the number of observations to simulate.
       * @return this builder.
       */
      public Builder setN(int n) {
        if (n < 1) {
          throw new IllegalArgumentException("the number of observations to simulate must be a positive integer.");
        }
        this.n = n;
        return this;
      }

      /**
       * Simulate the time asArray directly from this builder. This is equivalent to calling build on this builder,
       * then sim on the returned Simulation object.
       * @return the simulated time asArray.
       */
      public TimeSeries sim() {
        return new Simulation(this).sim();
      }

      /**
       * Construct and return a new fully built and immutable Simulation object.
       * @return a new fully built and immutable Simulation object.
       */
      public Simulation build() {
        return new Simulation(this);
      }
    }
  }

  /**
   * The strategy to be used for fitting an ARIMA model.
   *
   * @author Jacob Rachiele
   *
   */
  public enum FittingStrategy {

    /**
     * Conditional sum-of-squares.
     */
    CSS,

    /**
     * Unconditional sum-of-squares.
     */
    USS,

    /**
     * Maximum likelihood.
     */
    ML,

    /**
     * Conditional sum-of-squares followed by maximum likelihood.
     */
    CSSML,

    /**
     * Unconditional sum-of-squares followed by maximum likelihood.
     */
    USSML;

  }
}
