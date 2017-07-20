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

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * Static factory constructors for {@link TimeSeries} objects.
 *
 * @author Jacob Rachiele
 */
public final class Ts {

    private static final int ZONE_OFFSET = 0;

    private Ts() {
    }

    /**
     * Construct a new time series with observations made annually starting at the given year.
     *
     * @param startYear the year of the first observation.
     * @param series    the sequence of observations constituting this time series.
     * @return a new time series with the given series data and start year.
     */
    public static TimeSeries newAnnualSeries(final int startYear, final double... series) {
        final LocalDateTime localDateTime = LocalDateTime.of(startYear, Month.JANUARY, 1, 0, 0);
        final OffsetDateTime startPeriod = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(ZONE_OFFSET));
        return TimeSeries.from(TimePeriod.oneYear(), startPeriod, series);
    }

    /**
     * Construct a new time series with observations made annually, without regard to the observation dates.
     *
     * @param series the sequence of observations constituting this time series.
     * @return a new time series with the given series data.
     */
    public static TimeSeries newAnnualSeries(final double... series) {;
        return TimeSeries.from(TimePeriod.oneYear(), series);
    }

    /**
     * Construct a new time series of monthly observations without regard to the observation dates.
     *
     * @param series the sequence of observations constituting this time series.
     * @return a new time series with the given series data.
     */
    public static TimeSeries newMonthlySeries(final double... series) {
        return TimeSeries.from(TimePeriod.oneMonth(), series);
    }

    /**
     * Construct a new time series with monthly observations starting at the given year and month.
     *
     * @param startYear  the year of the first observation.
     * @param startMonth the month of the first observation - an integer between
     *                   1 and 12 corresponding to the months January through December.
     * @param series     the sequence of observations constituting this time series.
     * @return a new time series with the given series data, start year, and start month.
     */
    public static TimeSeries newMonthlySeries(final int startYear, final int startMonth, final double... series) {
        return newMonthlySeries(startYear, startMonth, 1, series);
    }

    /**
     * Construct a new time series with monthly observations starting at the given year, month, and day.
     *
     * @param startYear  The year of the first observation.
     * @param startMonth The month of the first observation - an integer between
     *                   1 and 12 corresponding to the months January through December.
     * @param startDay   The day of the first observation - an integer between 1 and 31.
     * @param series     The sequence of observations constituting this time series.
     * @return A new time series with the given year, month, day, and series data.
     */
    public static TimeSeries newMonthlySeries(final int startYear, final int startMonth, final int startDay,
                                              final double... series) {
        final OffsetDateTime startPeriod = OffsetDateTime
                .of(startYear, startMonth, startDay, 0, 0, 0, 0, ZoneOffset.ofHours(ZONE_OFFSET));
        return TimeSeries.from(TimePeriod.oneMonth(), startPeriod, series);
    }

    /**
     * Construct a new time series with quarterly observations without regard to the observation dates.
     *
     * @param series       The sequence of observations constituting this time series.
     * @return A new time series with the given year, quarter, and series data.
     */
    public static TimeSeries newQuarterlySeries(final double... series) {
        return TimeSeries.from(TimePeriod.oneQuarter(), series);
    }

    /**
     * Construct a new time series with quarterly observations starting at the given year and quarter.
     *
     * @param startYear    The year of the first observation.
     * @param startQuarter The quarter of the first observation - an integer between
     *                     1 and 4 corresponding to the four quarters of a year.
     * @param series       The sequence of observations constituting this time series.
     * @return A new time series with the given year, quarter, and series data.
     */
    public static TimeSeries newQuarterlySeries(final int startYear, final int startQuarter, final double... series) {
        final int startMonth = 3 * startQuarter - 2;
        final OffsetDateTime startPeriod = OffsetDateTime
                .of(startYear, startMonth, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
        return TimeSeries.from(TimePeriod.oneQuarter(), startPeriod, series);
    }

    public static TimeSeries newWeeklySeries(double... series) {
        return TimeSeries.from(TimePeriod.oneWeek(), series);
    }

    /**
     * Construct a new time series with weekly observations starting at the given year, month, and day.
     *
     * @param startYear  The year of the first observation.
     * @param startMonth The month of the first observation - an integer between
     *                   1 and 12 corresponding to the months January through December.
     * @param startDay   The day of the first observation - an integer between 1 and 31.
     * @param series     The sequence of observations constituting this time series.
     * @return A new time series with the given year, month, day, and series data.
     */
    public static TimeSeries newWeeklySeries(int startYear, int startMonth, int startDay, double... series) {
        final OffsetDateTime startPeriod = OffsetDateTime
                .of(startYear, startMonth, startDay, 0, 0, 0, 0, ZoneOffset.ofHours(ZONE_OFFSET));
        return TimeSeries.from(TimePeriod.oneWeek(), startPeriod, series);
    }

}
