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

import lombok.NonNull;

import java.util.*;

/**
 * A first-in, first-out storage structure such that the oldest element in the store is automatically removed whenever
 * a new element is added and the store is at maximum capacity.
 *
 * @param <T> The type of value to store.
 */
public class EvictingStore<T> {

    private int numElements = 0;
    private final int maxCapacity;
    private final LinkedList<T> elements;
    private final Object lock = new Object();

    private EvictingStore(final int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.elements = new LinkedList<>();
    }

    public static <T> EvictingStore<T> create(int maxCapacity) {
        return new EvictingStore<>(maxCapacity);
    }

    /**
     * Add the specified value to the store.
     *
     * @param value the value to add to the store.
     * @throws NullPointerException if a null argument is given.
     */
    void add(@NonNull T value) {
        synchronized (lock) {
            if (numElements == maxCapacity) {
                this.elements.remove(0);
                this.elements.add(value);
            } else {
                this.elements.add(value);
                numElements++;
            }
        }
    }

    /**
     * Get an Optional containing the last value added to the store if such a value exists and an empty Optional
     * otherwise.
     *
     * @return an Optional containing the last value added to the store if such a value exists and an empty Optional
     * otherwise.
     */
    Optional<T> peekLast() {
        if (elements.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(elements.peekLast());
    }

    // Don't forget to copy list if this method is ever made public.
    List<T> elements() {
        return elements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvictingStore<?> that = (EvictingStore<?>) o;
        return numElements == that.numElements && maxCapacity == that.maxCapacity &&
               Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numElements, maxCapacity, elements);
    }

    @Override
    public String toString() {
        return Arrays.toString(elements.toArray());
    }
}
