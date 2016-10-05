/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 * This file is licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */

package timeseries.models;

import static data.DoubleFunctions.slice;
import static data.DoubleFunctions.negativeOf;
import static stats.Statistics.sumOf;
import static stats.Statistics.sumOfSquared;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.None;

import data.DoubleFunctions;
import data.Operators;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import optim.AbstractMultivariateFunction;
import optim.BFGS;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.arima.FittingStrategy;
import timeseries.operators.LagPolynomial;

/**
 * A seasonal autoregressive integrated moving average (ARIMA) model. This class is immutable and
 * thread-safe.
 * 
 * @author Jacob Rachiele
 *
 */
public final class Arima implements Model {

  private final TimeSeries observations;
  private final TimeSeries differencedSeries;
  private final TimeSeries fittedSeries;
  private final TimeSeries residuals;
  private final int seasonalFrequency;

  // ModelOrder stores the number of parameters, degree of differencing, and a constant flag.
  private final ModelOrder order;
  private final ModelInformation modelInfo;
  private final ModelCoefficients modelCoefficients;

  // The intercept is equal to mean * (1 - (sum of AR coefficients))
  private final double intercept;
  private final double mean;
  private final double[] arSarCoeffs;
  private final double[] maSmaCoeffs;

  /**
   * Create a new ARIMA model from the given observations, model order, and seasonal cycle.
   * 
   * @param observations the time series of observations.
   * @param order the model order.
   * @param seasonalCycle the amount of time it takes for the seasonal pattern to complete one cycle,
   *        <i>relative to</i> a particular unit of time. For example, monthly data usually has a cycle of one year,
   *        hourly data a cycle of one day, etc... For a non-typical example, one could specify a seasonal
   *        cycle of half a year using a time period of six months, or, equivalently, two quarters.
   */
  public Arima(final TimeSeries observations, final ModelOrder order, final TimePeriod seasonalCycle) {
    this.observations = observations;
    this.order = order;
    this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
    this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);
    
    final Vector initParams = new Vector(getInitialParameters());
    final Matrix initHessian = getInitialHessian(initParams.elements());
    final AbstractMultivariateFunction function = new OptimFunction(differencedSeries, order, FittingStrategy.USS,
        seasonalFrequency);
    final BFGS optimizer = new BFGS(function, initParams, 1e-8, 1e-8, initHessian);
    final Vector optimizedParams = optimizer.parameters();
    final Matrix inverseHessian = optimizer.inverseHessian();
    
    final double[] stdErrors = DoubleFunctions.sqrt(inverseHessian.scaledBy((1.0) / differencedSeries.n()).diagonal());
    final double[] arCoeffs = getArCoeffs(optimizedParams);
    final double[] maCoeffs = getMaCoeffs(optimizedParams);
    final double[] sarCoeffs = getSarCoeffs(optimizedParams);
    final double[] smaCoeffs = getSmaCoeffs(optimizedParams);
    
    this.arSarCoeffs = expandArCoefficients(arCoeffs, sarCoeffs, seasonalFrequency);
    this.maSmaCoeffs = expandMaCoefficients(maCoeffs, smaCoeffs, seasonalFrequency);
    this.mean = (order.constant == 1)? optimizedParams.at(order.p + order.q + order.P + order.Q) : 0.0;
    this.intercept = mean * (1 - sumOf(arCoeffs));
    this.modelCoefficients = new ModelCoefficients(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs, order.d, order.D, this.mean);
    this.modelInfo = fitUss(differencedSeries, arCoeffs, maCoeffs, mean);
    
    final double[] residuals = modelInfo.residuals;
    final double[] fittedArray = integrate(
        Operators.differenceOf(differencedSeries.series(), slice(residuals, 2 * arCoeffs.length, residuals.length)));
    for (int i = 0; i < arCoeffs.length; i++) {
      fittedArray[i] -= residuals[i + arCoeffs.length];
    }
    this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
    this.residuals = this.observations.minus(this.fittedSeries);
  }

  /**
   * Create a new ARIMA model from the given observations, model coefficients, and seasonal cycle. This constructor sets
   * the model's {@link FittingStrategy} to unconditional sum-of-squares.
   * 
   * @param observations the time series of observations.
   * @param coeffs the parameter coefficients of the model.
   * @param seasonalCycle the amount of time it takes for the seasonal part of the model to complete one cycle. For
   *        example, monthly or quarterly data typically has a cycle of one year, hourly data may have a cycle of one
   *        day, etc... For less typical situations, one could specify a seasonal cycle of, e.g., half of a year, five
   *        milliseconds, or two decades.
   */
  public Arima(final TimeSeries observations, final ModelCoefficients coeffs, final TimePeriod seasonalCycle) {
    this(observations, coeffs, seasonalCycle, FittingStrategy.USS);
  }

  /**
   * Create a new ARIMA model from the given observations, model coefficients, seasonal cycle. and fitting strategy.
   * 
   * @param observations the time series of observations.
   * @param coeffs the parameter coefficients of the model.
   * @param seasonalCycle the amount of time it takes for the seasonal part of the model to complete one cycle. For
   *        example, monthly or quarterly data typically has a cycle of one year, hourly data may have a cycle of one
   *        day, etc... For less typical situations, one could specify a seasonal cycle of, e.g., half of a year, five
   *        milliseconds, or two decades.
   * @param fittingStrategy the strategy to use to fit the model to the data. Results may differ from dataset to
   *        dataset, but on average, unconditional sum-of-squares outperforms conditional sum-of-squares.
   */
  public Arima(final TimeSeries observations, final ModelCoefficients coeffs, final TimePeriod seasonalCycle,
      final FittingStrategy fittingStrategy) {
    this.observations = observations;
    this.modelCoefficients = coeffs;
    this.order = coeffs.extractModelOrder();
    this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
    this.differencedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);
    this.arSarCoeffs = expandArCoefficients(coeffs.arCoeffs, coeffs.sarCoeffs, seasonalFrequency);
    this.maSmaCoeffs = expandMaCoefficients(coeffs.maCoeffs, coeffs.smaCoeffs, seasonalFrequency);
    this.mean = coeffs.mean;
    this.intercept = mean * (1 - sumOf(arSarCoeffs));
    if (fittingStrategy == FittingStrategy.CSS) {
      modelInfo = fitCss(differencedSeries, arSarCoeffs, maSmaCoeffs, mean);
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), modelInfo.fitted);
      this.residuals = this.observations.minus(this.fittedSeries);
    } else {
      modelInfo = fitUss(differencedSeries, arSarCoeffs, maSmaCoeffs, mean);
      final double[] residuals = modelInfo.residuals;
      final double[] fittedArray = integrate(
          Operators.differenceOf(differencedSeries.series(), slice(residuals, 2 * arSarCoeffs.length, residuals.length)));
      for (int i = 0; i < arSarCoeffs.length; i++) {
        fittedArray[i] -= residuals[i + arSarCoeffs.length];
      }
      this.fittedSeries = new TimeSeries(observations.timePeriod(), observations.observationTimes(), fittedArray);
      this.residuals = this.observations.minus(this.fittedSeries);
      System.out.println(this.residuals.sumOfSquares());
    }
  }

  /**
   * The maximum-likelihood estimate of the model variance, equal to the sum of squared residuals divided by the number
   * of observations.
   * 
   * @return the maximum-likelihood estimate of the model variance, equal to the sum of squared residuals divided by the
   *         number of observations.
   */
  public final double sigma2() {
    return modelInfo.sigma2;
  }
  
  public final ModelCoefficients coefficients() {
    return this.modelCoefficients;
  }

  /**
   * The natural logarithm of the likelihood of the model parameters given the data. This is a partial likelihood in
   * case the fitting strategy is conditional sum-of-squares or unconditional sum-of-squares, and the full likelihood in
   * case the fitting strategy is maximum likelihood.
   * 
   * @return The natural logarithm of the likelihood of the model parameters.
   */
  public final double logLikelihood() {
    return modelInfo.logLikelihood;
  }

  private Matrix getInitialHessian(final double[] initParams) {
    final int n = initParams.length;
    final Matrix.IdentityBuilder builder = new Matrix.IdentityBuilder(n);
    if (order.constant == 1) {
      final double meanParScale = 10 * differencedSeries.stdDeviation() / Math.sqrt(differencedSeries.n());
      return builder.set(n - 1, n - 1, meanParScale).build();
    }
    return builder.build();
  }

  private final double[] getInitialParameters() {
    // Set initial constant to the mean and all other parameters to zero.
    double[] initParams = new double[order.sumARMA() + order.constant];
    if (order.constant == 1) {
      initParams[initParams.length - 1] = differencedSeries.mean();
    }
    return initParams;
  }

  // Expand the AR coefficients by combining the non-seasonal and seasonal coefficients into a single
  // array, which takes advantage of the fact that a seasonal AR model is a special case of a non-seasonal
  // AR model with zero coefficients at the non-seasonal indices.
  static final double[] expandArCoefficients(final double[] arCoeffs, final double[] sarCoeffs,
      final int seasonalFrequency) {
    double[] arSarCoeffs = new double[arCoeffs.length + sarCoeffs.length * seasonalFrequency];

    for (int i = 0; i < arCoeffs.length; i++) {
      arSarCoeffs[i] = arCoeffs[i];
    }

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

  // Expand the AR coefficients by combining the non-seasonal and seasonal coefficients into a single
  // array, which takes advantage of the fact that a seasonal AR model is a special case of a non-seasonal
  // AR model with zero coefficients at the non-seasonal indices.
  static final double[] expandMaCoefficients(final double[] maCoeffs, final double[] smaCoeffs,
      final int seasonalFrequency) {
    double[] maSmaCoeffs = new double[maCoeffs.length + smaCoeffs.length * seasonalFrequency];

    for (int i = 0; i < maCoeffs.length; i++) {
      maSmaCoeffs[i] = maCoeffs[i];
    }

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
   * Fit the model using conditional sum-of-squares.
   * 
   * @return information about the model fit.
   */
  static final ModelInformation fitCss(final TimeSeries differencedSeries, final double[] arCoeffs,
      final double[] maCoeffs, final double mean) {
    final int offset = arCoeffs.length;
    final int n = differencedSeries.n();

    final double[] fitted = new double[n];
    final double[] residuals = new double[n];

    for (int t = offset; t < fitted.length; t++) {
      for (int i = 0; i < arCoeffs.length; i++) {
        fitted[t] += mean + arCoeffs[i] * (differencedSeries.at(t - i - 1) - mean);
      }
      residuals[t] = differencedSeries.at(t) - fitted[t];
      for (int j = 0; j < maCoeffs.length; j++) {
        fitted[t] += maCoeffs[j] * residuals[t - j - 1];
      }
    }

    final double sigma2 = sumOfSquared(residuals) / differencedSeries.n();
    final double logLikelihood = (-n / 2) * (Math.log(2 * Math.PI * sigma2) + 1);
    return new ModelInformation(sigma2, logLikelihood, residuals, fitted);
  }

  /**
   * Fit the model using unconditional sum-of-squares, utilizing back-forecasting to estimate the residuals for the
   * first few observations. This method, compared to conditional sum-of-squares, often gives estimates much closer 
   * to those obtained from maximum-likelihood fitting, especially for shorter series.
   * 
   * @return information about the model fit.
   */
  static final ModelInformation fitUss(final TimeSeries differencedSeries, final double[] arCoeffs,
      final double[] maCoeffs, final double mean) {
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

    n = differencedSeries.n();
    final double sigma2 = sumOfSquared(residuals) / n;
    final double logLikelihood = (-n / 2) * (Math.log(2 * Math.PI * sigma2) + 1);
    return new ModelInformation(sigma2, logLikelihood, residuals, extendedFit);
  }
  
  /*
   * Start with the difference equation form of the model (Cryer and Chan 2008, 5.2): diffedSeries_t = ArmaProcess_t.
   * Then, subtracting diffedSeries_t from both sides, we have ArmaProcess_t - diffedSeries_t = 0. Now add Y_t to both
   * sides and rearrange terms to obtain Y_t = Y_t - diffedSeries_t + ArmaProcess_t. Thus, our in-sample estimate of
   * Y_t, the "integrated" series, is Y_t(hat) = Y_t - diffedSeries_t + fittedArmaProcess_t
   */
  private final double[] integrate(final double[] fitted) {
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

  /**
   * The model intercept term. Note that this is <i>not</i> the model mean, as in R, but the actual intercept.
   * 
   * @return the model intercept term.
   */
  public final double intercept() {
    return this.intercept;
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
  
  public final double[] forecast(final int steps) {
    final int d = order.d;
    final int n = differencedSeries.n();
    final int m = observations.n();
    final double[] resid = this.residuals.series();
    final double[] diffedFcst = new double[n + steps];
    final double[] fcst = new double[m + steps];
    System.arraycopy(differencedSeries.series(), 0, diffedFcst, 0, n);
    System.arraycopy(observations.series(), 0, fcst, 0, m);
    LagPolynomial lagPolynomial = LagPolynomial.firstDifference();
    for (int t = 0; t < steps; t++) {
      diffedFcst[n + t] = mean;
      fcst[m + t] = mean;
      for (int i = 0; i < arSarCoeffs.length; i++) {
        diffedFcst[n + t] += arSarCoeffs[i] * (diffedFcst[n + t - i - 1] - mean);
        fcst[m + t] += arSarCoeffs[i] * diffedFcst[m + t - i - d - 1];
        fcst[m + t] += lagPolynomial.applyInverse(fcst, m + t);
      }
      for (int j = maSmaCoeffs.length; j > 0 && t - j < 0; j--) {
        diffedFcst[n + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
        fcst[m + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
      }
    }
    return slice(fcst, m, m + steps);
  }
  @Override
  public TimeSeries pointForecast(int steps) {
    final int n = observations.n();
    double[] fcst = forecast(steps);
    TimePeriod timePeriod = observations.timePeriod();
    final OffsetDateTime startTime = observations.observationTimes().get(n - 1)
            .plus((long)timePeriod.unitLength() * timePeriod.timeUnit().periodLength(), timePeriod.timeUnit().temporalUnit());
    return new TimeSeries(timePeriod, startTime, fcst);
  }

  @Override
  public Forecast forecast(int steps, double alpha) {
    return new ArimaForecast(this, steps, alpha);
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

  public final void plotFit() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(observations.series());
      List<Double> fittedList = com.google.common.primitives.Doubles.asList(fittedSeries.series());
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

  public final void plotResiduals() {
    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fittedSeries.observationTimes().size());
      for (OffsetDateTime dateTime : fittedSeries.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(residuals.series());
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

  /**
   * The order of an ARIMA model, consisting of the number of autoregressive and moving average parameters, along with
   * the degree of differencing and whether or not a constant is in the model. This class is immutable and thread-safe.
   * 
   * @author Jacob Rachiele
   *
   */
  public static final class ModelOrder {
    private final int p;
    private final int d;
    private final int q;
    private final int P;
    private final int D;
    private final int Q;
    private final int constant;

    /**
     * Create a new ModelOrder using the provided number of autoregressive and moving-average parameters, as well as the
     * degree of differencing and whether or not to fit a constant (or mean).
     * 
     * @param p the number of non-seasonal autoregressive coefficients.
     * @param d the degree of non-seasonal differencing.
     * @param q the number of non-seasonal moving-average coefficients.
     * @param P the number of seasonal autoregressive coefficients.
     * @param D the degree of seasonal differencing.
     * @param Q the number of seasonal moving-average coefficients.
     * @param constant determines whether or not a constant (or mean) is fitted to the model.
     */
    public ModelOrder(final int p, final int d, final int q, final int P, final int D, final int Q,
        final boolean constant) {
      this.p = p;
      this.d = d;
      this.q = q;
      this.P = P;
      this.D = D;
      this.Q = Q;
      this.constant = (constant == true) ? 1 : 0;
    }

    // This returns the total number of nonseasonal and seasonal ARMA parameters.
    private final int sumARMA() {
      return this.p + this.q + this.P + this.Q;
    }
  }

  /**
   * Represents the autoregressive and moving-average cofficients, as well as the degree of differencing and the mean
   * for a seasonal ARIMA model.
   * 
   * @author Jacob Rachiele
   *
   */
  public static final class ModelCoefficients {

    private final double[] arCoeffs;
    private final double[] maCoeffs;
    private final double[] sarCoeffs;
    private final double[] smaCoeffs;
    private final int d;
    private final int D;
    private final double mean;

    /**
     * Create a structure holding the coefficients of an ARIMA model, the degrees of differencing, and the model mean.
     * 
     * @param arCoeffs the non-seasonal autoregressive coefficients.
     * @param maCoeffs the non-seasonal moving-average coefficients.
     * @param sarCoeffs the seasonal autoregressive coefficients.
     * @param smaCoeffs the seasonal moving-average coefficients.
     * @param d the non-seasonal degree of differencing.
     * @param D the seasonal degree of differencing.
     * @param mean the process mean.
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
    }

    private ModelCoefficients(Builder builder) {
      this.arCoeffs = builder.arCoeffs.clone();
      this.maCoeffs = builder.maCoeffs.clone();
      this.sarCoeffs = builder.sarCoeffs.clone();
      this.smaCoeffs = builder.smaCoeffs.clone();
      this.d = builder.d;
      this.D = builder.D;
      this.mean = builder.mean;
    }
    
    public final double[] arCoeffs() {
      return arCoeffs.clone();
    }

    public final double[] maCoeffs() {
      return maCoeffs.clone();
    }

    public final double[] sarCoeffs() {
      return sarCoeffs.clone();
    }

    public final double[] smaCoeffs() {
      return smaCoeffs.clone();
    }

    public final int d() {
      return d;
    }

    public final int D() {
      return D;
    }

    public final double getMean() {
      return mean;
    }

    /**
     * The order of the ARIMA model corresponding to the model coefficients.
     * 
     * @return the order of the ARIMA model corresponding to the model coefficients.
     */
    public final ModelOrder extractModelOrder() {
      return new ModelOrder(arCoeffs.length, d, maCoeffs.length, sarCoeffs.length, D, smaCoeffs.length,
          (Math.abs(mean) > 1E-8));
    }

    /**
     * Get a new builder for a ModelCoefficients object.
     * 
     * @return a new builder for a ModelCoefficients object.
     */
    public static final Builder newBuilder() {
      return new Builder();
    }

    public static final class Builder {
      private double[] arCoeffs = new double[] {};
      private double[] maCoeffs = new double[] {};
      private double[] sarCoeffs = new double[] {};
      private double[] smaCoeffs = new double[] {};
      private int d = 0;
      private int D = 0;
      private double mean = 0.0;

      private Builder() {
      }

      public Builder setArCoeffs(double... arCoeffs) {
        this.arCoeffs = arCoeffs;
        return this;
      }

      public Builder setSarCoeffs(double... sarCoeffs) {
        this.sarCoeffs = sarCoeffs;
        return this;
      }

      public Builder setMaCoeffs(double... maCoeffs) {
        this.maCoeffs = maCoeffs;
        return this;
      }

      public Builder setSmaCoeffs(double... smaCoeffs) {
        this.smaCoeffs = smaCoeffs;
        return this;
      }

      public Builder setDiff(int d) {
        this.d = d;
        return this;
      }

      public Builder setSeasDiff(int D) {
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

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + D;
      result = prime * result + Arrays.hashCode(arCoeffs);
      result = prime * result + d;
      result = prime * result + Arrays.hashCode(maCoeffs);
      long temp;
      temp = Double.doubleToLongBits(mean);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      result = prime * result + Arrays.hashCode(sarCoeffs);
      result = prime * result + Arrays.hashCode(smaCoeffs);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      ModelCoefficients other = (ModelCoefficients) obj;
      if (D != other.D) return false;
      if (!Arrays.equals(arCoeffs, other.arCoeffs)) return false;
      if (d != other.d) return false;
      if (!Arrays.equals(maCoeffs, other.maCoeffs)) return false;
      if (Double.doubleToLongBits(mean) != Double.doubleToLongBits(other.mean)) return false;
      if (!Arrays.equals(sarCoeffs, other.sarCoeffs)) return false;
      if (!Arrays.equals(smaCoeffs, other.smaCoeffs)) return false;
      return true;
    }

    @Override
    public String toString() {
      return "ModelCoefficients [arCoeffs=" + Arrays.toString(arCoeffs) + ", maCoeffs=" + Arrays.toString(maCoeffs)
              + ", sarCoeffs=" + Arrays.toString(sarCoeffs) + ", smaCoeffs=" + Arrays.toString(smaCoeffs) + ", d=" + d
              + ", D=" + D + ", mean=" + mean + "]";
    }
  }

  /**
   * A numerical description of an ARIMA model.
   * 
   * @author Jacob Rachiele
   *
   */
  static final class ModelInformation {
    private final double sigma2;
    private final double logLikelihood;
    private final double[] residuals;
    private final double[] fitted;

    /**
     * Construct a new object with the provided set of data.
     * 
     * @param sigma2 an estimate of the model variance.
     * @param logLikelihood the natural logarithms of the likelihood of the model parameters.
     * @param residuals the difference between the observations and the fitted values.
     * @param fitted the values fitted by the model to the data.
     */
    ModelInformation(final double sigma2, final double logLikelihood, final double[] residuals, final double[] fitted) {
      this.sigma2 = sigma2;
      this.logLikelihood = logLikelihood;
      this.residuals = residuals.clone();
      this.fitted = fitted.clone();
    }
  }

  static final class OptimFunction extends AbstractMultivariateFunction {

    private final TimeSeries differencedSeries;
    private final ModelOrder order;
    private final FittingStrategy fittingStrategy;
    private final int seasonalFrequency;
    private final double[] arParams;
    private final double[] maParams;
    private final double[] sarParams;
    private final double[] smaParams;

    OptimFunction(final TimeSeries differencedSeries, final ModelOrder order, final FittingStrategy fittingStrategy,
        final int seasonalFrequency) {
      this.differencedSeries = differencedSeries;
      this.order = order;
      this.fittingStrategy = fittingStrategy;
      this.seasonalFrequency = seasonalFrequency;
      this.arParams = new double[order.p];
      this.maParams = new double[order.q];
      this.sarParams = new double[order.P];
      this.smaParams = new double[order.Q];
    }

    @Override
    public final double at(final Vector point) {
      functionEvaluations++;
      final double[] params = point.elements();
      for (int i = 0; i < order.p; i++) {
        arParams[i] = params[i];
      }
      for (int i = 0; i < order.q; i++) {
        maParams[i] = params[i + order.p];
      }
      for (int i = 0; i < order.P; i++) {
        sarParams[i] = params[i + order.p + order.q];
      }
      for (int i = 0; i < order.Q; i++) {
        smaParams[i] = params[i + order.p + order.q + order.Q];
      }
      final double[] arCoeffs = Arima.expandArCoefficients(arParams, sarParams, seasonalFrequency);
      final double[] maCoeffs = Arima.expandMaCoefficients(maParams, smaParams, seasonalFrequency);
      final double mean = (order.constant == 1) ? params[params.length - 1] : 0.0;
      final ModelInformation info = (fittingStrategy == FittingStrategy.CSS)
          ? Arima.fitCss(differencedSeries, arCoeffs, maCoeffs, mean)
          : Arima.fitUss(differencedSeries, arCoeffs, maCoeffs, mean);
      return 0.5 * Math.log(info.sigma2);
    }
  }
}
