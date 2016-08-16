package timeseries;

import data.TestData;
import timeseries.models.RandomWalk;

final class Main {

	public static void main(String[] args) throws Exception {
		RandomWalk rwalk = RandomWalk.simulate(100);
		rwalk.timeSeries().plotAcf();
		//rwalk.timeSeries().plot();
		try {
		    Thread.sleep(5000);
		} catch (Exception e) {}
	}

}
