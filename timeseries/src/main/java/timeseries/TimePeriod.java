/*
 * Copyright (c) 2016 Jacob Rachiele
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

/**
 * An amount of time expressed in a particular time unit.
 * This class wraps a {@link TimeUnit} together with a positive integer period length,
 * allowing one to create a wide range of different time periods.
 * This class is immutable and thread-safe.
 *
 * @author Jacob Rachiele
 */
public final class TimePeriod {

    private final TimeUnit timeUnit;
    private final long periodLength;

    /**
     * Create a new time period with the given unit of time and period length.
     *
     * @param timeUnit     the unit of time underlying this time period
     * @param periodLength the length of this time period relative to the given unit of time. Note that the
     *                     period length must be a long. Most decimal time periods can be modeled by
     *                     converting to an appropriate time unit with a smaller order of magnitude. For example, the
     *                     {@link TimePeriod#halfMonth} constructor works by converting
     *                     15.2184375 days to 1314873 seconds.
     */
    public TimePeriod(final TimeUnit timeUnit, final long periodLength) {
        validate(periodLength);
        this.timeUnit = timeUnit;
        this.periodLength = periodLength;
    }

    /**
     * Create and return a new TimePeriod representing exactly one hour.
     *
     * @return a new TimePeriod representing exactly one hour.
     */
    public static TimePeriod oneHour() {
        return new TimePeriod(TimeUnit.HOUR, 1);
    }

    /**
     * Create and return a new TimePeriod representing exactly one year.
     *
     * @return a new TimePeriod representing exactly one year.
     */
    public static TimePeriod oneYear() {
        return new TimePeriod(TimeUnit.YEAR, 1);
    }

    /**
     * Create and return a new TimePeriod representing exactly two years.
     *
     * @return a new TimePeriod representing exactly two years.
     */
    public static TimePeriod twoYears() {
        return new TimePeriod(TimeUnit.YEAR, 2);
    }

    /**
     * Create and return a new TimePeriod representing one half of a decade.
     *
     * @return a new TimePeriod representing one half of a decade.
     */
    public static TimePeriod halfDecade() {
        return new TimePeriod(TimeUnit.YEAR, 5);
    }

    /**
     * Create and return a new TimePeriod representing exactly one month.
     *
     * @return a new TimePeriod representing exactly one month.
     */
    public static TimePeriod oneMonth() {
        return new TimePeriod(TimeUnit.MONTH, 1);
    }

    /**
     * Create and return a new TimePeriod representing one half of a month.
     *
     * @return a new TimePeriod representing one half of a month.
     */
    public static TimePeriod halfMonth() {
        final int secondsInHalfMonth = 1314873;
        return new TimePeriod(TimeUnit.SECOND, secondsInHalfMonth);
    }

    /**
     * Create and return a new TimePeriod representing one quarter of a year.
     *
     * @return a new TimePeriod representing one quarter of a year.
     */
    public static TimePeriod oneQuarter() {
        return new TimePeriod(TimeUnit.QUARTER, 1);
    }

    /**
     * Create and return a new TimePeriod representing one week.
     *
     * @return a new TimePeriod representing one week.
     */
    public static TimePeriod oneWeek() {
        return new TimePeriod(TimeUnit.WEEK, 1);
    }

    /**
     * Create and return a new TimePeriod representing one day.
     *
     * @return a new TimePeriod representing one day.
     */
    public static TimePeriod oneDay() {
        return new TimePeriod(TimeUnit.DAY, 1);
    }

    /**
     * Create and return a new TimePeriod representing one half of a year.
     *
     * @return a new TimePeriod representing one half of a year.
     */
    public static TimePeriod halfYear() {
        return new TimePeriod(TimeUnit.MONTH, 6);
    }

    /**
     * Create and return a new TimePeriod representing exactly one decade.
     *
     * @return a new TimePeriod representing exactly one decade.
     */
    public static TimePeriod oneDecade() {
        return new TimePeriod(TimeUnit.DECADE, 1);
    }

    /**
     * Create and return a new TimePeriod representing one half of a century
     *
     * @return a new TimePeriod representing one half of a century.
     */
    public static TimePeriod halfCentury() {
        return new TimePeriod(TimeUnit.DECADE, 5);
    }

    /**
     * Create and return a new TimePeriod representing exactly one century.
     *
     * @return a new TimePeriod representing exactly one century.
     */
    public static TimePeriod oneCentury() {
        return new TimePeriod(TimeUnit.CENTURY, 1);
    }

    /**
     * Create and return a new TimePeriod representing one half of an hour.
     *
     * @return a new TimePeriod representing one half of an hour.
     */
    public static TimePeriod halfHour() {
        return new TimePeriod(TimeUnit.MINUTE, 30);
    }

    /**
     * Create and return a new TimePeriod representing one half of a day.
     *
     * @return a new TimePeriod representing one half of a day.
     */
    public static TimePeriod halfDay() {
        return new TimePeriod(TimeUnit.HOUR, 12);
    }

    /**
     * Create and return a new TimePeriod representing one third of a year.
     *
     * @return a new TimePeriod representing one third of a year.
     */
    public static TimePeriod triAnnual() {
        return new TimePeriod(TimeUnit.MONTH, 4);
    }

    /**
     * Create and return a new TimePeriod representing one second.
     *
     * @return a new TimePeriod representing one second.
     */
    public static TimePeriod oneSecond() {
        return new TimePeriod(TimeUnit.SECOND, 1);
    }

    /**
     * Create and return a new TimePeriod representing one half of a second.
     *
     * @return a new TimePeriod representing one half of a second.
     */
    public static TimePeriod halfSecond() {
        return new TimePeriod(TimeUnit.MILLISECOND, 500);
    }

    /**
     * Create and return a new TimePeriod representing one tenth of a second.
     *
     * @return a new TimePeriod representing one tenth of a second.
     */
    public static TimePeriod oneTenthSecond() {
        return new TimePeriod(TimeUnit.MILLISECOND, 100);
    }

    /**
     * The unit of time underlying this time period.
     *
     * @return the unit of time underlying this time period.
     */
    public TimeUnit timeUnit() {
        return this.timeUnit;
    }

    /**
     * The length of this time period relative to the underlying time unit.
     *
     * @return the length of this time period relative to the underlying time unit.
     */
    public long periodLength() {
        return this.periodLength;
    }

    /**
     * Compute and return the number of times this time period occurs in the given time period.
     * <p>
     * For example, if this time period is a month and the given time period is half a year, the return value is
     * 6 since a month occurs 6 times in one year.
     * </p>
     *
     * @param otherTimePeriod the time period for which the frequency of occurrence of this time period is to be found.
     * @return the number of times this time period occurs in the provided time period.
     */
    public double frequencyPer(final TimePeriod otherTimePeriod) {
        return otherTimePeriod.totalSeconds() / this.totalSeconds();
    }

    /**
     * The total amount of time in this time period measured in seconds, the base SI unit of time.
     *
     * @return the total amount of time in this time period measured in seconds.
     */
    public double totalSeconds() {
        double thisDuration = this.timeUnit.totalDuration();
        return thisDuration * this.periodLength;
    }

    private void validate(final long unitLength) {
        if (unitLength <= 0) {
            throw new IllegalArgumentException("The provided unitLength must be a positive integer");
        }

    }

    @Override
    public String toString() {
        return periodLength + " " + timeUnit + ((periodLength > 1) ? "s" : "");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((timeUnit == null) ? 0 : timeUnit.hashCode());
        long temp;
        temp = Double.doubleToLongBits(periodLength);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TimePeriod other = (TimePeriod) obj;
        return timeUnit == other.timeUnit &&
               Double.doubleToLongBits(periodLength) == Double.doubleToLongBits(other.periodLength);
    }

}
