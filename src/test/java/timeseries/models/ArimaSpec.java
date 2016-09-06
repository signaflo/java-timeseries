package timeseries.models;

import java.util.Arrays;

import org.junit.Test;

import data.TestData;
import timeseries.TimeScale;
import timeseries.TimeUnit;

public class ArimaSpec {
  
  @Test
  public void arSarCoeffsMakeSense() {
    double[] arCoeffs = new double[] {0.5, -0.3};
    double[] sarCoeffs = new double[] {0.4, -0.2};
    Arima.ModelCoefficients modelCoeffs = new Arima.ModelCoefficients(arCoeffs, new double[] {}, sarCoeffs,
        new double[] {}, 0, 0, 0.0);
    Arima model = new Arima(TestData.internetTraffic(), modelCoeffs, new TimeUnit(TimeScale.MILLISECOND, 100));
    System.out.println(Arrays.toString(model.expandArCoefficients()));
  }

}
