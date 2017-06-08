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
public class ModelOrder {

    final int p;
    final int d;
    final int q;
    final int P;
    final int D;
    final int Q;
    final int constant;

    ModelOrder(final int p, final int d, final int q, final int P, final int D, final int Q,
               final Arima.Constant constant) {
        this.p = p;
        this.d = d;
        this.q = q;
        this.P = P;
        this.D = D;
        this.Q = Q;
        this.constant = (constant == Arima.Constant.INCLUDE) ? 1 : 0;
    }

    // This returns the total number of nonseasonal and seasonal ARMA parameters.
    int sumARMA() {
        return this.p + this.q + this.P + this.Q;
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
        builder.append(") with").append((constant == 1) ? " a constant" : " no constant");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + D;
        result = prime * result + P;
        result = prime * result + Q;
        result = prime * result + constant;
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
        ModelOrder other = (ModelOrder) obj;
        if (D != other.D) return false;
        if (P != other.P) return false;
        if (Q != other.Q) return false;
        if (constant != other.constant) return false;
        if (d != other.d) return false;
        return p == other.p && q == other.q;
    }
}
