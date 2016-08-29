package timeseries;

import data.TestData;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries sydney = TestData.sydneyAir();
    sydney.difference().plot();
    //livestock.plot();
  }

}