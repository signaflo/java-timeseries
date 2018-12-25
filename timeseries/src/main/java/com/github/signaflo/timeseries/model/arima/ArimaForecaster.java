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

package com.github.signaflo.timeseries.model.arima;

import static com.github.signaflo.math.operations.DoubleFunctions.fill;
import static java.lang.Math.sqrt;

import com.github.signaflo.data.Range;
import com.github.signaflo.math.linear.doubles.Matrix;
import com.github.signaflo.math.linear.doubles.Vector;
import com.github.signaflo.math.operations.DoubleFunctions;
import com.github.signaflo.math.stats.distributions.Normal;
import com.github.signaflo.timeseries.Time;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.forecast.Forecaster;
import com.github.signaflo.timeseries.operators.LagPolynomial;

/**
 * A forecaster for ARIMA model.
 */
class ArimaForecaster implements Forecaster {

  private final TimeSeries observations;
  private final ArimaCoefficients coefficients;
  private final ArimaOrder order;
  private final TimeSeries differencedSeries;
  private final TimeSeries residuals;
  private final Matrix regressionMatrix;
  private final double sigma2;


  private ArimaForecaster(TimeSeries observations, ArimaCoefficients coefficients, ArimaOrder order,
                          TimeSeries differencedSeries, TimeSeries residuals,
                          Matrix regressionMatrix, double sigma2) {
    this.observations = observations;
    this.coefficients = coefficients;
    this.order = order;
    this.differencedSeries = differencedSeries;
    this.residuals = residuals;
    this.regressionMatrix = regressionMatrix;
    this.sigma2 = sigma2;
  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public Forecast forecast(int steps, double alpha) {
    TimeSeries pointForecasts = computePointForecasts(steps);
    TimeSeries lowerValues = computeLowerPredictionBounds(pointForecasts, steps, alpha);
    TimeSeries upperValues = computeUpperPredictionBounds(pointForecasts, steps, alpha);
    return new ArimaForecast(pointForecasts, lowerValues, upperValues, alpha);
  }

  @Override
  public TimeSeries computeUpperPredictionBounds(TimeSeries observations, final int steps,
                                                 final double alpha) {
    TimeSeries forecast = computePointForecasts(steps);
    final double criticalValue = new Normal().quantile(1 - alpha / 2);
    double[] upperPredictionValues = new double[steps];
    double[] errors = getStdErrors(forecast, criticalValue);
    for (int t = 0; t < steps; t++) {
      upperPredictionValues[t] = forecast.at(t) + errors[t];
    }
    return TimeSeries
        .from(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
  }

  @Override
  public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, final int steps,
                                                 final double alpha) {
    final double criticalValue = new Normal().quantile(alpha / 2);
    double[] lowerPredictionValues = new double[steps];
    double[] errors = getStdErrors(forecast, criticalValue);
    for (int t = 0; t < steps; t++) {
      lowerPredictionValues[t] = forecast.at(t) + errors[t];
    }
    return TimeSeries
        .from(forecast.timePeriod(), forecast.observationTimes().get(0), lowerPredictionValues);
  }

  @Override
  public TimeSeries computePointForecasts(int steps) {
    final int n = observations.size();
    double[] fcst = fcst(steps);
    TimePeriod timePeriod = observations.timePeriod();
    final Time startTime = observations.observationTimes()
        .get(n - 1)
        .plus(timePeriod);
    return TimeSeries.from(timePeriod, startTime, fcst);
  }

  /**
   * Compute point forecasts for the given number of steps ahead and return the result in a
   * primitive array.
   *
   * @param steps the number of time periods ahead to forecast.
   *
   * @return point forecasts for the given number of steps ahead.
   */
  public double[] fcst(final int steps) {
    final int d = this.order.d();
    final int D = this.order.D();
    final int n = this.differencedSeries.size();
    final int m = this.observations.size();
    final int seasonalFrequency = coefficients.seasonalFrequency();
    final double[] arSarCoeffs = coefficients.getAllAutoRegressiveCoefficients();
    final double[] maSmaCoeffs = coefficients.getAllMovingAverageCoefficients();
    final double[] resid = this.residuals.asArray();
    final double[] diffedFcst = new double[n + steps];
    final double[] fcst = new double[m + steps];

    Vector regressionParameters = Vector.from(
        this.coefficients.getRegressors(this.order));
    Vector regressionEffects = regressionMatrix.times(regressionParameters);

    TimeSeries armaSeries = this.observations.minus(regressionEffects.elements());
    TimeSeries differencedSeries = armaSeries.difference(1, this.order.d()).difference(
        seasonalFrequency, this.order.D());
    System.arraycopy(differencedSeries.asArray(), 0, diffedFcst, 0, n);
    System.arraycopy(armaSeries.asArray(), 0, fcst, 0, m);

    LagPolynomial diffPolynomial = LagPolynomial.differences(d);
    LagPolynomial seasDiffPolynomial = LagPolynomial.seasonalDifferences(seasonalFrequency, D);
    LagPolynomial lagPolynomial = diffPolynomial.times(seasDiffPolynomial);
    for (int t = 0; t < steps; t++) {
      fcst[m + t] = lagPolynomial.solve(fcst, m + t);
      for (int i = 0; i < arSarCoeffs.length; i++) {
        diffedFcst[n + t] += arSarCoeffs[i] * diffedFcst[n + t - i - 1];
        fcst[m + t] += arSarCoeffs[i] * diffedFcst[n + t - i - 1];
      }
      for (int j = maSmaCoeffs.length; j > 0 && t < j; j--) {
        diffedFcst[n + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
        fcst[m + t] += maSmaCoeffs[j - 1] * resid[m + t - j];
      }
    }

    Matrix forecastRegressionMatrix = this.getForecastRegressionMatrix(steps, this.order);
    Vector forecastRegressionEffects = forecastRegressionMatrix.times(regressionParameters);
    Vector forecast = Vector.from(DoubleFunctions.slice(fcst, m, m + steps));
    return forecast.plus(forecastRegressionEffects).elements();
  }

  private double[] getStdErrors(TimeSeries forecast, final double criticalValue) {
    double[] psiCoeffs = getPsiCoefficients(forecast);
    double[] stdErrors = new double[forecast.size()];
    double sigma = sqrt(sigma2);
    double sd;
    double psiWeightSum = 0.0;
    for (int i = 0; i < stdErrors.length; i++) {
      psiWeightSum += psiCoeffs[i] * psiCoeffs[i];
      sd = sigma * sqrt(psiWeightSum);
      stdErrors[i] = criticalValue * sd;
    }
    return stdErrors;
  }

  private double[] getPsiCoefficients(TimeSeries forecast) {
    final int steps = forecast.size();
    LagPolynomial arPoly = LagPolynomial.autoRegressive(
        coefficients.getAllAutoRegressiveCoefficients());
    LagPolynomial diffPoly = LagPolynomial.differences(order.d());
    LagPolynomial seasDiffPoly = LagPolynomial.seasonalDifferences(
        coefficients.seasonalFrequency(), order.D());

    double[] phi = diffPoly.times(seasDiffPoly).times(arPoly).inverseParams();
    double[] theta = coefficients.getAllMovingAverageCoefficients();
    final double[] psi = new double[steps];
    psi[0] = 1.0;

    System.arraycopy(theta, 0, psi, 1, Math.min(steps - 1, theta.length));
    for (int j = 1; j < psi.length; j++) {
      for (int i = 0; i < Math.min(j, phi.length); i++) {
        psi[j] += psi[j - i - 1] * phi[i];
      }
    }
    return psi;
  }

  private Matrix getForecastRegressionMatrix(int steps, ArimaOrder order) {
    double[][] matrix = new double[order.numRegressors()][steps];
    if (order.constant().include()) {
      matrix[0] = fill(steps, 1.0);
    }
    if (order.drift().include()) {
      int startTime = this.observations.size() + 1;
      matrix[order.constant().asInt()] = Range.inclusiveRange(startTime, startTime + steps)
          .asArray();
    }
    return Matrix.create(Matrix.Layout.BY_COLUMN, matrix);
  }

  static class Builder {

    private TimeSeries observations;
    private ArimaCoefficients coefficients;
    private ArimaOrder order;
    private TimeSeries differencedSeries;
    private TimeSeries residuals;
    private Matrix regressionMatrix;
    private double sigma2;

    public Builder setObservations(TimeSeries observations) {
      this.observations = observations;
      return this;
    }

    public Builder setCoefficients(ArimaCoefficients coefficients) {
      this.coefficients = coefficients;
      return this;
    }

    public Builder setOrder(ArimaOrder order) {
      this.order = order;
      return this;
    }

    public Builder setDifferencedSeries(TimeSeries differencedSeries) {
      this.differencedSeries = differencedSeries;
      return this;
    }

    public Builder setResiduals(TimeSeries residuals) {
      this.residuals = residuals;
      return this;
    }

    public Builder setRegressionMatrix(Matrix regressionMatrix) {
      this.regressionMatrix = regressionMatrix;
      return this;
    }

    public Builder setSigma2(double sigma2) {
      this.sigma2 = sigma2;
      return this;
    }

    public ArimaForecaster build() {
      return new ArimaForecaster(observations, coefficients, order, differencedSeries, residuals,
                                 regressionMatrix, sigma2);
    }
  }
}
