/*
 * Copyright (c) 2016 Jacob Rachiele
 */

package timeseries;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class Ts {
	
	private static final int ZONE_OFFSET = 0;
	
	private Ts(){}
	
	/**
	 * Construct a new TimeSeries object with observations made annually starting at the given year.
	 * @param startYear the year of the first observation.
	 * @param series the sequence of observations constituting this time series.
	 * @return a new TimeSeries object with the given series data and start year.
	 */
	public static final TimeSeries newAnnualSeries(final int startYear, final double... series) {
	  final LocalDateTime localDateTime = LocalDateTime.of(startYear, Month.JANUARY, 1, 0, 0);
	  final OffsetDateTime startingInstant = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(ZONE_OFFSET));
	  final TimeUnit timeUnit = TimeUnit.YEAR;
	  final long periodLength = 1;
	  return new TimeSeries(timeUnit, startingInstant, periodLength, series);
	}
	
	/**
	 * Construct a new TimeSeries object with observations made monthly in a yearly cycle,
	 * starting at the given year and month.
	 * @param startYear the year of the first observation.
	 * @param startMonth the month of the first observation - an integer between
	 *  1 and 12 corresponding to the months January through December.
	 * @param series the sequence of observations constituting this time series.
	 * @return a new TimeSeries object with the given series data, start year, and start month.
	 */
	public static final TimeSeries newMonthlySeries(final int startYear, final int startMonth,
			final double... series) {
	  final LocalDateTime localDateTime = LocalDateTime.of(startYear, startMonth, 1, 0, 0);
		final OffsetDateTime startingInstant = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(ZONE_OFFSET));
		final TimeUnit timeUnit = TimeUnit.MONTH;
		final long periodLength = 1;
		return new TimeSeries(timeUnit, startingInstant, periodLength, series);
	}
	
	/**
	 * Construct a new TimeSeries object with a cycle length of one year and monthly observations,
	 * starting at the given year, month, and day.
	 * @param startYear The year of the first observation.
	 * @param startMonth The month of the first observation - an integer between
	 *  1 and 12 corresponding to the months January through December.
	 * @param startDay The day of the first observation - an integer between 1 and 31.
	 * @param series The sequence of observations constituting this time series.
	 * @return A new TimeSeries object with the given year, month, and series data.
	 */
	public static final TimeSeries newMonthlySeries(final int startYear, final int startMonth,
			final int startDay, final double... series) {
		final OffsetDateTime startingInstant = OffsetDateTime.of(startYear, startMonth, startDay, 0, 0, 0, 0,
				ZoneOffset.ofHours(ZONE_OFFSET));
		final TimeUnit timeUnit = TimeUnit.MONTH;
		final long periodLength = 1;
		return new TimeSeries(timeUnit, startingInstant, periodLength, series);
	}
	
	/**
	 * Construct a new TimeSeries object with a cycle length of one year and quarterly observations,
	 * starting at the given year and quarter.
	 * @param startYear The year of the first observation.
	 * @param startQuarter The quarter of the first observation - an integer between
	 *  1 and 4 corresponding to the four quarters of a year.
	 * @param series The sequence of observations constituting this time series.
	 * @return A new TimeSeries object with the given year, quarter, and series data.
	 */
	public static final TimeSeries newQuarterlySeries(final int startYear, final int startQuarter,
			final double... series) {
		final int startMonth  = 3*startQuarter - 2;
		final OffsetDateTime startingInstant = OffsetDateTime.of(startYear, startMonth, 1, 0, 0, 0, 0,
				ZoneOffset.ofHours(0));
		final TimeUnit timeUnit = TimeUnit.QUARTER;
		final long periodLength = 1L;
		return new TimeSeries(timeUnit, startingInstant, periodLength, series);
	}

}
