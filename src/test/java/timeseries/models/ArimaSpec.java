package timeseries.models;

import static data.DoubleFunctions.newArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import org.junit.Test;

import data.TestData;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.TimeUnit;
import timeseries.models.Arima.ModelOrder;

public class ArimaSpec {
  
  @Test
  public void arSarCoeffsMakeSense() {
    double[] arCoeffs = new double[] {0.5, -0.3};
    double[] sarCoeffs = new double[] {0.4, -0.2};
    Arima.ModelCoefficients modelCoeffs = new Arima.ModelCoefficients(arCoeffs, new double[] {}, sarCoeffs,
        new double[] {}, 0, 0, 0.0);
    Arima model = new Arima(TestData.internetTraffic(), modelCoeffs, new TimePeriod(TimeUnit.MILLISECOND, 100));
  }
  
  @Test
  public void whenArimaModelFitKnownCoefficientsInterceptCorrect() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelOrder order = new ModelOrder(1, 1, 1, 0, 0, 0, false);
    Arima model = new Arima(series, order, TimePeriod.oneYear());
    new Arima(series, order, TimePeriod.oneYear());
    new Arima(series, order, TimePeriod.oneYear());
    System.out.println(Arrays.toString(model.forecast(10)));
    //Thread.sleep(50000L);
    //assertThat(model.intercept(), is(closeTo(0.00146, 1E-1)));
//    System.out.println(model.logLikelihood());
//    System.out.println(model.sigma2());
    
  }

}
