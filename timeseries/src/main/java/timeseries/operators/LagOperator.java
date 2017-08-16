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
package timeseries.operators;

import timeseries.TimeSeries;

import java.time.OffsetDateTime;

/**
 * Static methods for working with the lag operator.
 *
 * @author Jacob Rachiele
 */
public final class LagOperator {

    private LagOperator() {
    }

    /**
     * Apply the lag operator once at the given index.
     *
     * @param series the series to apply the lag operator to.
     * @param index  the index to apply the lag operator at.
     * @return the value of the series at lag 1 from the given index.
     */
    public static double apply(final TimeSeries series, final int index) {
        return series.at(index - 1);
    }

    /**
     * Apply the lag operator once at the given date-time.
     *
     * @param series   the series to apply the lag operator to.
     * @param dateTime the date-time to apply the lag operator at.
     * @return the value of the series at lag 1 from the given date-time.
     */
    public static double apply(final TimeSeries series, final OffsetDateTime dateTime) {
        return series.at(series.dateTimeIndex().get(dateTime) - 1);
    }

    /**
     * Apply the lag operator the given number of times at the given index.
     *
     * @param series the series to apply the lag operator to.
     * @param index  the index to apply the lag operator at.
     * @param times  the number of times to apply the lag operator.
     * @return the value of the series at the given number of lags from the given index.
     */
    public static double apply(final TimeSeries series, final int index, final int times) {
        return series.at(index - times);
    }

    /**
     * Apply the lag operator the given number of times at the given date-time.
     *
     * @param series   the series to apply the lag operator to.
     * @param dateTime the date-time to apply the lag operator at.
     * @param times    the number of times to apply the lag operator.
     * @return the value of the series at the given number of lags from the given date-time.
     */
    public static double apply(final TimeSeries series, final OffsetDateTime dateTime, final int times) {
        return series.at(series.dateTimeIndex().get(dateTime) - times);
    }

    /**
     * Apply the lag operator the given number of times at the given index.
     *
     * @param series the series to apply the lag operator to.
     * @param index  the index to apply the lag operator at.
     * @param times  the number of times to apply the lag operator.
     * @return the value of the series at the given number of lags from the given index.
     */
    public static double apply(final double[] series, final int index, final int times) {
        return series[index - times];
    }

}
