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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * A set of constants representing standard time units, ranging from nanoseconds to centuries. This class
 * wraps a {@link ChronoUnit} together with a length of time to create a broader set of time units than
 * those provided by ChronoUnit and to allow additional functionality. For even more fine-tuned time modeling
 * use the {@link TimePeriod} class.
 *
 * @author Jacob Rachiele
 */
public enum TimeUnit {

    CENTURY(ChronoUnit.CENTURIES, 1L),
    DECADE(ChronoUnit.DECADES, 1L),
    YEAR(ChronoUnit.YEARS, 1L),
    QUARTER(ChronoUnit.MONTHS, 3L),
    MONTH(ChronoUnit.MONTHS, 1L),
    WEEK(ChronoUnit.WEEKS, 1L),
    DAY(ChronoUnit.DAYS, 1L),
    HOUR(ChronoUnit.HOURS, 1L),
    MINUTE(ChronoUnit.MINUTES, 1L),
    SECOND(ChronoUnit.SECONDS, 1L),
    MILLISECOND(ChronoUnit.MILLIS, 1L),
    MICROSECOND(ChronoUnit.MICROS, 1L),
    NANOSECOND(ChronoUnit.NANOS, 1L);

    private final TemporalUnit temporalUnit;
    private final long unitLength;

    TimeUnit(final TemporalUnit timeUnit, final long unitLength) {
        this.temporalUnit = timeUnit;
        this.unitLength = unitLength;
    }

    /**
     * The temporal unit used as the basis for this time unit.
     *
     * @return the temporal unit used as the basis for this time unit.
     */
    public TemporalUnit temporalUnit() {
        return this.temporalUnit;
    }

    /**
     * The length of this time unit relative to the underlying temporal unit.
     *
     * @return the length of this time unit relative to the underlying temporal unit.
     */
    public long unitLength() {
        return this.unitLength;
    }

    /**
     * Compute and return the number of times this time unit occurs in another time unit.
     * <p>
     * For example, if this time unit is a month and the other time unit is a year, the return value is 12,
     * since a month occurs 12 times in one year.
     * </p>
     *
     * @param otherTimeUnit the time unit for which the frequency of occurrence of this time unit is to be found.
     * @return the number of times this time unit occurs in the provided time unit.
     */
    public double frequencyPer(final TimeUnit otherTimeUnit) {
        return otherTimeUnit.totalDuration() / this.totalDuration();

    }

    /**
     * Compute and return the number of times this time unit occurs in the given time period.
     * <p>
     * For example, if this time unit is a month and the given time period is half a year, the return value is 6,
     * since a month occurs 6 times in one year.
     * </p>
     *
     * @param timePeriod the time period for which the frequency of occurrence of this time unit is to be found.
     * @return the number of times this time unit occurs in the provided time period.
     */
    public double frequencyPer(final TimePeriod timePeriod) {
        return timePeriod.totalSeconds() / this.totalDuration();
    }

    /**
     * The total amount of time in this time unit measured in seconds, the base SI unit of time.
     *
     * @return the total amount of time in this time unit measured in seconds.
     */
    double totalDuration() {
        final double nanoSecondsPerSecond = 1E9;
        Duration thisDuration = this.temporalUnit.getDuration();
        return thisDuration.getSeconds() * this.unitLength +
               ((thisDuration.getNano() * this.unitLength) / nanoSecondsPerSecond);

    }
}
