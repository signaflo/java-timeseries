package data;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;


public class TimeSeriesSpec {
	
	@Test
	public void whenTimeSeriesMeanTakenThenResultCorrect() {
		double[] data = new double[] {3.0, 7.0, 5.0};
		TimeSeries series = new TimeSeries(data);
		assertThat(series.mean(), is(equalTo(5.0)));
	}

}
