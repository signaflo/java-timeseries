package timeseries.models.arima;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;

import org.junit.Test;

import data.TestData;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.arima.Arima;
import timeseries.models.arima.Arima.ModelCoefficients;
import timeseries.models.arima.Arima.ModelOrder;

public class ArimaSpec {
  
  @Test
  public void whenArimaModelFitThenParametersSimilarToROutput() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelOrder order = new ModelOrder(1, 1, 1, 0, 0, 0, false);
    Arima model = Arima.model(series, order, TimePeriod.oneYear(), FittingStrategy.ML);
    assertThat(model.coefficients().arCoeffs()[0], is(closeTo(0.64, 0.02)));
    assertThat(model.coefficients().maCoeffs()[0], is(closeTo(-0.48, 0.02)));
  }

  @Test
  public void whenArimaModelFitDebitcardsThenParametersSimilarToROutput() throws Exception {
    TimeSeries series = TestData.debitcards();
    ModelOrder order = new ModelOrder(1, 1, 1, 1, 1, 1);
    Arima model = Arima.model(series, order, TimePeriod.oneYear(), FittingStrategy.CSS);
    System.out.println(model.coefficients());
    System.out.println(model.sigma2());
    System.out.println(Arrays.toString(model.forecast(12)));
  }
  
  @Test
  public void whenArimaModelForecastThenForecastValuesCorrect() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelOrder order = new ModelOrder(1, 1, 1, 0, 0, 0, false);
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setArCoeffs(0.6480679).setMaCoeffs(-0.5035514).
        setDiff(1).build();
    Arima model = Arima.model(series, coeffs, TimePeriod.oneYear());
    Arima.model(series, order, TimePeriod.oneYear());
    Arima.model(series, order, TimePeriod.oneYear());
    System.out.println(Arrays.toString(model.forecast(10)));
  }
  
  @Test
  public void whenArimaModelForecastThenPsiAccurate() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setArCoeffs(-0.4857229)//.setMaCoeffs(-0.5035514).
        .setDiff(2).build();
    Arima model = Arima.model(series, coeffs, TimePeriod.oneYear(), FittingStrategy.USS);
    ArimaForecast fcst = ArimaForecast.forecast(model, 12, 0.05);
    System.out.println(fcst.computeLowerPredictionValues(12, 0.05));
    System.out.println(fcst.computeUpperPredictionValues(12, 0.05));
    //assertArrayEquals(new double[] {1.0, 1.144516, 1.238173}, fcst.getPsiCoefficients(), 1E-4);
  }

  @Test
  public void testOdyssey() {
    TimeSeries series = TestData.odyssey();
    ModelOrder order = new ModelOrder(1, 0, 1, 1, 0, 1);
    Arima model = Arima.model(series, order, TimePeriod.oneYear(), FittingStrategy.USS);
    System.out.println(model.coefficients());
    System.out.println(Arrays.toString(model.stdErrors()));
    System.out.println(model.sigma2());
    System.out.println(Arrays.toString(model.forecast(12)));
  }

}
