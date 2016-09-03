package timeseries;

import data.TestData;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries traffic = TestData.internetTraffic();
    traffic.difference().plotAcf(20);
  }

}