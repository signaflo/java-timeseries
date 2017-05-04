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

import com.google.common.collect.Sets;
import math.Real;

import java.util.Set;

public class Event<E> {

    private final Set<Outcome<E>> elementaryOutcomes;

    public Event(Set<Outcome<E>> elementaryOutcomes) {
        this.elementaryOutcomes = elementaryOutcomes;
    }

    public Real probability() {
        return addElementaryProbabilities(this.elementaryOutcomes);
    }

    public String toString() {
        return this.elementaryOutcomes.toString() + "with total probability " + this.probability();
    }

    public Event<E> intersection(Event<E> otherEvent) {
        return new Event<>(Sets.intersection(this.elementaryOutcomes, otherEvent.elementaryOutcomes));
    }

    private Real addElementaryProbabilities(Set<Outcome<E>> elementaryOutcomes) {
        Real probability = Real.zero();
        for (Outcome<E> outcome : elementaryOutcomes) {
            probability = probability.plus(outcome.probability());
        }
        return probability;
    }
}
