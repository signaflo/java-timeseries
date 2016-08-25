package timeseries;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public final class Ts {
	
	private static final int ZONE_OFFSET = 0;
	
	private Ts(){}
	
	public static final TimeSeries newAnnualSeries(final int startYear, final double... series) {
	  final LocalDateTime localDateTime = LocalDateTime.of(startYear, Month.JANUARY, 1, 0, 0);
	  final OffsetDateTime startingInstant = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(ZONE_OFFSET));
	  final TemporalUnit timeUnit = ChronoUnit.YEARS;
	  final long periodLength = 1;
	  return new TimeSeries(timeUnit, startingInstant, periodLength, series);
	}
	
	/**
	 * Construct a new TimeSeries object with a cycle length of one year and monthly observations,
	 * starting at the given year and month.
	 * @param startYear The year of the first observation.
	 * @param startMonth The month of the first observation - an integer between
	 *  1 and 12 corresponding to the months January through December.
	 * @param series The sequence of observations constituting this time series.
	 * @return A new TimeSeries object with the given year, month, and series data.
	 */
	public static final TimeSeries newMonthlySeries(final int startYear, final int startMonth,
			final double... series) {
	  final LocalDateTime localDateTime = LocalDateTime.of(startYear, startMonth, 1, 0, 0);
		final OffsetDateTime startingInstant = OffsetDateTime.of(localDateTime, ZoneOffset.ofHours(ZONE_OFFSET));
		final TemporalUnit timeUnit = ChronoUnit.MONTHS;
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
		final TemporalUnit timeUnit = ChronoUnit.MONTHS;
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
		final TemporalUnit timeUnit = ChronoUnit.MONTHS;
		final long periodLength = 3;
		return new TimeSeries(timeUnit, startingInstant, periodLength, series);
	}

}
