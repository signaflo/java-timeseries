package timeseries;

import data.TestData;
import timeseries.models.RandomWalk;

final class Main {

	public static void main(String[] args) throws Exception {
		TestData.ausbeerSeries();
	    RandomWalk rwalk = RandomWalk.simulate(100);
		rwalk.plotResiduals();
	}

}
