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
package stats.distributions;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import smile.stat.distribution.GaussianDistribution;

/**
 * A Normal, or Gaussian, probability distribution.
 */
@EqualsAndHashCode @ToString
public final class Normal implements Distribution {

    private final smile.stat.distribution.Distribution dist;

    /**
     * Create a new Normal distribution with the given mean and standard deviation.
     *
     * @param mean the mean of the distribution.
     * @param stdev the standard deviation of the distribution.
     */
    public Normal(final double mean, final double stdev) {
        this.dist = new GaussianDistribution(mean, stdev);
    }

    /**
     * Create a new standard normal distribution with mean 0 and standard deviation 1.
     */
    public Normal() {
        this(0, 1);
    }

    @Override
    public final double rand() {
        return this.dist.rand();
    }

    @Override
    public final double quantile(final double prob) {
        return this.dist.quantile(prob);
    }

}
