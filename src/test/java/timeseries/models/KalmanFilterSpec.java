package timeseries.models;

import org.junit.Test;

import data.TestData;
import timeseries.models.arima.StateSpaceARMA;

public class KalmanFilterSpec {

  @Test
  public void testKalmanFilter() {
    double[] ar = new double[] {-0.03873153};
    double[] ma = new double[] {-0.61187375};
    double[] y = TestData.debitcards().difference(12).difference().series();
    StateSpaceARMA ss = new StateSpaceARMA(y, ar, ma);
    KalmanFilter filter = new KalmanFilter(ss);
    filter.filter();
    filter.filter();
    long start = System.currentTimeMillis();
    for (int i = 0; i < 25; i++) {
      filter.filter();
    }
    long end = System.currentTimeMillis();
    System.out.println("Time taken: " + (end - start) + " millis.");
    
  }
}
