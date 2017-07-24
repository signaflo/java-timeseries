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

import math.operations.DoubleFunctions;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import math.stats.distributions.Distribution;
import math.stats.distributions.Normal;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.operators.LagPolynomial;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * An ARIMA model simulation.
 */
@EqualsAndHashCode
@ToString
public class ArimaSimulation {

    private final ArimaCoefficients coefficients;
    private final Distribution distribution;
    private final TimePeriod period;
    private final TimePeriod seasonalCycle;
    private final int n;

    private ArimaSimulation(Builder builder) {
        this.coefficients = builder.coefficients;
        this.distribution = builder.distribution;
        this.period = builder.period;
        this.seasonalCycle = builder.seasonalCycle;
        this.n = builder.n;
    }

    /**
     * Get a new builder for an ARIMA simulation.
     *
     * @return a new builder for an ARIMA simulation.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Simulate the ARIMA model and return the resulting time series.
     *
     * @return the simulated time series.
     */
    public TimeSeries sim() {
        final int burnin = (int) (n / 2.0);
        final int seasonalFrequency = (int) period.frequencyPer(seasonalCycle);
        double[] arSarCoeffs = ArimaCoefficients.expandArCoefficients(coefficients.arCoeffs(), coefficients.seasonalARCoeffs(),
                                                                      seasonalFrequency);
        double[] maSmaCoeffs = ArimaCoefficients.expandMaCoefficients(coefficients.maCoeffs(), coefficients.seasonalMACoeffs(),
                                                                      seasonalFrequency);
        int diffOffset = coefficients.d() + coefficients.D() * seasonalFrequency;
        int offset = Math.min(n, arSarCoeffs.length);
        double[] series = new double[n + burnin];
        double[] errors = new double[n + burnin];
        for (int t = 0; t < offset; t++) {
            series[t] = errors[t] = distribution.rand();
            series[t] += coefficients.mean();
            for (int j = 0; j < Math.min(t, maSmaCoeffs.length); j++) {
                series[t] += maSmaCoeffs[j] * errors[t - j - 1];
            }
        }

        int end;
        for (int t = offset; t < n + burnin; t++) {
            series[t] = errors[t] = distribution.rand();
            series[t] += coefficients.mean();
            end = Math.min(t, arSarCoeffs.length);
            for (int j = 0; j < end; j++) {
                series[t] += arSarCoeffs[j] * (series[t - j - 1] - coefficients.mean());
            }
            end = Math.min(t, maSmaCoeffs.length);
            for (int j = 0; j < end; j++) {
                series[t] += maSmaCoeffs[j] * errors[t - j - 1];
            }
        }

        LagPolynomial poly = LagPolynomial.differences(coefficients.d())
                                          .times(LagPolynomial.seasonalDifferences(seasonalFrequency,
                                                                                   coefficients.D()));
        end = n + burnin;
        for (int t = diffOffset; t < end; t++) {
            series[t] += poly.fit(series, t);
        }
        series = DoubleFunctions.slice(series, burnin, n + burnin);
        return TimeSeries.from(period, OffsetDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0)), series);
    }

    /**
     * An ARIMA simulation builder.
     */
    public static class Builder {

        private int defaultSimulationSize = 500;
        private ArimaCoefficients coefficients = ArimaCoefficients.newBuilder().build();
        private Distribution distribution = new Normal();
        private TimePeriod period = (coefficients.isSeasonal()) ? TimePeriod.oneMonth() : TimePeriod.oneYear();
        private TimePeriod seasonalCycle = TimePeriod.oneYear();
        private int n = defaultSimulationSize;
        private boolean periodSet = false;

        /**
         * Set the model coefficients to be used in simulating the ARIMA model.
         *
         * @param coefficients the model coefficients for the simulation.
         * @return this builder.
         */
        public Builder setCoefficients(ArimaCoefficients coefficients) {
            if (coefficients == null) {
                throw new NullPointerException("The model coefficients cannot be null.");
            }
            this.coefficients = coefficients;
            if (!periodSet) {
                this.period = (coefficients.isSeasonal()) ? TimePeriod.oneMonth() : TimePeriod.oneYear();
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
         *
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
         * Set the time cycle at which the seasonal pattern of the simulated time series repeats. This defaults
         * to one year.
         *
         * @param seasonalCycle the time cycle at which the seasonal pattern of the simulated time series repeats.
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
                throw new IllegalArgumentException(
                        "the number of observations to simulate must be a positive integer.");
            }
            this.n = n;
            return this;
        }

        /**
         * Simulate the time series directly from this builder. This is equivalent to calling build on this builder,
         * then sim on the returned ArimaSimulation object.
         *
         * @return the simulated time series.
         */
        public TimeSeries sim() {
            return new ArimaSimulation(this).sim();
        }

        /**
         * Construct and return a new fully built and immutable ArimaSimulation object.
         *
         * @return a new fully built and immutable ArimaSimulation object.
         */
        public ArimaSimulation build() {
            return new ArimaSimulation(this);
        }
    }
}
