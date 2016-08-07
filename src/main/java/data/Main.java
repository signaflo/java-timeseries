package data;

import timeseries.TimeSeries;

public class Main {

	public static void main(String[] args) {
		TimeSeries ausBeer = new TimeSeries(TestData.ausbeer());
	    DataSet data = ausBeer.timeSlice(2, 211);
	    data.setName("Australian Beer Production");
	    DataSet lag1 = ausBeer.timeSlice(1, 210);
	    lag1.setName("Australian Beer Production Lagged Once");
	    System.out.println(data + "\n");
	    System.out.println(lag1);
	    data.plotAgainst(lag1);
	}

}
