package timeseries;

import timeseries.models.RandomWalk;

final class Main {

	public static void main(String[] args) throws Exception {
		RandomWalk rwalk = RandomWalk.simulate(100);
		//rwalk.timeSeries().plotAcf();
		rwalk.timeSeries().plot();
	}

}
