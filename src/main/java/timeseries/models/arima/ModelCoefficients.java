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

import data.DoubleFunctions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import static data.DoubleFunctions.append;
import static stats.Statistics.sumOf;

/**
 * Consists of the autoregressive and moving-average coefficients for a seasonal ARIMA model, along with the
 * degrees of differencing and the model mean.
 *
 * @author Jacob Rachiele
 */
public class ModelCoefficients {

    private static final double EPSILON = Math.ulp(1.0);

    private final double[] arCoeffs;
    private final double[] maCoeffs;
    private final double[] sarCoeffs;
    private final double[] smaCoeffs;
    private final int d;
    private final int D;
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
    ModelCoefficients(final double[] arCoeffs, final double[] maCoeffs, final double[] sarCoeffs,
                      final double[] smaCoeffs, final int d, final int D, final double mean, final double drift) {
        this.arCoeffs = arCoeffs.clone();
        this.maCoeffs = maCoeffs.clone();
        this.sarCoeffs = sarCoeffs.clone();
        this.smaCoeffs = smaCoeffs.clone();
        this.d = d;
        this.D = D;
        this.mean = mean;
        this.intercept = this.mean * (1 - sumOf(arCoeffs) - sumOf(sarCoeffs));
        this.drift = drift;
    }

    private ModelCoefficients(Builder builder) {
        this.arCoeffs = builder.arCoeffs.clone();
        this.maCoeffs = builder.maCoeffs.clone();
        this.sarCoeffs = builder.sarCoeffs.clone();
        this.smaCoeffs = builder.smaCoeffs.clone();
        this.d = builder.d;
        this.D = builder.D;
        this.mean = builder.mean;
        this.intercept = this.mean * (1 - sumOf(arCoeffs) - sumOf(sarCoeffs));
        this.drift = builder.drift;
    }

    /**
     * Create a new builder for a ModelCoefficients object.
     *
     * @return a new builder for a ModelCoefficients object.
     */
    public static Builder newBuilder() {
        return new Builder();
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
            return DoubleFunctions.combine(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs);
        }
        return append(append(DoubleFunctions.combine(arCoeffs, maCoeffs, sarCoeffs, smaCoeffs), mean), drift);
    }

    final boolean isSeasonal() {
        return this.D > 0 || this.sarCoeffs.length > 0 || this.smaCoeffs.length > 0;
    }

    /**
     * Computes and returns the order of the ARIMA model corresponding to the model coefficients.
     *
     * @return the order of the ARIMA model corresponding to the model coefficients.
     */
    ModelOrder extractModelOrder() {
        ArimaModel.Constant constant = (Math.abs(this.mean) > EPSILON)
                                  ? ArimaModel.Constant.INCLUDE
                                  : ArimaModel.Constant.EXCLUDE;
        ArimaModel.Drift drift = (Math.abs(this.drift) > EPSILON)
                            ? ArimaModel.Drift.INCLUDE
                            : ArimaModel.Drift.EXCLUDE;
        return new ModelOrder(arCoeffs.length, d, maCoeffs.length, sarCoeffs.length, D, smaCoeffs.length,
                              constant, drift);
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
        if (d > 0) sb.append(newLine).append(d).append(" non-seasonal difference").append((d > 1)? "s" : "");
        if (D > 0) sb.append(newLine).append(D).append(" seasonal difference").append((D > 1)? "s" : "");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelCoefficients that = (ModelCoefficients) o;

        if (d != that.d) return false;
        if (D != that.D) return false;
        if (Double.compare(that.mean, mean) != 0) return false;
        if (Double.compare(that.intercept, intercept) != 0) return false;
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

        public Builder setMean(double mean) {
            this.mean = mean;
            return this;
        }

/*        public Builder setDrift(double drift) {
            this.drift = drift;
            return this;
        }*/

        //double[] stdErrors();

        public ModelCoefficients build() {
            return new ModelCoefficients(this);
        }
    }
}
