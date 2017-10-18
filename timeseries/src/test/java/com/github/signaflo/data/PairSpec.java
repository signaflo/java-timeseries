package com.github.signaflo.data;

import org.junit.Test;

import static com.github.signaflo.data.Pair.intPair;
import static com.github.signaflo.data.Pair.newPair;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class PairSpec {

    @Test
    public void testEqualsAndHashCode() {
        Pair<String, Integer> x = newPair("first", 1);
        Pair<String, Integer> y = newPair("first", 2);
        Pair<String, Integer> z = newPair("first", 1);
        Pair<String, Integer> w = newPair("second", 2);
        String s = "abc";
        assertThat(x, is(x));
        assertThat(x, is(z));
        assertThat(x.hashCode(), is(z.hashCode()));
        assertThat(x, is(not(y)));
        assertThat(x, is(not(w)));
        assertThat(x, is(notNullValue()));
        assertThat(x, is(not(s)));
        Pair<Integer, Integer> a = intPair(1, 2);
        Pair<Integer, Integer> b = intPair(2, 1);
        assertThat(a, is(not(b)));
        assertThat(b, is(not(a)));
    }

    @Test
    public void testComparable() {
        Pair<Integer, Integer> a = intPair(1, 2);
        Pair<Integer, Integer> b = intPair(1, 2);
        assertThat(a.compareTo(b), is(0));
        b = intPair(1, 3);
        assertThat(a.compareTo(b), is(lessThan(0)));
        b = intPair(0, 2);
        assertThat(a.compareTo(b), is(greaterThan(0)));
    }
}
