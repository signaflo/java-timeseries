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


public class SortedMapping<K, V> extends LinkedHashMap<K, V> implements SortedMap<K, V> {

    private static final int DEFAULT_MAX_SIZE = 1000;

    private final SortedMap<K, V> sortedMap;
    private final int maxSize;

    public SortedMapping() {
        this(DEFAULT_MAX_SIZE);
    }

    public SortedMapping(int maxSize) {
        super(maxSize);
        this.sortedMap = new TreeMap<>();
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> entry) {
        return this.size() > maxSize;
    }

    @Override
    public Comparator<? super K> comparator() {
        return this.sortedMap.comparator();
    }

    @Override
    public SortedMap<K, V> subMap(@NonNull K fromKey, @NonNull K toKey) {
        return this.sortedMap.subMap(fromKey, toKey);
    }

    @Override
    public SortedMap<K, V> headMap(@NonNull K toKey) {
        return this.sortedMap.headMap(toKey);
    }

    @Override
    public SortedMap<K, V> tailMap(@NonNull K fromKey) {
        return this.sortedMap.tailMap(fromKey);
    }

    @Override
    public K firstKey() {
        return this.sortedMap.firstKey();
    }

    @Override
    public K lastKey() {
        return this.sortedMap.lastKey();
    }

    public Optional<V> lastValue() {
        if (this.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(this.sortedMap.get(this.sortedMap.lastKey()));
    }

    @Override
    public V put(@NonNull K key, @NonNull V value) {
        super.put(key, value);
        this.sortedMap.put(key, value);
        return value;
    }

    @Override
    public boolean isEmpty() {
        return this.sortedMap.isEmpty();
    }

    @Override
    public int size() {
        return this.sortedMap.size();
    }

    @Override
    public String toString() {
        return this.sortedMap.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SortedMapping<?, ?> that = (SortedMapping<?, ?>) o;
        return maxSize == that.maxSize && Objects.equals(sortedMap, that.sortedMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sortedMap, maxSize);
    }
}
