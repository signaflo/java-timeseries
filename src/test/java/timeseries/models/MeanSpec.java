package timeseries.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import org.junit.Test;

import data.Doubles;
import data.TestData;
import timeseries.TimeSeries;

public class MeanSpec {
	
	@Test
	public final void whenMeanForecastComputedForecastValuesCorrect() {
		TimeSeries series = TestData.ausbeerSeries();
		int h = 6;
		MeanModel meanModel = new MeanModel(series);
		TimeSeries forecast = meanModel.forecast(h);
		double[] expected = Doubles.fill(h, series.mean());
		assertArrayEquals(expected, forecast.series(), 1E-2);
	}
	
	@Test
	public final void whenMeanForecastComputedFirstObservationTimeCorrect() {
		TimeSeries series = TestData.ausbeerSeries();
		TimeSeries forecast = new MeanModel(series).forecast(6);
		OffsetDateTime expectedTime = OffsetDateTime.of(2008, 10, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
		assertThat(forecast.observationTimes().get(0), is(equalTo(expectedTime)));
	}
	
	@Test
	public final void whenMeanForecastComputedTimeScaleUnchanged() {
		TimeSeries series = TestData.ausbeerSeries();
		TimeSeries forecast = new MeanModel(series).forecast(6);
		TemporalUnit timeScale = ChronoUnit.MONTHS;
		assertThat(forecast.timeScale(), is(equalTo(timeScale)));
	}

}
