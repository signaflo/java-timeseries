package com.github.signaflo.math;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class NASpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final NA<Double> missingDouble = NA.missingDouble();

    @Test
    public void whenNewMissingDoubleThenRepresentationEqualToNaN() {
        assertThat(missingDouble.doubleValue(), is(Double.NaN));
    }

    @Test
    public void whenIntValueCalledOnMissingDoubleThenUnsupportedOperationException() {
        exception.expect(UnsupportedOperationException.class);
        missingDouble.intValue();
    }

    @Test
    public void whenByteValueCalledOnMissingDoubleThenUnsupportedOperationException() {
        exception.expect(UnsupportedOperationException.class);
        missingDouble.byteValue();
    }

    @Test
    public void whenShortValueCalledOnMissingDoubleThenUnsupportedOperationException() {
        exception.expect(UnsupportedOperationException.class);
        missingDouble.shortValue();
    }

    @Test
    public void whenDoubleValueCalledOnMissingIntegerThenUnsupportedOperationException() {
        exception.expect(UnsupportedOperationException.class);
        new NA<>(Integer.MIN_VALUE).doubleValue();
    }

    @Test
    public void whenLongValueCalledOnMissingDoubleThenUnsupportedOperationException() {
        exception.expect(UnsupportedOperationException.class);
        missingDouble.longValue();
    }

    @Test
    public void whenFloatValueCalledOnMissingDoubleThenUnsupportedOperationException() {
        exception.expect(UnsupportedOperationException.class);
        missingDouble.floatValue();
    }

    @Test
    public void whenRepresentationNullThenNPE() {
        exception.expect(NullPointerException.class);
        new NA<>(null);
    }

    @Test
    public void testEqualsAndHashCode() {
        NA<Double> missingDoubleMirror = NA.missingDouble();
        NA<Double> missingDouble2 = new NA<>(Double.MAX_VALUE);
        NA<Integer> missingInteger = new NA<>(Integer.MIN_VALUE);
        assertThat(missingDoubleMirror, is(missingDoubleMirror));
        assertThat(missingDouble, is(missingDoubleMirror));
        assertThat(missingDouble.hashCode(), is(missingDoubleMirror.hashCode()));
        assertThat(missingDouble, is(not(missingInteger)));
        assertThat(missingDouble, is(not(missingDouble2)));
        assertThat(Objects.equals(missingDouble, null), is(false));
    }

    @Test
    public void testToString() {
        String expected = "NA";
        assertThat(missingDouble.toString(), is(expected));
    }
}
