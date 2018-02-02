/*
 * Copyright (c) 2018 Jacob Rachiele
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

package com.github.signaflo.streaming;

import com.google.common.collect.EvictingQueue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedSizeStreamingSeries<T extends Number> {

    private AtomicInteger numElements = new AtomicInteger();
    private final int maxSize;
    final List<T> elements;
    private final Object lock = new Object();

    FixedSizeStreamingSeries(final int maxSize) {
        this.maxSize = maxSize;
        this.elements = Collections.synchronizedList(new LinkedList());
    }

    void add(T observation) {
        synchronized (lock) {
            if (numElements.get() == maxSize) {
                this.elements.remove(0);
                this.elements.add(observation);
            } else {
                this.elements.add(observation);
                numElements.incrementAndGet();
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.toString(elements.toArray());
    }
}
