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

import math.Interval;
import math.Real;
import math.RealInterval;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ExperimentSpec {

    @Test
    public void WhenDieTossedThenProbabilitiesCorrect() {
        Set<Outcome<Integer>> outcomes = new HashSet<>(6);
        for (int i = 0; i < 6; i++) {
            outcomes.add(new Outcome<>((i + 1), Real.from(1.0/6.0)));
        }
        SampleSpace<Integer> sampleSpace = new SampleSpace<>(outcomes);
        Experiment<Integer> dieToss = new Experiment<>(sampleSpace);
        Set<Outcome<Integer>> evenFace = new HashSet<>(3);
        for (Outcome<Integer> outcome : outcomes) {
            if (outcome.samplePoint() % 2 == 0) {
                evenFace.add(outcome);
            }
        }
        Event<Integer> evenResult = dieToss.defineEvent(evenFace);
        assertThat(evenResult.probability(), is(Real.from(0.5)));
        Event<Integer> rollATwo = dieToss.defineEvent(Collections.singleton(
                new Outcome<>(2, Real.from(1.0/6.0))));
        Real conditionalProbability = rollATwo.probabilityGiven(evenResult);
        assertThat(conditionalProbability.asDouble(), is(closeTo(1.0 / 3.0, 1E-15)));
    }

    @Test
    public void whenTwoDieTossedThenConditionalProbabilityCorrect() {
        Set<Outcome<TwoTuple<Integer>>> sumEqualsSeven = new HashSet<>(6);
        Set<Outcome<TwoTuple<Integer>>> outcomes = getTwoDieTossOutcomes(sumEqualsSeven);

        SampleSpace<TwoTuple<Integer>> sampleSpace = new SampleSpace<>(outcomes);
        Experiment<TwoTuple<Integer>> dieToss = new Experiment<>(sampleSpace);
        Event<TwoTuple<Integer>> sumIsSeven = dieToss.defineEvent(sumEqualsSeven);
        Event<TwoTuple<Integer>> firstTossFour = dieToss.defineEvent(getFirstTossFour(outcomes));
        assertThat(sumIsSeven.probabilityGiven(firstTossFour).asDouble(), is(closeTo(1.0 / 6.0, 1E-15)));

    }

    @Test
    public void testContinuousExperiment() {
        Set<Outcome<Interval>> outcomes = new HashSet<>(1);
        Interval lifetime = new RealInterval(0, 8);

    }

    private Set<Outcome<TwoTuple<Integer>>> getFirstTossFour(Set<Outcome<TwoTuple<Integer>>> outcomes) {
        Set<Outcome<TwoTuple<Integer>>> firstTossFour = new HashSet<>(6);
        for (Outcome<TwoTuple<Integer>> outcome : outcomes) {
            if (outcome.samplePoint().one.equals(4)) {
                firstTossFour.add(outcome);
            }
        }
        return firstTossFour;
    }

    private Set<Outcome<TwoTuple<Integer>>> getTwoDieTossOutcomes(Set<Outcome<TwoTuple<Integer>>> sumEqualsSeven) {
        Set<Outcome<TwoTuple<Integer>>> outcomes = new HashSet<>(36);
        for (int i = 1; i <= 6; i++) {
            for (int j = 1; j <= 6; j++) {
                Real prob = Real.from(1.0/36.0);
                TwoTuple<Integer> pair = new TwoTuple<>(i, j);
                Outcome<TwoTuple<Integer>> outcome = new Outcome<>(pair, prob);
                outcomes.add(outcome);
                if (i + j == 7) {
                    sumEqualsSeven.add(outcome);
                }
            }
        }
        return outcomes;
    }

    private class TwoTuple<T> {

        private final T one;
        private final T two;

        TwoTuple(T one, T two) {
            this.one = one; this.two = two;
        }
    }
}
