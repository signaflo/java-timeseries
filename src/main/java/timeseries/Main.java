package timeseries;

import data.TestData;
import timeseries.models.RandomWalk;

final class Main {

	public static void main(String[] args) throws Exception {
		final TimeSeries timeSeries = TestData.ausbeerSeries();
		//RandomWalk rwalk = RandomWalk.simulate(100);
		//rwalk.timeSeries().plotAcf();
		timeSeries.plot();
	}

}
