package timeseries;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TimePeriodSpec {

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
        assertThat(minutes.frequencyPer(new TimePeriod(TimeUnit.SECOND, 15)), is(closeTo(0.00555555556, 1E-4)));
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
}
