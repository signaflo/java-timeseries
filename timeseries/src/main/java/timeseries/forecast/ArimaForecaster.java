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

package timeseries.forecast;

import math.stats.distributions.Normal;
import timeseries.TimeSeries;
import timeseries.model.arima.Arima;
import timeseries.operators.LagPolynomial;

import static java.lang.Math.sqrt;

/**
 * A forecaster for ARIMA model.
 */
class ArimaForecaster implements Forecaster {

    private final Arima model;


    private ArimaForecaster(Arima model) {
        this.model = model;
    }

    /**
     * Create a new forecaster from the provided ARIMA model.
     *
     * @param model the model to be used by the forecaster.
     * @return a new forecaster.
     */
    static ArimaForecaster from(Arima model) {
        return new ArimaForecaster(model);
    }

    @Override
    public Forecast forecast(int steps, double alpha) {
        TimeSeries pointForecasts = computePointForecasts(steps);
        TimeSeries lowerValues = computeLowerPredictionBounds(pointForecasts, steps, alpha);
        TimeSeries upperValues = computeUpperPredictionBounds(pointForecasts, steps, alpha);
        return new ArimaForecast(pointForecasts, lowerValues, upperValues, alpha);
    }

    @Override
    public TimeSeries computeUpperPredictionBounds(TimeSeries observations, final int steps, final double alpha) {
        TimeSeries forecast = computePointForecasts(steps);
        final double criticalValue = new Normal().quantile(1 - alpha / 2);
        double[] upperPredictionValues = new double[steps];
        double[] errors = getStdErrors(forecast, criticalValue);
        for (int t = 0; t < steps; t++) {
            upperPredictionValues[t] = forecast.at(t) + errors[t];
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), upperPredictionValues);
    }

    @Override
    public TimeSeries computeLowerPredictionBounds(TimeSeries forecast, final int steps, final double alpha) {
        final double criticalValue = new Normal().quantile(alpha / 2);
        double[] lowerPredictionValues = new double[steps];
        double[] errors = getStdErrors(forecast, criticalValue);
        for (int t = 0; t < steps; t++) {
            lowerPredictionValues[t] = forecast.at(t) + errors[t];
        }
        return TimeSeries.from(forecast.timePeriod(), forecast.observationTimes().get(0), lowerPredictionValues);
    }

    @Override
    public TimeSeries computePointForecasts(int steps) {
        return model.forecast(steps);
    }

    private double[] getStdErrors(TimeSeries forecast, final double criticalValue) {
        double[] psiCoeffs = getPsiCoefficients(forecast);
        double[] stdErrors = new double[forecast.size()];
        double sigma = sqrt(model.sigma2());
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
        LagPolynomial arPoly = LagPolynomial.autoRegressive(model.coefficients().getAllAutoRegressiveCoefficients());
        LagPolynomial diffPoly = LagPolynomial.differences(model.order().d());
        LagPolynomial seasDiffPoly = LagPolynomial.seasonalDifferences(model.seasonalFrequency(), model.order().D());
        double[] phi = diffPoly.times(seasDiffPoly).times(arPoly).inverseParams();
        double[] theta = model.coefficients().getAllMovingAverageCoefficients();
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

}
