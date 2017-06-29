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

package timeseries;

import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TimePeriodSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenZeroUnitLengthThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        new TimePeriod(TimeUnit.DAY, 0);
    }

    @Test
    public void whenNegativeUnitLengthThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        new TimePeriod(TimeUnit.DAY, -1);
    }

    @Test
    public final void whenDayTotalSecondsComputedThenResultCorrect() {
        TimePeriod fiveDays = new TimePeriod(TimeUnit.DAY, 5);
        assertThat(fiveDays.totalSeconds(), is(closeTo(432000, 1E-15)));
    }

    @Test
    public final void whenMillisecondsTotalComputedResultCorrect() {
        TimePeriod millis = new TimePeriod(TimeUnit.MILLISECOND, 480);
        assertThat(millis.totalSeconds(), is(equalTo(0.48)));
    }

    @Test
    public void whenNanosecondsTotalComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(TimeUnit.NANOSECOND, 480);
        assertThat(nanos.totalSeconds(), is(equalTo(480 * 1E-9)));
    }

    @Test
    public void whenFrequencyPerComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(TimeUnit.MINUTE, 4);
        assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(equalTo(7.5)));
    }

    @Test
    public void whenMilliFrequencyPerComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(TimeUnit.MILLISECOND, 480);
        assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(equalTo(3750.0)));
    }

    @Test
    public void whenNanoFrequencyPerComputedResultCorrect() {
        TimePeriod nanos = new TimePeriod(TimeUnit.NANOSECOND, 480);
        assertThat(nanos.frequencyPer(TimePeriod.halfHour()), is(closeTo(3750.0 * 1E+6, 1E-4)));
    }

    @Test
    public void whenFrequencyPerReverseComputedResultCorrect() {
        TimePeriod minutes = new TimePeriod(TimeUnit.MINUTE, 45);
        assertThat(minutes.frequencyPer(new TimePeriod(TimeUnit.SECOND, 15)), is(
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
        TimePeriod minute = new TimePeriod(TimeUnit.MINUTE, 1);
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
        TimePeriod halfYear = TimePeriod.halfYear();
        TimePeriod oneQuarter = TimePeriod.oneQuarter();
        TimePeriod nullPeriod = null;
        TimePeriod oneYearAgain = TimePeriod.oneYear();
        String nonTimePeriod = "";
        assertThat(oneYear.hashCode(), is(oneYearAgain.hashCode()));
        assertThat(oneYear, is(oneYearAgain));
        MatcherAssert.assertThat(oneYear, is(not(halfYear)));
        MatcherAssert.assertThat(oneQuarter, is(not(oneYear)));
        MatcherAssert.assertThat(oneYear, is(not(nullPeriod)));
        MatcherAssert.assertThat(oneYear, is(not(nonTimePeriod)));
    }
}
