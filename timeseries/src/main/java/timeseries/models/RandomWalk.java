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
package timeseries.models;

import lombok.NonNull;
import math.stats.distributions.Distribution;
import math.stats.distributions.Normal;
import timeseries.TimePeriod;
import timeseries.TimeSeries;

import java.time.OffsetDateTime;

/**
 * A model for a random walk process. Some important characteristics of the random walk are that the
 * process variance increases with time (non-stationarity) and that the optimal forecast
 * at any point in the future is equal to the last observed value.
 *
 * @author Jacob Rachiele
 */
public final class RandomWalk implements Model {

    private final TimeSeries timeSeries;
    private final TimeSeries fittedSeries;
    private final TimeSeries residuals;

    /**
     * Create a new random walk model from the given time series of observations.
     *
     * @param observed the observed series.
     */
    public RandomWalk(@NonNull final TimeSeries observed) {
        if (observed.size() < 1) {
            throw new IllegalArgumentException("A random walk model requires at least one observation.");
        }
        this.timeSeries = observed;
        this.fittedSeries = fitSeries();
        this.residuals = calculateResiduals();
    }

    /**
     * Simulate a random walk assuming that the errors, or random shocks, follow the given Distribution.
     *
     * @param dist The probability distribution that observations are drawn from.
     * @param n    The number of observations to simulate.
     * @return the simulated series.
     */
    public static TimeSeries simulate(@NonNull final Distribution dist, final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("the number of observations to simulate must be a positive integer.");
        }
        final double[] series = new double[n];
        series[0] = dist.rand();
        for (int t = 1; t < n; t++) {
            series[t] = series[t - 1] + dist.rand();
        }
        return TimeSeries.from(series);
    }

    /**
     * Simulate a random walk assuming errors follow a Normal (Gaussian) Distribution with the given mean and standard
     * deviation.
     *
     * @param mean  the mean of the Normal distribution the observations are drawn from.
     * @param sigma the standard deviation of the Normal distribution the observations are drawn from.
     * @param n     the number of observations to simulate.
     * @return the simulated series.
     */
    public static TimeSeries simulate(final double mean, final double sigma, final int n) {
        final Distribution dist = new Normal(mean, sigma);
        return simulate(dist, n);
    }

    /**
     * Simulate a random walk assuming errors follow a Normal (Gaussian) Distribution with zero mean and with the
     * provided standard deviation.
     *
     * @param sigma the standard deviation of the Normal distribution the observations are drawn from.
     * @param n     the number of observations to simulate.
     * @return the simulated series.
     */
    public static TimeSeries simulate(final double sigma, final int n) {
        final Distribution dist = new Normal(0, sigma);
        return simulate(dist, n);
    }

    /**
     * Simulate a random walk assuming errors follow a standard Normal (Gaussian) Distribution.
     *
     * @param n the number of observations to simulate.
     * @return the simulated series.
     */
    public static TimeSeries simulate(final int n) {
        final Distribution dist = new Normal(0, 1);
        return simulate(dist, n);
    }

    @Override
    public TimeSeries pointForecast(final int steps) {
        int n = timeSeries.size();
        TimePeriod timePeriod = timeSeries.timePeriod();
        final OffsetDateTime startTime = timeSeries.observationTimes().get(n - 1)
                                                   .plus(timePeriod.periodLength() * timePeriod.timeUnit().unitLength(),
                                                         timePeriod.timeUnit().temporalUnit());
        double[] forecast = new double[steps];
        for (int t = 0; t < steps; t++) {
            forecast[t] = timeSeries.at(n - 1);
        }
        return TimeSeries.from(timePeriod, startTime, forecast);
    }

    @Override
    public TimeSeries timeSeries() {
        return this.timeSeries;
    }

    @Override
    public TimeSeries fittedSeries() {
        return this.fittedSeries;
    }

    @Override
    public TimeSeries predictionErrors() {
        return this.residuals;
    }

    private TimeSeries fitSeries() {
        final double[] fitted = new double[timeSeries.size()];
        fitted[0] = timeSeries.at(0);
        for (int t = 1; t < timeSeries.size(); t++) {
            fitted[t] = timeSeries.at(t - 1);
        }
        return TimeSeries.from(timeSeries.timePeriod(), timeSeries.observationTimes().get(0), fitted);
    }

    private TimeSeries calculateResiduals() {
        final double[] residuals = new double[timeSeries.size()];
        for (int t = 1; t < timeSeries.size(); t++) {
            residuals[t] = timeSeries.at(t) - fittedSeries.at(t);
        }
        return TimeSeries.from(timeSeries.timePeriod(), timeSeries.observationTimes().get(0), residuals);
    }

    @Override
    public String toString() {
        return "Random walk time series model";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RandomWalk that = (RandomWalk) o;

        if (timeSeries != null ? !timeSeries.equals(that.timeSeries) : that.timeSeries != null) return false;
        if (!fittedSeries.equals(that.fittedSeries)) return false;
        return residuals.equals(that.residuals);
    }

    @Override
    public int hashCode() {
        int result = timeSeries != null ? timeSeries.hashCode() : 0;
        result = 31 * result + fittedSeries.hashCode();
        result = 31 * result + residuals.hashCode();
        return result;
    }
}
