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

import math.FieldElement;
import math.Interval;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An infinite set, which consists of a finite set of intervals of field elements.
 *
 * @param <E> The type of field element contained in this set.
 */
public class InfiniteSet<T extends FieldElement<T>, E extends Interval<T>> extends HashSet<E> {

    private final Set<E> intervals;

    InfiniteSet(Set<E> intervals) {
        super(intervals);
        this.intervals = new HashSet<>(intervals);
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof FieldElement) {
            return isInAnyInterval((FieldElement) o);
        } else if (o instanceof Interval) {
            return isInAnyInterval((Interval) o);
        }
        return false;
    }

    private boolean isInAnyInterval(FieldElement x) {
        try {
            for (E interval : intervals) {
                if (interval.containsFieldElement(x)) {
                    return true;
                }
            }
            // Thrown especially when the field element is complex, but the intervals are not.
        } catch (UnsupportedOperationException e) {
            return false;
        }
        return false;
    }

    private boolean isInAnyInterval(Interval x) {
        try {
            for (E interval : intervals) {
                if (x.isSubset(interval)) {
                    return true;
                }
            }
            // Thrown especially when the given interval, x, is complex, but the parent is not.
        } catch (UnsupportedOperationException e) {
            return false;
        }
        return false;
    }

    /**
     * @return nothing.
     * @throws UnsupportedOperationException since this is an uncountable set.
     */
    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException("Infinite sets do not support iteration.");
    }

    @Override
    public int size() {
        return Integer.MAX_VALUE;
    }
}
