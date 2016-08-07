package timeseries;

public final class Main {

	public static void main(String[] args) {
	    TimeSeries series = new TimeSeries(125, 76, 130, 55, 99, 201, 102, 160, 75, 253);
	    System.out.println(series);
	    series.plot();
	}

}
