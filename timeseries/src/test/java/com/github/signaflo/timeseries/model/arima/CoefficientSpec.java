package com.github.signaflo.timeseries.model.arima;

import org.junit.Test;

import java.util.Objects;
import java.util.OptionalDouble;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class CoefficientSpec {

    private final Coefficient coefficient = getCoefficientBuilder().build();

    @Test
    public void whenCoefficientGettersThenCorrectValuesReturned() {
        assertThat(coefficient.value(), is(0.3));
        assertThat(coefficient.standardError(), is(OptionalDouble.of(0.1)));
    }

    @Test
    public void whenNoStdErrorGivenThenEmptyOptionalDouble() {
        Coefficient coefficient = Coefficient.builder(0.5).build();
        assertThat(coefficient.standardError(), is(OptionalDouble.empty()));
    }

    @Test
    public void testEqualsAndHashCode() {
        Coefficient coefficientMirror = getCoefficientBuilder().build();
        Coefficient coefficient2 = getCoefficientBuilder().setStandardError(0.4).build();
        Coefficient coefficient3 = Coefficient.builder(0.5).setStandardError(0.1).build();
        assertThat(coefficient, is(coefficient));
        assertThat(coefficient, is(coefficientMirror));
        assertThat(coefficient.hashCode(), is(coefficientMirror.hashCode()));
        assertThat(coefficient, is(not(coefficient2)));
        assertThat(coefficient, is(not(coefficient3)));
        assertThat(Objects.equals(coefficient, null), is(false));
        assertThat(coefficient.equals(new Object()), is(false));
    }

    @Test
    public void testToString() {
        String expected = "Coefficient (SE): 0.3 (0.1)";
        assertThat(coefficient.toString(), is(expected));
        System.out.println(Coefficient.builder(0.3).build().toString());
    }

    private Coefficient.Builder getCoefficientBuilder() {
        return Coefficient.builder(0.3).setStandardError(0.1);
    }
}
