/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 * This file is licensed under the Apache License, Version 2.0, http://www.apache.org/licenses/LICENSE-2.0
 */

package timeseries.models;

import static stats.Statistics.sumOfSquared;
import static stats.Statistics.sumOf;
import static data.DoubleFunctions.slice;

import timeseries.TimeSeries;
import timeseries.models.arima.FittingStrategy;
import timeseries.TimePeriod;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.XYStyler;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.markers.Circle;
import org.knowm.xchart.style.markers.None;

import data.Operators;

/**
 * A potentially seasonal autoregressive integrated moving average (ARIMA) model. This class is immutable and
 * thread-safe.
 * 
 * @author Jacob Rachiele
 *
 */
public final class Arima {

  private final TimeSeries observations;
  private final TimeSeries diffedSeries;
  private final TimeSeries fitted;
  private final TimeSeries residuals;
  private final double sigma2;
  private final int seasonalFrequency;
  
  // The number of parameters, degree of differencing, and constant flag.
  private final ModelOrder order;
  private final ModelCoefficients coeffs;
  private final double mean;
  
  // The intercept is equal to mean * (1 - (sum of AR coefficients))
  private final double intercept;
  private final double[] arCoeffs;
  private final double[] maCoeffs;

  // Note: no need to copy since TimeSeries and ModelOrder are immutable;
  /**
   * Create a new ARIMA model from the given observations, model coefficients, and seasonal cycle.
   * 
   * @param observations the time series of observations.
   * @param coeffs the parameter coefficients of the model.
   * @param seasonalCycle the amount of time it takes for the seasonal part of the model to complete one cycle,
   *        <i>relative to</i> a particular unit of time. For example, typical monthly data has a cycle of one year,
   *        hourly data likely has a cycle of one day, etc... For a non-typical example, one could specify a seasonal
   *        cycle of half a year using a time period of six months, or, equivalently, two quarters.
   */
  // Arima(final TimeSeries observations, final ModelOrder order, final TimePeriod seasonalCycle) {
  // this.observations = observations;
  // this.order = order;
  // this.cycleLength = (int)(observations.timePeriod().frequencyPer(seasonalCycle));
  // double[] initialParameters = setInitialParameters();
  // System.out.println(initialParameters);
  // }

  public Arima(final TimeSeries observations, final ModelCoefficients coeffs, final TimePeriod seasonalCycle) {
    this(observations, coeffs, seasonalCycle, FittingStrategy.USS);
  }
  
  public Arima(final TimeSeries observations, final ModelCoefficients coeffs, 
      final TimePeriod seasonalCycle, final FittingStrategy fittingStrategy) {
    this.observations = observations;
    this.coeffs = coeffs;
    this.order = coeffs.extractModelOrder();
    this.seasonalFrequency = (int) (observations.timePeriod().frequencyPer(seasonalCycle));
    this.diffedSeries = observations.difference(1, order.d).difference(seasonalFrequency, order.D);
    this.arCoeffs = expandArCoefficients();
    this.maCoeffs = expandMaCoefficients();
    this.mean = coeffs.mean;
    this.intercept = mean * (1 - sumOf(arCoeffs));
    final FitStatistics fitStats;
    if (fittingStrategy == FittingStrategy.CSS) {
      fitStats = fitCss();
      this.fitted = new TimeSeries(observations.timePeriod(), observations.observationTimes(),
          fitStats.fitted);
      this.residuals = this.observations.minus(this.fitted);
      this.sigma2 = sumOfSquared(residuals.series()) / observations.n();
    }
    else {
      fitStats = fitUss();
      final double[] residuals = fitStats.residuals;
      this.sigma2 = fitStats.sigma2;
      final double[] fittedArray = integrate(Operators.differenceOf(diffedSeries.series(), 
          slice(residuals, 2 * arCoeffs.length, residuals.length)));
      for (int i = 0; i < arCoeffs.length; i++) {
        fittedArray[i] -= residuals[i + arCoeffs.length];
      }
      this.fitted = new TimeSeries(observations.timePeriod(), observations.observationTimes(),
          fittedArray);
      this.residuals = this.observations.minus(this.fitted);
      System.out.println(this.residuals.sumOfSquares());
    }
  }
  
  public final double sigma2() {
    return this.sigma2;
  }
  
  public final double logLikelihood() {
    final int n = this.observations.n();
    return (-n/2) * (Math.log(2 * Math.PI * this.sigma2) + 1);
  }
  
  private final double[] setInitialParameters() {
    // Set initial constant to the mean and all other parameters to zero.
    double[] initParams = new double[order.sumARMA() + order.constant];
    if (order.constant == 1) {
      initParams[initParams.length - 1] = diffedSeries.mean();
    }
    return initParams;
  }

  // Expand the AR coefficients by combining the non-seasonal and seasonal coefficients into a single
  // array, which takes advantage of the fact that a seasonal AR model is a special case of a non-seasonal
  // AR model with zero coefficients at the non-seasonal indices.
  final double[] expandArCoefficients() {
    double[] arCoeffs = coeffs.arCoeffs;
    double[] sarCoeffs = coeffs.sarCoeffs;
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
  final double[] expandMaCoefficients() {
    double[] maCoeffs = coeffs.maCoeffs;
    double[] smaCoeffs = coeffs.smaCoeffs;
    double[] maSmaCoeffs = new double[maCoeffs.length + smaCoeffs.length * seasonalFrequency];

    for (int i = 0; i < maCoeffs.length; i++) {
      maSmaCoeffs[i] = maCoeffs[i];
    }

    // Note that we take into account the interaction between the seasonal and non-seasonal coefficients,
    // which arises because the model's ma and sma polynomials are multiplied together.
    // In contrast to the ar polynomial, the ma and sma interaction maintains a positive sign.
    for (int i = 0; i < smaCoeffs.length; i++) {
      maSmaCoeffs[(i + 1) * seasonalFrequency - 1] = smaCoeffs[i];
      for (int j = 0; j < maCoeffs.length; j++) {
        maSmaCoeffs[(i + 1) * seasonalFrequency + j] = smaCoeffs[i] * maCoeffs[j];
      }
    }

    return maSmaCoeffs;
  }

  // public static final TimeSeries simulate(final ModelCoefficients order, final int n) {
  // final double[] series = new double[n];
  //
  // }
  
  final FitStatistics fitCss() {
    final int offset = arCoeffs.length;
    final int n = diffedSeries.n();
    
    final double[] fitted = new double[n];
    final double[] residuals = new double[n];

    for (int t = offset; t < fitted.length; t++) {
      for (int i = 0; i < this.arCoeffs.length; i++) {
        fitted[t] += mean + arCoeffs[i] * (diffedSeries.at(t - i - 1) - mean);
      }
      residuals[t] = diffedSeries.at(t) - fitted[t];
      for (int j = 0; j < this.maCoeffs.length; j++) {
        fitted[t] += maCoeffs[j] * residuals[t - j - 1];
      }
    }
    
    final double sigma2 = sumOfSquared(residuals) / diffedSeries.n();
    final double logLikelihood = (-n/2) * (Math.log(2 * Math.PI * sigma2) + 1);
    return new FitStatistics(sigma2, logLikelihood, residuals, fitted);
  }
  
  final FitStatistics fitUss() {
    int n = diffedSeries.n();
    final int m = arCoeffs.length;
    
    final double[] extendedFit = new double[2 * m + n];
    final double[] extendedSeries = new double[2 * m + n];
    final double[] residuals = new double[2 * m + n];
    for (int i = 0; i < diffedSeries.n(); i++) {
      extendedSeries[i] = diffedSeries.at(--n);
    }
    
    n = diffedSeries.n();
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
    
    n = diffedSeries.n();
    final double sigma2 = sumOfSquared(residuals) / n;
    final double logLikelihood = (-n/2) * (Math.log(2 * Math.PI * sigma2) + 1);
    return new FitStatistics(sigma2, logLikelihood, residuals, extendedFit);
  }

  /*
   * Start with the difference equation form of the model (Cryer and Chan 2008, 5.2):
   * diffedSeries_t = ArmaProcess_t
   * Then, subtracting diffedSeries_t from both sides, we have
   * ArmaProcess_t - diffedSeries_t = 0
   * Now add Y_t to both sides and rearrange terms to obtain
   * Y_t = Y_t - diffedSeries_t + ArmaProcess_t
   * Thus, our in-sample estimate of Y_t, the "integrated" series, is 
   * Y_t(hat) = Y_t - diffedSeries_t + fittedArmaProcess_t
   */
  private final double[] integrate(final double[] fitted) {
    final int offset = this.order.d + this.order.D * this.seasonalFrequency;
    final double[] integrated = new double[this.observations.n()];
    for (int t = 0; t < offset; t++) {
      integrated[t] = observations.at(t);
    }
    for (int t = offset; t < observations.n(); t++) {
      integrated[t] = observations.at(t) - diffedSeries.at(t - offset) + fitted[t - offset];
    }
    return integrated;
  }
  final double intercept() {
    return this.intercept;
  }
  
  public final void plotFit() {

    new Thread(() -> {
      final List<Date> xAxis = new ArrayList<>(fitted.observationTimes().size());
      for (OffsetDateTime dateTime : fitted.observationTimes()) {
        xAxis.add(Date.from(dateTime.toInstant()));
      }
      List<Double> seriesList = com.google.common.primitives.Doubles.asList(observations.series());
      List<Double> fittedList = com.google.common.primitives.Doubles.asList(fitted.series());
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
      final List<Date> xAxis = new ArrayList<>(fitted.observationTimes().size());
      for (OffsetDateTime dateTime : fitted.observationTimes()) {
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
   * (or intercept) for a seasonal ARIMA model.
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
     * Create a new ModelCoefficients object using the supplied coefficients, degrees of differencing, and mean.
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

    /**
     * Return the order of the ARIMA model corresponding to the model coefficients.
     * 
     * @return the order of the ARIMA model corresponding to the model coefficients.
     */
    public final ModelOrder extractModelOrder() {
      return new ModelOrder(arCoeffs.length, d, maCoeffs.length, sarCoeffs.length, D, smaCoeffs.length,
          (Math.abs(mean) > 1E-8));
    }

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

      public Builder setArCoeffs(double[] arCoeffs) {
        this.arCoeffs = arCoeffs;
        return this;
      }

      public Builder setSarCoeffs(double[] sarCoeffs) {
        this.sarCoeffs = sarCoeffs;
        return this;
      }

      public Builder setMaCoeffs(double[] maCoeffs) {
        this.maCoeffs = maCoeffs;
        return this;
      }

     public  Builder setSmaCoeffs(double[] smaCoeffs) {
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
  }
  
  static final class FitStatistics {
    private final double sigma2;
    private final double logLikelihood;
    private final double[] residuals;
    private final double[] fitted;
    
    FitStatistics(final double sigma2, final double logLikelihood, final double[] residuals, final double[] fitted) {
      this.sigma2 = sigma2;
      this.logLikelihood = logLikelihood;
      this.residuals = residuals.clone();
      this.fitted = fitted.clone();
    }
  }

}
