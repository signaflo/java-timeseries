package timeseries.model.arima;

import com.google.common.collect.EvictingQueue;
import math.operations.DoubleFunctions;
import math.stats.distributions.Distribution;
import math.stats.distributions.Normal;
import timeseries.TimePeriod;

import java.util.Iterator;
import java.util.Queue;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Oct. 06, 2017
 */
public class ArimaProcess implements Iterator<Double> {

    private final ArimaCoefficients coefficients;
    private final Distribution distribution;
    private final TimePeriod period;
    private final TimePeriod seasonalCycle;
    private final int seasonalFrequency;

    private final double[] arSarCoeffs;
    private final double[] maSmaCoeffs;
    private final int diffOffset;
    private final int offset;
    private final Queue<Double> series;
    private final Queue<Double> errors;

    private ArimaProcess(Builder builder) {
        this.coefficients = builder.coefficients;
        this.distribution = builder.distribution;
        this.period = builder.period;
        this.seasonalCycle = builder.seasonalCycle;
        this.seasonalFrequency = (int) period.frequencyPer(seasonalCycle);
        this.arSarCoeffs = ArimaCoefficients.expandArCoefficients(coefficients.arCoeffs(), coefficients.seasonalARCoeffs(),
                                                                  seasonalFrequency);
        this.maSmaCoeffs = ArimaCoefficients.expandMaCoefficients(coefficients.maCoeffs(), coefficients.seasonalMACoeffs(),
                                                             seasonalFrequency);
        this.diffOffset = coefficients.d() + coefficients.D() * seasonalFrequency;
        this.offset = arSarCoeffs.length;
        this.errors = EvictingQueue.create(this.offset);
        this.series = EvictingQueue.create(this.offset);
        initialize();
    }

    private Queue<Double> initialize() {
        for (int i = 0; i < this.offset; i++) {
            Double error = distribution.rand();
            errors.add(error);
            series.add(error);
        }
        return errors;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Double next() {
        return 0.0;
    }

    double[] getErrors() {
        return DoubleFunctions.arrayFrom(this.errors);
    }

    double[] getSeries() {
        return DoubleFunctions.arrayFrom(this.series);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * An ARIMA simulation builder.
     */
    public static class Builder {

        private ArimaCoefficients coefficients = ArimaCoefficients.builder().build();
        private Distribution distribution = new Normal();
        private TimePeriod period = (coefficients.isSeasonal()) ? TimePeriod.oneMonth() : TimePeriod.oneYear();
        private TimePeriod seasonalCycle = TimePeriod.oneYear();
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
         * Construct and return a new fully built and immutable ArimaSimulation object.
         *
         * @return a new fully built and immutable ArimaSimulation object.
         */
        public ArimaProcess build() {
            return new ArimaProcess(this);
        }
    }
}
