package timeseries;

import data.TestData;

public final class Main {

	public static void main(String[] args) {
	    TimeSeries series = TestData.ausbeerSeries();
	    System.out.println(series);
	    series.plotAcf();
	}

}
