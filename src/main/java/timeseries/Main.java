package timeseries;

import data.TestData;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries livestock = TestData.livestock();
    TimeSeries ausbeer = TestData.ausbeerSeries();
    livestock.print();
    //livestock.plot();
  }

}