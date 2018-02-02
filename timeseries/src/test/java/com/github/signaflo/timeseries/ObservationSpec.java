package com.github.signaflo.timeseries;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for the Observation class.
 *
 * @author Jacob Rachiele
 * Nov. 12, 2017
 */
public class ObservationSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private final OffsetDateTime dateTime = OffsetDateTime.of(getDefaultLocalDateTime(), getDefaultZoneOffset());
    private final Observation<Double> x = new Observation<>(3.0, dateTime);

    @Test
    public void whenNullDateTimeThenNPE() {
        exception.expectMessage("observationPeriod");
        exception.expect(NullPointerException.class);
        new Observation<>(3.0, null);
    }

    @Test
    public void whenToStringThenCorrectStringReturned() {
        String expected = "3.0 at " + dateTime.toString();
        assertThat(x.toString(), is(expected));
    }

    @Test
    public void whenDifferentIdentityButSameDataThenEquals() {
        Observation<Double> y = new Observation<>(3.0, dateTime);
        assertThat(x, is(y));
        assertThat(x.hashCode(), is(y.hashCode()));
    }

    @Test
    public void whenSameValueButDifferentObservationPeriodThenNotEqual() {
        Observation<Double> y = new Observation<>(3.0, OffsetDateTime.now());
        assertThat(x, is(not(y)));
    }

    @Test
    public void whenSameObservationPeriodButDifferentValueThenNotEqual() {
        Observation<Double> y = new Observation<>(4.5, dateTime);
        assertThat(x, is(not(y)));
    }

    @Test
    public void testBasicEqualsAndHashCode() {
        assertThat(x, is(x));
        assertThat(x.hashCode(), is(x.hashCode()));
        assertThat(Objects.equals(x, null), is(false));
        Object y = new Object();
        assertThat(x, is(not(y)));
    }

    private LocalDateTime getDefaultLocalDateTime() {
        int year = 2017;
        int month = 1;
        int day = 1;
        int hour = 0;
        int minute = 0;
        return LocalDateTime.of(year, month, day, hour, minute);
    }

    private ZoneOffset getDefaultZoneOffset() {
        return ZoneOffset.UTC;
    }
}
