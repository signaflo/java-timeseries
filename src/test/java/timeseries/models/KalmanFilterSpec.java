package timeseries.models;

import org.junit.Ignore;
import org.junit.Test;

import data.TestData;
import timeseries.models.arima.StateSpaceARMA;

public class KalmanFilterSpec {

  @Test
  @Ignore
  public void testKalmanFilter() throws Exception {
    double[] ar = new double[] {0.3114114};
    double[] ma = new double[] {-0.8373430,  0.0,  0.0,  0.3854193, -0.3227282};
    double[] y = TestData.ukcars().difference().series();
    StateSpaceARMA ss = new StateSpaceARMA(y, ar, ma);
    new KalmanFilter(ss);
    new KalmanFilter(ss);
    new KalmanFilter(ss);
    long start = System.currentTimeMillis();
    for (int i = 0; i < 25; i++) {
      new KalmanFilter(ss);
    }
    long end = System.currentTimeMillis();
    System.out.println("Time taken: " + (end - start) + " millis.");
  }
}
