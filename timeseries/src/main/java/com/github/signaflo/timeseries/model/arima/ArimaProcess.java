package com.github.signaflo.timeseries.model.arima;

import com.google.common.collect.EvictingQueue;
import com.github.signaflo.math.operations.DoubleFunctions;
import com.github.signaflo.math.stats.distributions.Distribution;
import com.github.signaflo.math.stats.distributions.Normal;
import com.github.signaflo.timeseries.TimePeriod;
import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.operators.LagPolynomial;

import java.util.PrimitiveIterator;
import java.util.Queue;
import java.util.function.DoubleSupplier;

/**
 * Represents an indefinite, observation generating ARIMA process.
 *
 * @author Jacob Rachiele
 * Oct. 06, 2017
 */
public class ArimaProcess implements DoubleSupplier, PrimitiveIterator.OfDouble {

    private final ArimaCoefficients coefficients;
    private final Distribution distribution;
    private final TimePeriod period;
    private final TimePeriod seasonalCycle;

    private final LagPolynomial maPoly;
    private final LagPolynomial arPoly;
    private final LagPolynomial diffPoly;
    private final Queue<Double> diffSeries;
    private final Queue<Double> series;
    private final Queue<Double> errors;

    private ArimaProcess(Builder builder) {
        this.coefficients = builder.coefficients;
        this.distribution = builder.distribution;
        this.period = builder.period;
        this.seasonalCycle = builder.seasonalCycle;
        int seasonalFrequency = (int) builder.period.frequencyPer(builder.seasonalCycle);
        double[] arSarCoeffs = ArimaCoefficients.expandArCoefficients(coefficients.arCoeffs(),
                                                                      coefficients.seasonalARCoeffs(),

                                                                      seasonalFrequency);
        double[] maSmaCoeffs = ArimaCoefficients.expandMaCoefficients(coefficients.maCoeffs(),
                                                                      coefficients.seasonalMACoeffs(),
                                                                      seasonalFrequency);
        this.errors = EvictingQueue.create(maSmaCoeffs.length);
        this.diffSeries = EvictingQueue.create(arSarCoeffs.length);
        this.series = EvictingQueue.create(coefficients.d() + coefficients.D() * seasonalFrequency);
        this.maPoly = LagPolynomial.movingAverage(maSmaCoeffs);
        this.arPoly = LagPolynomial.autoRegressive(arSarCoeffs);
        this.diffPoly = LagPolynomial.differences(coefficients.d())
                                     .times(LagPolynomial.seasonalDifferences(seasonalFrequency, coefficients.D()));
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public double nextDouble() {
        return getAsDouble();
    }

    /**
     * Generate the next observation from this ARIMA process.
     * @return the next observation from this ARIMA process.
     */
    @Override
    public synchronized double getAsDouble() {
        double error = distribution.rand();
        double newValue = error;
        double[] series = getSeries();
        double[] errors = getErrors();
        double[] diffSeries = getDiffSeries();
        int p = diffSeries.length;
        int q = errors.length;
        int d = series.length;
        newValue += (d == 0)? coefficients.intercept() : coefficients.drift();
        newValue += arPoly.solve(diffSeries, p);
        newValue += maPoly.solve(errors, q);
        this.diffSeries.add(newValue);
        newValue += diffPoly.solve(series, d);
        this.series.add(newValue);
        this.errors.add(error);
        return newValue;
    }

    /**
     * Generate and return the next n values of this process.
     * 
     * @param n the number of values to generate.
     * @return the next n values of this process.
     */
    public double[] getNext(int n) {
        double[] next = new double[n];
        for (int i = 0; i < n; i++) {
            next[i] = getAsDouble();
        }
        return next;
    }

    /**
     *
     * Transform a snapshot of the process into a time series of the given size.
     *
     * @param size the size of the returned series.
     * @return a snapshot of the process as a time series of the given size.
     */
    public TimeSeries toSeries(int size) {
        return TimeSeries.from(this.period, getNext(size));
    }

    double[] getErrors() {
        return DoubleFunctions.arrayFrom(this.errors);
    }

    double[] getSeries() {
        return DoubleFunctions.arrayFrom(this.series);
    }

    double[] getDiffSeries() {
        return DoubleFunctions.arrayFrom(this.diffSeries);
    }

    /**
     * Start this process from the beginning.
     *
     * @return a new process with the same structure as this one.
     */
    public ArimaProcess startOver() {
        return builder()
                .setCoefficients(this.coefficients)
                .setDistribution(this.distribution)
                .setPeriod(this.period)
                .setSeasonalCycle(this.seasonalCycle)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * An ARIMA process builder.
     */
    public static class Builder {

        private ArimaCoefficients coefficients = ArimaCoefficients.builder().build();
        private Distribution distribution = new Normal();
        private TimePeriod period = (coefficients.isSeasonal()) ? TimePeriod.oneMonth() : TimePeriod.oneYear();
        private TimePeriod seasonalCycle = TimePeriod.oneYear();
        private boolean periodSet = false;

        /**
         * Set the process model coefficients.
         *
         * @param coefficients the model coefficients for the process.
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
         * Construct and return a new ArimaProcess.
         *
         * @return a new ArimaProcess.
         */
        public ArimaProcess build() {
            return new ArimaProcess(this);
        }
    }
}
