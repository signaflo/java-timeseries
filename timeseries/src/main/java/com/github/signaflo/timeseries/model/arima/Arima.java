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

import lombok.NonNull;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.forecast.Forecast;
import com.github.signaflo.timeseries.model.Model;

/**
 * A seasonal autoregressive integrated moving average (ARIMA) model.
 */
public interface Arima extends Model {
    /**
     * Create a new ARIMA model from the given observations and model order. This constructor sets the
     * model {@link FittingStrategy} to conditional sum-of-squares with maximum likelihood
     * and the seasonal cycle to one year.
     *
     * @param observations the time series of observations.
     * @param order        the order of the ARIMA model.
     *
     * @return a new ARIMA model from the given observations and model order.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaOrder order) {
        return new ArimaModel(observations, order, TimePeriod.oneYear(), FittingStrategy.CSSML);
    }

    /**
     * Create a new ARIMA model from the given observations, model order, and seasonal cycle. This method sets the
     * model {@link FittingStrategy} to conditional sum-of-squares with maximum likelihood.
     *
     * @param observations  the time series of observations.
     * @param order         the order of the ARIMA model.
     * @param seasonalCycle the amount of time it takes for the seasonal pattern to complete one cycle. For example,
     *                      monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
     *                      However, a seasonal cycle may be an arbitrary amount of time.
     *
     * @return a new ARIMA model from the given observations, model order, and seasonal cycle.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaOrder order, @NonNull TimePeriod seasonalCycle) {
        return new ArimaModel(observations, order, seasonalCycle, FittingStrategy.CSSML);
    }

    /**
     * Create a new ARIMA model from the given observations, model order, and fitting strategy. This method sets the
     * seasonal cycle to one year.
     *
     * @param observations    the time series of observations.
     * @param order           the order of the ARIMA model.
     * @param fittingStrategy the strategy to use to fit the model to the data. Maximum-likelihood estimates are
     *                        typically preferred for greater precision and accuracy, but take longer to obtain than
     *                        conditional sum-of-squares estimates.
     *
     * @return a new ARIMA model from the given observations, model order, and fitting strategy.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaOrder order,
                       @NonNull FittingStrategy fittingStrategy) {
        return new ArimaModel(observations, order, TimePeriod.oneYear(), fittingStrategy);
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
     *                        conditional sum-of-squares estimates.
     *
     * @return a new ARIMA model from the given observations, model order, seasonal cycle, and fitting strategy.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaOrder order, @NonNull TimePeriod seasonalCycle,
                            @NonNull FittingStrategy fittingStrategy) {
        return new ArimaModel(observations, order, seasonalCycle, fittingStrategy);
    }

    /**
     * Create a new ARIMA model from the given observations, model coefficients, and fitting strategy. This constructor
     * sets the seasonal cycle to one year.
     *
     * @param observations    the time series of observations.
     * @param coeffs          the coefficients of the model.
     * @param fittingStrategy the strategy to use to fit the model to the data. Maximum-likelihood estimates are
     *                        typically preferred for greater precision and accuracy, but take longer to obtain than
     *                        conditional sum-of-squares estimates.
     *
     * @return a new ARIMA model from the given observations, model coefficients, and fitting strategy.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaCoefficients coeffs,
                       @NonNull FittingStrategy fittingStrategy) {
        return new ArimaModel(observations, coeffs, TimePeriod.oneYear(), fittingStrategy);
    }

    /**
     * Create a new ARIMA model from the given observations, model coefficients, and seasonal cycle. This constructor
     * sets the model {@link FittingStrategy} to conditional sum-of-squares with maximum likelihood.
     *
     * @param observations  the time series of observations.
     * @param coeffs        the coefficients of the model.
     * @param seasonalCycle the amount of time it takes for the seasonal pattern to complete one cycle. For example,
     *                      monthly data usually has a cycle of one year, hourly data a cycle of one day, etc...
     *                      However, a seasonal cycle may be an arbitrary amount of time.
     *
     * @return a new ARIMA model from the given observations, model coefficients, and seasonal cycle.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaCoefficients coeffs,
                       @NonNull TimePeriod seasonalCycle) {
        return new ArimaModel(observations, coeffs, seasonalCycle, FittingStrategy.CSSML);
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
     *                        conditional sum-of-squares estimates.
     *
     * @return a new ARIMA model from the given observations, model coefficients, seasonal cycle, and fitting strategy.
     */
    static Arima model(@NonNull TimeSeries observations, @NonNull ArimaCoefficients coeffs,
                       @NonNull TimePeriod seasonalCycle, @NonNull FittingStrategy fittingStrategy) {
        return new ArimaModel(observations, coeffs, seasonalCycle, fittingStrategy);
    }

    @Override
    Forecast forecast(int steps, double alpha);

    @Override
    TimeSeries observations();

    @Override
    TimeSeries fittedSeries();

    @Override
    TimeSeries predictionErrors();

    /**
     * Get the estimate of the process variance.
     *
     * @return the estimate of the process variance.
     */
    double sigma2();

    /**
     * Get the frequency of observations per seasonal cycle.
     *
     * @return the frequency of observations per seasonal cycle.
     */
    int seasonalFrequency();

    /**
     * Get the standard errors of the model coefficients.
     *
     * @return the standard errors of the model coefficients.
     */
    double[] stdErrors();

    /**
     * Get the coefficients of this ARIMA model.
     *
     * @return the coefficients of this ARIMA model.
     */
    ArimaCoefficients coefficients();

    /**
     * Get the order of this ARIMA model.
     *
     * @return the order of this ARIMA model.
     */
    ArimaOrder order();

    /**
     * Get the natural logarithm of the likelihood of the model given the data.
     *
     * @return the natural logarithm of the likelihood of the model given the data.
     */
    double logLikelihood();

    /**
     * Get the Akaike Information Criterion (AIC) for this model. The AIC is defined as 2k &minus;
     * 2L where k is the number of parameters in the model and L is the logarithm of the likelihood.
     *
     * @return the Akaike Information Criterion (AIC) for this model.
     */
    double aic();

    /**
     * Check whether the model is stationary, which is the case if the roots of the autoregressive
     * characteristic polynomial lie outside the unit circle.
     *
     * @return true if the model is stationary, false otherwise.
     */
    boolean isStationary();

    /**
     * Check whether the model is invertible, which is the case if the roots of the moving average
     * characteristic polynomial lie outside the unit circle.
     *
     * @return true if the model is invertible, false otherwise.
     */
    boolean isInvertible();

    /**
     * An indicator for whether an ARIMA model has a constant term.
     */
    enum Constant {
        INCLUDE(1), EXCLUDE(0);

        private final int constant;

        Constant(int constant) {
            this.constant = constant;
        }

        public int asInt() {
            return constant;
        }

        public boolean include() {
            return this == INCLUDE;
        }
    }

    /**
     * An indicator for whether an ARIMA model has a drift term.
     */
    enum Drift {
        INCLUDE(1), EXCLUDE(0);

        private final int drift;

        Drift(int drift) {
            this.drift = drift;
        }

        public int asInt() {
            return drift;
        }

        public boolean include() {
            return this == INCLUDE;
        }
    }

    /**
     * The strategy to be used for fitting an ARIMA model.
     *
     * @author Jacob Rachiele
     */
    enum FittingStrategy {

        CSS("conditional sum-of-squares"),

        ML("maximum likelihood"),

        CSSML("conditional sum-of-squares, then maximum likelihood");

        private final String description;

        FittingStrategy(final String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }

    }
}
