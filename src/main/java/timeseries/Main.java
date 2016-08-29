package timeseries;

import data.TestData;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries sydney = TestData.sydneyAir();
    sydney.transform(0.5).plot();
    //livestock.plot();
  }

}