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

package linear.regression;

import com.google.common.collect.ImmutableList;

import java.util.List;

public final class MultipleLinearRegression implements LinearRegression {

    private final List<List<Double>> predictors;
    private final List<Double> response;
    private final boolean hasIntercept;

    private MultipleLinearRegression(Builder builder) {
        this.predictors = builder.predictors;
        this.response = builder.response;
        this.hasIntercept = builder.hasIntercept;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public List<List<Double>> predictors() {
        return ImmutableList.copyOf(this.predictors);
    }

    @Override
    public List<Double> response() {
        return ImmutableList.copyOf(this.response);
    }

    @Override
    public boolean hasIntercept() {
        return this.hasIntercept;
    }

    public static final class Builder {
        private List<List<Double>> predictors;
        private List<Double> response;
        private boolean hasIntercept = true;

        Builder predictors(List<List<Double>> predictors) {
            this.predictors = ImmutableList.copyOf(predictors);
            return this;
        }

        Builder predictor(List<Double> predictor) {
            this.predictors = ImmutableList.of(predictor);
            return this;
        }

        Builder response(List<Double> response) {
            this.response = ImmutableList.copyOf(response);
            return this;
        }

        Builder hasIntercept(boolean hasIntercept) {
            this.hasIntercept = hasIntercept;
            return this;
        }

        LinearRegression build() {
            return new MultipleLinearRegression(this);
        }
    }
}
