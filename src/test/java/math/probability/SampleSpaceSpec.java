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

public class SampleSpaceSpec {

    @Test
    public void createInfiniteSampleSpace() {
        RealInterval interval = new RealInterval(0, 1);
        Outcome<RealInterval> outcome = new Outcome<>(interval, Real.from(0.5));
        Outcome<RealInterval> outcome2 = new Outcome<>(new RealInterval(2, 3), Real.from(0.5));
        Set<Outcome<RealInterval>> outcomes = new HashSet<>(2);
        outcomes.add(outcome); outcomes.add(outcome2);
        SampleSpace<RealInterval> sampleSpace = new SampleSpace<>(outcomes);
        Event<RealInterval> event = new Event<>(outcomes);
        System.out.println(event.probability());
        Experiment<RealInterval> realIntervalExperiment = new Experiment<>(sampleSpace);
        Set<Outcome<RealInterval>> outcomeSet = Collections.singleton(outcome);
        Event<RealInterval> realEvent = realIntervalExperiment.defineEvent(outcomeSet);
        RealInterval subset = new RealInterval(0.1, 0.9);
        Set<RealInterval> subsetInterval = new HashSet<>();
        subsetInterval.add(subset);
        Set<RealInterval> intervals = new InfiniteSet<>(subsetInterval);
        System.out.println(intervals.contains(Real.from(0.25)));
        Set<RealInterval> subIntervals = new HashSet<>(2);
        subIntervals.add(new RealInterval(0.3, 0.7));
        subIntervals.add(new RealInterval(0.2, 0.5));
        System.out.println(intervals.containsAll(subIntervals));
        Outcome<RealInterval> outcome3 = new Outcome<>(new RealInterval(0.3, 0.7), Real.from(0.2));
        Outcome<RealInterval> outcome4 = new Outcome<>(new RealInterval(0.2, 0.3), Real.from(0.1));
        outcomes = new HashSet<>(2);
        outcomes.add(outcome3); outcomes.add(outcome4);
        System.out.println(realIntervalExperiment.defineEvent(outcomes));

    }
}
