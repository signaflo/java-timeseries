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

/**
 * The order of an ARIMA model, consisting of the number of autoregressive and moving average parameters, along with
 * the degree of differencing and a flag indicating whether or not the model includes a constant.
 * This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public class ArimaOrder {

    final int p;
    final int d;
    final int q;
    final int P;
    final int D;
    final int Q;
    final ArimaModel.Constant constant;
    final ArimaModel.Drift drift;
    private final int sumARMA;
    private final int npar;
    private final int numRegressors;

    ArimaOrder(final int p, final int d, final int q, final int P, final int D, final int Q,
               final ArimaModel.Constant constant, final ArimaModel.Drift drift) {
        this.p = p;
        this.d = d;
        this.q = q;
        this.P = P;
        this.D = D;
        this.Q = Q;
        this.constant = constant;
        this.drift = drift;
        this.sumARMA = this.p + this.q + this.P + this.Q;
        this.numRegressors = constant.asInt() + drift.asInt();
        this.npar = sumARMA + numRegressors;
    }

    /**
     * Create and return a new non-seasonal model order with the given number of coefficients. A constant will be fit
     * only if d is equal to 0.
     *
     * @param p the number of non-seasonal autoregressive coefficients.
     * @param d the degree of non-seasonal differencing.
     * @param q the number of non-seasonal moving-average coefficients.
     * @return a new ARIMA model order.
     */
    public static ArimaOrder order(final int p, final int d, final int q) {
        ArimaModel.Constant constant = (d == 0) ? ArimaModel.Constant.INCLUDE : ArimaModel.Constant.EXCLUDE;
        return new ArimaOrder(p, d, q, 0, 0, 0, constant, ArimaModel.Drift.EXCLUDE);
    }

    /**
     * Create and return a new non-seasonal model order with the given number of coefficients and indication of
     * whether or not to fit a constant.
     *
     * @param p        the number of non-seasonal autoregressive coefficients.
     * @param d        the degree of non-seasonal differencing.
     * @param q        the number of non-seasonal moving-average coefficients.
     * @param constant whether or not to fit a constant to the model.
     * @param drift    whether or not to include a drift term in the model.
     * @return a new ARIMA model order.
     *
     * @throws IllegalArgumentException if the degree of differencing is greater than zero
     *                                  and both a drift term and constant are set to be included.
     */
    public static ArimaOrder order(final int p, final int d, final int q, final ArimaModel.Constant constant,
                                   final ArimaModel.Drift drift) {
        if (d > 0 && constant.include() && drift.include()) {
            throw new IllegalArgumentException("Arima models cannot be fit with both a constant and a" +
                                               " drift term when the degree of differencing is greater" +
                                               " than zero.");
        }
        return new ArimaOrder(p, d, q, 0, 0, 0, constant, drift);
    }

    /**
     * Create and return a new non-seasonal model order with the given number of coefficients and indication of
     * whether or not to fit a drift. A constant will be fit only if both the degree of differencing is zero
     * and there is no drift term to be included.
     *
     * @param p        the number of non-seasonal autoregressive coefficients.
     * @param d        the degree of non-seasonal differencing.
     * @param q        the number of non-seasonal moving-average coefficients.
     * @param drift    whether or not to fit a drift term to the model.
     * @return         a new ARIMA model order.
     */
    public static ArimaOrder order(final int p, final int d, final int q, final ArimaModel.Drift drift) {
        Arima.Constant constant = (d > 0 && drift.include())? Arima.Constant.EXCLUDE : Arima.Constant.INCLUDE;
        return new ArimaOrder(p, d, q, 0, 0, 0, constant, drift);
    }

    /**
     * Create and return a new non-seasonal model order with the given number of coefficients and indication of
     * whether or not to fit a constant.
     *
     * @param p        the number of non-seasonal autoregressive coefficients.
     * @param d        the degree of non-seasonal differencing.
     * @param q        the number of non-seasonal moving-average coefficients.
     * @param          constant whether or not to fit a constant to the model.
     * @return         a new ARIMA model order.
     */
    public static ArimaOrder order(final int p, final int d, final int q, final ArimaModel.Constant constant) {
        return new ArimaOrder(p, d, q, 0, 0, 0, constant, ArimaModel.Drift.EXCLUDE);
    }

    /**
     * Create a new ArimaOrder using the provided number of autoregressive and moving-average parameters, as well as the
     * degrees of differencing. A constant will be fit only if both d and D are equal to 0.
     *
     * @param p the number of non-seasonal autoregressive coefficients.
     * @param d the degree of non-seasonal differencing.
     * @param q the number of non-seasonal moving-average coefficients.
     * @param P the number of seasonal autoregressive coefficients.
     * @param D the degree of seasonal differencing.
     * @param Q the number of seasonal moving-average coefficients.
     * @return a new ARIMA model order.
     */
    public static ArimaOrder order(final int p, final int d, final int q, final int P, final int D, final int Q) {
        ArimaModel.Constant constant = (d == 0 && D == 0) ? ArimaModel.Constant.INCLUDE : ArimaModel.Constant.EXCLUDE;
        return new ArimaOrder(p, d, q, P, D, Q, constant, ArimaModel.Drift.EXCLUDE);
    }

    /**
     * Create a new ArimaOrder using the provided number of autoregressive and moving-average parameters, as well as the
     * degrees of differencing and indication of whether or not to fit a constant.
     *
     * @param p        the number of non-seasonal autoregressive coefficients.
     * @param d        the degree of non-seasonal differencing.
     * @param q        the number of non-seasonal moving-average coefficients.
     * @param P        the number of seasonal autoregressive coefficients.
     * @param D        the degree of seasonal differencing.
     * @param Q        the number of seasonal moving-average coefficients.
     * @param constant determines whether or not a constant is fitted with the model.
     * @return a new ARIMA model order.
     */
    public static ArimaOrder order(final int p, final int d, final int q, final int P, final int D, final int Q,
                                   final ArimaModel.Constant constant) {
        return new ArimaOrder(p, d, q, P, D, Q, constant, ArimaModel.Drift.EXCLUDE);
    }

    /**
     * Create a new ArimaOrder using the provided number of autoregressive and moving-average parameters, as well as the
     * degrees of differencing and indication of whether or not to fit a constant and or a drift term.
     *
     * @param p        the number of non-seasonal autoregressive coefficients.
     * @param d        the degree of non-seasonal differencing.
     * @param q        the number of non-seasonal moving-average coefficients.
     * @param P        the number of seasonal autoregressive coefficients.
     * @param D        the degree of seasonal differencing.
     * @param Q        the number of seasonal moving-average coefficients.
     * @param constant determines whether or not a constant is fitted with the model.
     * @param drift    whether or not to fit a drift term to the model.
     * @return a new ARIMA model order.
     *
     * @throws IllegalArgumentException if the degree of differencing (seasonal or non-seasonal) is greater than zero
     *                                  and both a drift term and constant are set to be included.
     */
    public static ArimaOrder order(final int p, final int d, final int q, final int P, final int D, final int Q,
                                   final ArimaModel.Constant constant, final Arima.Drift drift) {
        if ((d > 0 || D > 0) && constant.include() && drift.include()) {
            throw new IllegalArgumentException("Arima models cannot be fit with both a constant and a" +
                                               " drift term when the degree of differencing is greater" +
                                               " than zero.");
        }
        return new ArimaOrder(p, d, q, P, D, Q, constant, drift);
    }

    // This returns the total number of nonseasonal and seasonal ARMA parameters.
    int sumARMA() {
        return this.sumARMA;
    }

    int npar() {
        return this.npar;
    }

    int numRegressors() {
        return this.numRegressors;
    }

    @Override
    public String toString() {
        boolean isSeasonal = P > 0 || Q > 0 || D > 0;
        StringBuilder builder = new StringBuilder();
        if (isSeasonal) {
            builder.append("Seasonal ");
        }
        builder.append("ARIMA (").append(p).append(", ").append(d).append(", ").append(q);
        if (isSeasonal) {
            builder.append(") x (").append(P).append(", ").append(D).append(", ").append(Q);
        }
        builder.append(") with").append((constant == ArimaModel.Constant.INCLUDE) ? " a constant" : " no constant");
        builder.append((drift == ArimaModel.Drift.INCLUDE) ? " and drift" : "");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + D;
        result = prime * result + P;
        result = prime * result + Q;
        result = prime * result + constant.asInt();
        result = prime * result + drift.asInt();
        result = prime * result + d;
        result = prime * result + p;
        result = prime * result + q;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ArimaOrder other = (ArimaOrder) obj;
        if (D != other.D) return false;
        if (P != other.P) return false;
        if (Q != other.Q) return false;
        if (constant != other.constant) return false;
        if (drift != other.drift) return false;
        if (d != other.d) return false;
        return p == other.p && q == other.q;
    }
}
