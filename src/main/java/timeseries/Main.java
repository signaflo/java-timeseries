package timeseries;

import data.TestData;
import timeseries.models.RandomWalk;

final class Main {

	public static void main(String[] args) {
		RandomWalk rwalk = RandomWalk.simulate(100);
		rwalk.residualPlot();
	}

}
