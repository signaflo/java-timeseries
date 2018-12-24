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

package com.github.signaflo.timeseries;

import com.google.common.testing.EqualsTester;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.temporal.ChronoUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TimePeriodSpec {
    
    private static final double EPSILON = Math.ulp(1.0);

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenNullTemporalUnitThenNPE() {
        exception.expect(NullPointerException.class);
        new TimePeriod(null, 1);
    }

    @Test
    public void whenZeroPeriodLengthThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        new TimePeriod(ChronoUnit.DAYS, 0);
    }

    @Test
    public void whenNegativePeriodLengthThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        new TimePeriod(ChronoUnit.DAYS, -1);
    }

    @Test
    public final void whenDayTotalSecondsComputedThenResultCorrect() {
        TimePeriod fiveDays = new TimePeriod(ChronoUnit.DAYS, 5);
        assertThat(fiveDays.totalSeconds(), is(closeTo(432000, EPSILON)));
    }

    @Test
    public final void whenMillisecondsTotalComputedResultCorrect() {
        TimePeriod millis = new TimePeriod(ChronoUnit.MILLIS, 480);
        assertThat(millis.totalSeconds(), is(equalTo(0.48)));
    }

    @Test
    public void whenNanosecondsTotalComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(ChronoUnit.NANOS, 480);
        double expectedNanos = 4.8E-7;
        assertThat(nanos.totalSeconds(), is(equalTo(expectedNanos)));
    }

    @Test
    public void whenFrequencyPerComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(ChronoUnit.MINUTES, 4);
        assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(equalTo(7.5)));
    }

    @Test
    public void whenMilliFrequencyPerComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(ChronoUnit.MILLIS, 480);
        assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(equalTo(3750.0)));
    }

    @Test
    public void whenNanoFrequencyPerComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(ChronoUnit.NANOS, 480);
        assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(closeTo(3750.0 * 1E+6, 1E-4)));
    }

    @Test
    public void whenFrequencyPerReverseComputedResultCorrect() {
        TimePeriod minutes = new TimePeriod(ChronoUnit.MINUTES, 45);
        assertThat(minutes.frequencyPer(new TimePeriod(ChronoUnit.SECONDS, 15)), is(
                closeTo(0.00555555556, 1E-4)));
    }

    @Test
    public void whenHalfDayCreatedThenTwelveHours() {
        TimePeriod halfDay = TimePeriod.halfDay();
        assertThat(TimePeriod.oneHour().frequencyPer(halfDay), is(12.0));
    }

    @Test
    public void whenOneHourCreatedThenSixtyMinutes() {
        TimePeriod hour = TimePeriod.oneHour();
        TimePeriod minute = new TimePeriod(ChronoUnit.MINUTES, 1);
        assertThat(minute.frequencyPer(hour), is(60.0));
    }

    @Test
    public void whenHalfMonthThenTwentyFourInOneYear() {
        TimePeriod halfMonth = TimePeriod.halfMonth();
        TimePeriod oneYear = TimePeriod.oneYear();
        assertThat(halfMonth.frequencyPer(oneYear), is(24.0));
    }

    @Test
    public void whenHalfYearThenTwoInOneYear() {
        TimePeriod halfYear = TimePeriod.halfYear();
        TimePeriod oneYear = TimePeriod.oneYear();
        assertThat(halfYear.frequencyPer(oneYear), is(2.0));
    }

    @Test
    public void whenOneMonthThenTwelveInOneYear() {
        TimePeriod oneMonth = TimePeriod.oneMonth();
        TimePeriod oneYear = TimePeriod.oneYear();
        assertThat(oneMonth.frequencyPer(oneYear), is(12.0));
    }

    @Test
    public void whenTwoYearsThenFiveInADecade() {
        TimePeriod twoYears = TimePeriod.twoYears();
        TimePeriod oneDecade = TimePeriod.oneDecade();
        assertThat(twoYears.frequencyPer(oneDecade), is(5.0));
    }

    @Test
    public void whenHalfCenturyThenTwoInOneCentury() {
        TimePeriod halfCentury = TimePeriod.halfCentury();
        TimePeriod oneCentury = TimePeriod.oneCentury();
        assertThat(halfCentury.frequencyPer(oneCentury), is(2.0));
    }

    @Test
    public void whenOneDayThenSevenInOneWeek() {
        TimePeriod oneDay = TimePeriod.oneDay();
        TimePeriod oneWeek = TimePeriod.oneWeek();
        assertThat(oneDay.frequencyPer(oneWeek), is(7.0));
    }

    @Test
    public void whenOneTenthSecondThenTenInOneSecond() {
        TimePeriod oneTenthSecond = TimePeriod.oneTenthSecond();
        TimePeriod oneSecond = TimePeriod.oneSecond();
        assertThat(oneTenthSecond.frequencyPer(oneSecond), is(10.0));
    }

    @Test
    public void WhenHalfSecondThenTwoInOneSecond() {
        TimePeriod halfSecond = TimePeriod.halfSecond();
        TimePeriod oneSecond = TimePeriod.oneSecond();
        assertThat(halfSecond.frequencyPer(oneSecond), is(2.0));
    }

    @Test
    public void whenOneQuarterThenFourInOneYear() {
        TimePeriod oneQuarter = TimePeriod.oneQuarter();
        TimePeriod oneYear = TimePeriod.oneYear();
        assertThat(oneQuarter.frequencyPer(oneYear), is(4.0));
    }

    @Test
    public void whenHalfDecadeThenTwoInOneDecade() {
        TimePeriod halfDecade = TimePeriod.halfDecade();
        TimePeriod oneDecade = TimePeriod.oneDecade();
        assertThat(halfDecade.frequencyPer(oneDecade), is(2.0));
    }

    @Test
    public void whenTriAnnualThenThreeInOneYear() {
        TimePeriod triAnnual = TimePeriod.triAnnual();
        TimePeriod oneYear = TimePeriod.oneYear();
        assertThat(triAnnual.frequencyPer(oneYear), is(3.0));
    }

    @Test
    public void testEqualsAndHashCode() {
        TimePeriod oneYear = TimePeriod.oneYear();
        TimePeriod oneMonth = TimePeriod.oneMonth();
        TimePeriod halfYear = TimePeriod.halfYear();
        TimePeriod oneQuarter = TimePeriod.oneQuarter();
        TimePeriod twoQuarters = new TimePeriod(ChronoUnit.MONTHS, 6);
        TimePeriod oneYearAgain = TimePeriod.oneYear();
        String nonTimePeriod = "";
        new EqualsTester()
            .addEqualityGroup(oneYear, oneYearAgain)
            .addEqualityGroup(oneMonth)
            .addEqualityGroup(halfYear, twoQuarters)
            .addEqualityGroup(oneQuarter)
            .addEqualityGroup(nonTimePeriod)
            .testEquals();
    }
}
