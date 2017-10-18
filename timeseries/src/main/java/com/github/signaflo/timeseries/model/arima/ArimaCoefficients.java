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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import static com.github.signaflo.math.operations.DoubleFunctions.append;
import static com.github.signaflo.math.operations.DoubleFunctions.combine;
import static com.github.signaflo.math.stats.Statistics.sumOf;

/**
 * Consists of the autoregressive and moving-average coefficients for a seasonal ARIMA model, along with the
 * degrees of differencing and the model mean and/or drift term.
 *
 * @author Jacob Rachiele
 */
public class ArimaCoefficients {

    private static final double EPSILON = Math.ulp(1.0);

    private final double[] arCoeffs;
    private final double[] maCoeffs;
    private final double[] sarCoeffs;
    private final double[] smaCoeffs;
    private final int d;
    private final int D;
    private final int seasonalFrequency;
    private final double mean;
    // The intercept is equal to mean * (1 - (sum of AR coefficients))
    private final double intercept;
    private final double drift;

    /**
     * Create a structure holding the coefficients, the degrees of differencing, and the mean of a seasonal ARIMA
     * model.
     *
     * @param arCoeffs  the non-seasonal autoregressive coefficients.
     * @param maCoeffs  the non-seasonal moving-average coefficients.
     * @param sarCoeffs the seasonal autoregressive coefficients.
     * @param smaCoeffs the seasonal moving-average coefficients.
     * @param d         the non-seasonal degree of differencing.
     * @param D         the seasonal degree of differencing.
     * @param mean      the process mean.
     */
    ArimaCoefficients(double[] arCoeffs, double[] maCoeffs, double[] sarCoeffs,
                      double[] smaCoeffs, int d, int D, double mean,
                      double drift, int seasonalFrequency) {
        this.arCoeffs = arCoeffs.clone();
        this.maCoeffs = maCoeffs.clone();
        this.sarCoeffs = sarCoeffs.clone();
        this.smaCoeffs = smaCoeffs.clone();
        this.d = d;
        this.D = D;
        this.seasonalFrequency = seasonalFrequency;
        this.mean = mean;
        this.intercept = meanToIntercept(expandArCoefficients(arCoeffs, sarCoeffs, seasonalFrequency), mean);
        this.drift = drift;
    }

    private ArimaCoefficients(Builder builder) {
        this.arCoeffs = builder.arCoeffs.clone();
        this.maCoeffs = builder.maCoeffs.clone();
        this.sarCoeffs = builder.sarCoeffs.clone();
        this.smaCoeffs = builder.smaCoeffs.clone();
        this.d = builder.d;
        this.D = builder.D;
        this.seasonalFrequency = builder.seasonalFrequency;
        this.mean = builder.mean;
        this.intercept = meanToIntercept(expandArCoefficients(arCoeffs, sarCoeffs, builder.seasonalFrequency), mean);
        this.drift = builder.drift;
    }

    // Expand the autoregressive coefficients by combining the non-seasonal and seasonal coefficients into a single
    // array, which takes advantage of the fact that a seasonal AR model is a special case of a non-seasonal
    // AR model, with zero coefficients at the non-seasonal indices.
    static double[] expandArCoefficients(final double[] arCoeffs, final double[] sarCoeffs,
                                         final int seasonalFrequency) {
        double[] arSarCoeffs = new double[arCoeffs.length + sarCoeffs.length * seasonalFrequency];

        System.arraycopy(arCoeffs, 0, arSarCoeffs, 0, arCoeffs.length);

        // Note that we take into account the interaction between the seasonal and non-seasonal coefficients,
        // which arises because the model's ar and seasonal ar polynomials are multiplied together.
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
    static double[] expandMaCoefficients(final double[] maCoeffs, final double[] smaCoeffs,
                                         final int seasonalFrequency) {
        double[] maSmaCoeffs = new double[maCoeffs.length + smaCoeffs.length * seasonalFrequency];

        System.arraycopy(maCoeffs, 0, maSmaCoeffs, 0, maCoeffs.length);

        // Note that we take into account the interaction between the seasonal and non-seasonal coefficients,
        // which arises because the model's ma and seasonal ma polynomials are multiplied together.
        // In contrast to the ar polynomial, the ma and seasonal ma product maintains a positive sign.
        for (int i = 0; i < smaCoeffs.length; i++) {
            maSmaCoeffs[(i + 1) * seasonalFrequency - 1] = smaCoeffs[i];
            for (int j = 0; j < maCoeffs.length; j++) {
                maSmaCoeffs[(i + 1) * seasonalFrequency + j] = smaCoeffs[i] * maCoeffs[j];
            }
        }
        return maSmaCoeffs;
    }

    private static double meanToIntercept(double[] autoRegressiveCoefficients, double mean) {
        return mean * (1 - sumOf(autoRegressiveCoefficients));
    }

    static double interceptToMean(double[] autoRegressiveCoefficients, double intercept) {
        return intercept / (1 - sumOf(autoRegressiveCoefficients));
    }

    double[] getAllMovingAverageCoefficients() {
        return expandMaCoefficients(maCoeffs(), seasonalMACoeffs(), seasonalFrequency);
    }

    double[] getAllAutoRegressiveCoefficients() {
        return expandArCoefficients(arCoeffs(), seasonalARCoeffs(), seasonalFrequency);
    }

    /**
     * Get the autoregressive coefficients.
     *
     * @return the autoregressive coefficients.
     */
    final double[] arCoeffs() {
        return arCoeffs.clone();
    }


    /**
     * Get the moving-average coefficients.
     *
     * @return the moving-average coefficients.
     */
    final double[] maCoeffs() {
        return maCoeffs.clone();
    }

    /**
     * Get the seasonal autoregressive coefficients.
     *
     * @return the seasonal autoregressive coefficients.
     */
    final double[] seasonalARCoeffs() {
        return sarCoeffs.clone();
    }

    /**
     * Get the seasonal moving-average coefficients.
     *
     * @return the seasonal moving-average coefficients.
     */
    final double[] seasonalMACoeffs() {
        return smaCoeffs.clone();
    }

    /**
     * Get the degree of non-seasonal differencing.
     *
     * @return the degree of non-seasonal differencing.
     */
    final int d() {
        return d;
    }

    /**
     * Get the degree of seasonal differencing.
     *
     * @return the degree of seasonal differencing.
     */
    final int D() {
        return D;
    }

    final int seasonalFrequency() {
        return this.seasonalFrequency;
    }

    /**
     * Get the model mean.
     *
     * @return the model mean.
     */
    final double mean() {
        return mean;
    }

    final double drift() {
        return drift;
    }

    /**
     * Get the model intercept term. Note that this is <i>not</i> the model mean, as in R, but the actual
     * intercept. The intercept is equal to &mu; &times; (1 - sum(AR)), where &mu; is the model mean and AR
     * is a vector containing the non-seasonal and seasonal autoregressive coefficients.
     *
     * @return the model intercept term.
     */
    final double intercept() {
        return this.intercept;
    }

    final double[] getAllCoeffs() {
        if (Math.abs(mean) < EPSILON && Math.abs(drift) < EPSILON) {
            return combine(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs);
        }
        return append(append(combine(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs), mean), drift);
    }

    final boolean isSeasonal() {
        return this.D > 0 || this.sarCoeffs.length > 0 || this.smaCoeffs.length > 0;
    }

    /**
     * Computes and returns the order of the ARIMA model corresponding to the model coefficients.
     *
     * @return the order of the ARIMA model corresponding to the model coefficients.
     */
    ArimaOrder extractModelOrder() {
        ArimaModel.Constant constant = (Math.abs(this.mean) > EPSILON)
                                  ? ArimaModel.Constant.INCLUDE
                                  : ArimaModel.Constant.EXCLUDE;
        ArimaModel.Drift drift = (Math.abs(this.drift) > EPSILON)
                            ? ArimaModel.Drift.INCLUDE
                            : ArimaModel.Drift.EXCLUDE;
        return new ArimaOrder(arCoeffs.length, d, maCoeffs.length, sarCoeffs.length, D, smaCoeffs.length,
                              constant, drift);
    }

    double[] getRegressors(final ArimaOrder order) {
        double[] regressors = new double[order.npar() - order.sumARMA()];
        if (order.constant().include()) {
            regressors[0] = this.mean;
        }
        if (order.drift().include()) {
            regressors[order.constant().asInt()] = this.drift;
        }
        return regressors;
    }

    /**
     * Create a new builder for a ArimaCoefficients object.
     *
     * @return a new builder for a ArimaCoefficients object.
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        NumberFormat numFormatter = new DecimalFormat("#0.0000");
        StringBuilder sb = new StringBuilder();
        if (arCoeffs.length > 0) {
            sb.append(newLine).append("autoregressive:");
            for (double d : arCoeffs) {
                sb.append(" ").append(numFormatter.format(d));
            }
        }
        if (maCoeffs.length > 0) {
            sb.append(newLine).append("moving-average:");
            for (double d : maCoeffs) {
                sb.append(" ").append(numFormatter.format(d));
            }
        }
        if (sarCoeffs.length > 0) {
            sb.append(newLine).append("seasonal autoregressive:");
            for (double d : sarCoeffs) {
                sb.append(" ").append(numFormatter.format(d));
            }
        }
        if (smaCoeffs.length > 0) {
            sb.append(newLine).append("seasonal moving-average:");
            for (double d : smaCoeffs) {
                sb.append(" ").append(numFormatter.format(d));
            }
        }
        if (Math.abs(mean) > EPSILON) {
            sb.append(newLine).append("mean: ").append(numFormatter.format(mean));
            sb.append(newLine).append("intercept: ").append(numFormatter.format(intercept));
        }
        else {
            sb.append(newLine).append("zero mean");
        }
        if (Math.abs(drift) > EPSILON) {
            sb.append(newLine).append("drift: ").append(numFormatter.format(drift));
        }
        if (d > 0) sb.append(newLine).append(d).append(" non-seasonal difference").append((d > 1)? "s" : "");
        if (D > 0) sb.append(newLine).append(D).append(" seasonal difference").append((D > 1)? "s" : "");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArimaCoefficients that = (ArimaCoefficients) o;

        if (d != that.d) return false;
        if (D != that.D) return false;
        if (Double.compare(that.mean, mean) != 0) return false;
        if (Double.compare(that.intercept, intercept) != 0) return false;
        if (Double.compare(that.drift, drift) != 0) return false;
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
        temp = Double.doubleToLongBits(drift);
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
        private int seasonalFrequency = 1;
        private double mean = 0.0;
        private double drift = 0.0;

        private Builder() {
        }

        public Builder setARCoeffs(double... arCoeffs) {
            this.arCoeffs = arCoeffs.clone();
            return this;
        }

        public Builder setSeasonalARCoeffs(double... sarCoeffs) {
            this.sarCoeffs = sarCoeffs.clone();
            return this;
        }

        public Builder setMACoeffs(double... maCoeffs) {
            this.maCoeffs = maCoeffs.clone();
            return this;
        }

        public Builder setSeasonalMACoeffs(double... smaCoeffs) {
            this.smaCoeffs = smaCoeffs.clone();
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

        public Builder setSeasonalFrequency(int seasonalFrequency) {
            this.seasonalFrequency = seasonalFrequency;
            return this;
        }

        public Builder setMean(double mean) {
            this.mean = mean;
            return this;
        }

        public Builder setDrift(double drift) {
            this.drift = drift;
            return this;
        }

/*        public MultipleLinearRegressionBuilder setDrift(double drift) {
            this.drift = drift;
            return this;
        }*/

        //double[] stdErrors();

        public ArimaCoefficients build() {
            verifyState(this);
            return new ArimaCoefficients(this);
        }

        private void verifyState(Builder builder) {
            if ((builder.d + builder.D > 0) && builder.mean != 0.0) {
                String message = "An ARIMA model cannot have both differencing and a mean, though it " +
                                 "might possibly include a drift term.";
                throw new IllegalStateException(message);
            }
            if ((builder.d + builder.D > 1) && builder.drift != 0.0) {
                String message = "An ARIMA model with more than one degree of differencing may not include " +
                                 "a drift term.";
                throw new IllegalStateException(message);
            }
        }
    }
}
