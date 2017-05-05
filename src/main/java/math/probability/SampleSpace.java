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

import math.Real;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;

public class SampleSpace<E> {

    private final Set<Outcome<E>> samplePoints;

    public SampleSpace(Set<Outcome<E>> samplePoints) {
        this.samplePoints = samplePoints;

    }

    public Set<Outcome<E>> samplePoints() {
        return new HashSet<>(this.samplePoints);
    }

    Event<E> defineEvent(RandomVariable<E> randomVariable, BiPredicate<Real, Real> predicate, Real x) {
        Set<Outcome<E>> subset = new HashSet<>();
        Set<Outcome<E>> samplePoints  = this.samplePoints();
        for (Outcome<E> outcome : samplePoints) {
            Real output = randomVariable.apply(outcome);
            if (predicate.test(x, output)) {
                subset.add(outcome);
            }
        }
        return new Event<>(subset);
    }

    Event<E> defineEvent(RandomVariable<E> randomVariable, BiPredicate<Real, Real> first,
            BiPredicate<Real, Real> second, Real a, Real b) {
        Set<Outcome<E>> subset = new HashSet<>();
        Set<Outcome<E>> samplePoints  = this.samplePoints();
        for (Outcome<E> outcome : samplePoints) {
            Real output = randomVariable.apply(outcome);
            if (first.test(a, output) && second.test(b, output)) {
                subset.add(outcome);
            }
        }
        return new Event<>(subset);
    }
}
