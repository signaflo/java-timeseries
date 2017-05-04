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
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ExperimentSpec {

    @Test
    public void testExperiment() {
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
        System.out.println(evenResult.probability());
        Event<Integer> rollATwo = dieToss.defineEvent(Collections.singleton(
                new Outcome<>(2, Real.from(1.0/6.0))));
        Real intersectionProb = evenResult.intersection(rollATwo).probability();
        Real conditionalProbability = intersectionProb.dividedBy(evenResult.probability());
        System.out.println(conditionalProbability);
    }

    @Test
    public void testContinuousExperiment() {
        Set<Outcome<Real.Interval>> outcomes = new HashSet<>(1);
        outcomes.add(new Outcome<>(new Real.Interval(0, 8), Real.from(1.0)));
        SampleSpace<Real.Interval> sampleSpace = new SampleSpace<>(outcomes);
        Experiment<Real.Interval> componentLifetime = new Experiment<>(sampleSpace);
        Set<Outcome<Real.Interval>> lessThan7Hours = new HashSet<>(1);
        lessThan7Hours.add(new Outcome<>(new Real.Interval(0, 7), Real.from(7.0/8.0)));
    }
}
