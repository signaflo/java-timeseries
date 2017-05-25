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

import lombok.ToString;
import smile.stat.distribution.TDistribution;

/**
 * A Student's t probability distribution.
 */
@ToString
public final class StudentsT implements Distribution {

    private final TDistribution dist;
    private final int df;

    /**
     * Create a new Student's t distribution with the given degrees of freedom.
     *
     * @param df the degrees of freedom for this distribution.
     */
    public StudentsT(final int df) {
        this.dist = new TDistribution(df);
        this.df = df;
    }

    @Override
    public double rand() {
        return this.dist.rand();
    }

    @Override
    public double quantile(final double prob) {
        return this.dist.quantile(prob);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentsT studentsT = (StudentsT) o;

        return df == studentsT.df;
    }

    @Override
    public int hashCode() {
        return df;
    }
}
