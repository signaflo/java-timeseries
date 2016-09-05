package timeseries.models;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertArrayEquals;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import data.DoubleFunctions;
import data.TestData;
import timeseries.TimeScale;
import timeseries.TimeSeries;

public class MeanSpec {
	
	@Test
	public final void whenMeanForecastComputedForecastValuesCorrect() {
		TimeSeries series = TestData.ausbeerSeries();
		int h = 6;
		MeanModel meanModel = new MeanModel(series);
		TimeSeries pointForecast = meanModel.pointForecast(h);
		double[] expected = DoubleFunctions.fill(h, series.mean());
		assertArrayEquals(expected, pointForecast.series(), 1E-2);
	}
	
	@Test
	public final void whenMeanForecastComputedFirstObservationTimeCorrect() {
		TimeSeries series = TestData.ausbeerSeries();
		TimeSeries pointForecast = new MeanModel(series).pointForecast(6);
		OffsetDateTime expectedTime = OffsetDateTime.of(2008, 10, 1, 0, 0, 0, 0, ZoneOffset.ofHours(0));
		assertThat(pointForecast.observationTimes().get(0), is(equalTo(expectedTime)));
	}
	
	@Test
	public final void whenMeanForecastComputedTimeScaleUnchanged() {
		TimeSeries series = TestData.ausbeerSeries();
		TimeSeries pointForecast = new MeanModel(series).pointForecast(6);
		TimeScale timeScale = TimeScale.QUARTER;
		assertThat(pointForecast.timeScale(), is(equalTo(timeScale)));
	}

}
