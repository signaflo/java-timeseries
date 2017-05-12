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

package math.probability;

import lombok.EqualsAndHashCode;
import math.Real;

/**
 * Represents the outcome of an experiment.
 */
@EqualsAndHashCode
public class Outcome<K> {

    private final K samplePoint;
    private final Real probability;

    Outcome(K samplePoint, Real probability) {
        validateProbability(probability.asDouble());
        this.samplePoint = samplePoint;
        this.probability = probability;
    }

    Real probability() {
        return this.probability;
    }

    K samplePoint() {
        return this.samplePoint;
    }

    private void validateProbability(double value) {
        if (value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("The probability must lie between 0 and 1, but was " +
                                               Double.toString(value));
        }
    }

    @Override
    public String toString() {
        return samplePoint.toString() + " with probability " +
               Double.toString(this.probability.asDouble());
    }
}
