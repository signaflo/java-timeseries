package timeseries.models.arima;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertArrayEquals;

import static data.DoubleFunctions.newArray;

import org.junit.Rule;
import org.junit.Test;

import data.TestData;
import org.junit.rules.ExpectedException;
import timeseries.TimePeriod;
import timeseries.TimeSeries;
import timeseries.models.Forecast;
import timeseries.models.arima.Arima.ModelCoefficients;
import timeseries.models.arima.Arima.ModelOrder;

import java.util.Arrays;

public class ArimaSpec {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void whenZerosThenFittedAreZero() {
    TimeSeries timeSeries = new TimeSeries(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    double[] expected = new double[timeSeries.n()];
    ModelOrder order = Arima.order(1, 0, 1, 0, 0, 0, true);
    Arima arimaModel = Arima.model(timeSeries, order, Arima.FittingStrategy.USS);
    assertThat(arimaModel.fittedSeries().series(), is(expected));
  }

  @Test
  public void whenSimulateNLessThanOneThenException() {
    exception.expect(IllegalArgumentException.class);
    Arima.Simulation.newBuilder().setN(0);
  }

  @Test
  public void whenSimulateDistributionNullThenNPE() {
    exception.expect(NullPointerException.class);
    Arima.Simulation.newBuilder().setDistribution(null);
  }

  @Test
  public void whenSimulateCoefficientsNullThenNPE() {
    exception.expect(NullPointerException.class);
    Arima.Simulation.newBuilder().setCoefficients(null);
  }

  @Test
  public void whenSimulateThenSeriesOfSizeNReturned() {
    TimeSeries series = Arima.Simulation.newBuilder().setN(50).build().sim();
    assertThat(series.n(), is(50));
    ModelCoefficients coefficients = ModelCoefficients.newBuilder().setARCoeffs(0.4).setSeasonalARCoeffs(0.5).build();
    ModelOrder order = Arima.order(1, 0, 0, 1, 0, 0, false);
    series = Arima.Simulation.newBuilder().setN(120).setCoefficients(coefficients).sim();
    Arima model = Arima.model(series, order, Arima.FittingStrategy.CSS);
    System.out.println(model.coefficients());
    model = Arima.model(series, order, Arima.FittingStrategy.USS);
    System.out.println(model.coefficients());
    model = Arima.model(series, order, Arima.FittingStrategy.ML);
    System.out.println(model.coefficients());
  }
  
  @Test
  public void whenArimaModelFitThenParametersSimilarToROutput() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelOrder order = Arima.order(1, 1, 1);
    Arima model = Arima.model(series, order, TimePeriod.oneYear(), Arima.FittingStrategy.USSML);
    assertThat(model.coefficients().arCoeffs()[0], is(closeTo(0.64, 0.02)));
    assertThat(model.coefficients().maCoeffs()[0], is(closeTo(-0.50, 0.02)));
  }

  @Test
  public void whenArimaModelFitDebitcardsThenParametersSimilarToROutput() throws Exception {
    TimeSeries series = TestData.debitcards();
    ModelOrder order = Arima.order(1, 1, 1, 1, 1, 1);
    Arima model = Arima.model(series, order, TimePeriod.oneYear(), Arima.FittingStrategy.USSML);;
    ModelCoefficients expected = ModelCoefficients.newBuilder().setARCoeffs(-0.1042)
                                                  .setMACoeffs(-0.6213).setSeasonalARCoeffs(0.0051)
                                                  .setSeasonalMACoeffs(-0.5713).setDifferences(1)
                                                  .setSeasonalDifferences(1).build();
    assertArrayEquals(expected.getAllCoeffs(), model.coefficients().getAllCoeffs(), 1E-4);
  }
  
  @Test
  public void whenArimaModelForecastThenForecastValuesCorrect() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelOrder order = Arima.order(1, 1, 1, 0, 0, 0, false);
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setARCoeffs(0.6480679).setMACoeffs(-0.5035514).
        setDifferences(1).build();
    Arima model = Arima.model(series, coeffs, TimePeriod.oneYear());
    double[] expected = {457.660172, 458.904464, 459.71085, 460.233443, 460.572118, 460.791603, 460.933844,
        461.026026, 461.085766, 461.124482};
    assertArrayEquals(expected, model.fcst(10), 1E-4);
  }
  
  @Test
  public void whenArimaModelForecastThenPredictionLevelsAccurate() throws Exception {
    TimeSeries series = TestData.livestock();
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setARCoeffs(-0.4857229).setMACoeffs(-0.5035514)
                                                .setDifferences(1).build();
    Arima model = Arima.model(series, coeffs, TimePeriod.oneYear(), Arima.FittingStrategy.CSS);
    Forecast fcst = model.forecast(10);
    double[] expectedLower = {396.533565, 402.914866, 394.55647, 394.70626, 391.269108, 389.741938, 387.431149,
        385.602156, 383.635953, 381.820598};
    double[] expectedUpper = {488.660387, 495.046987, 497.203721, 500.06622, 502.040234, 504.278084, 506.24368,
        508.240341, 510.125104, 511.980016};
    double[] actualLower = fcst.computeLowerPredictionValues(10, 0.05).series();
    double[] actualUpper = fcst.computeUpperPredictionValues(10, 0.05).series();
    assertArrayEquals(expectedLower, actualLower, 1E-4);
    assertArrayEquals(expectedUpper, actualUpper, 1E-4);
  }

  @Test
  public void whenModelFitThenModelInformationCorrect() {
    ModelCoefficients coefficients = ModelCoefficients.newBuilder().setARCoeffs(-0.5).setMACoeffs(-0.5)
                                                      .setDifferences(1).build();
    Arima model = Arima.model(TestData.livestock(), coefficients, Arima.FittingStrategy.ML);
    assertThat(model.sigma2(), is(closeTo(531.8796, 1E-4)));
    assertThat(model.logLikelihood(), is(closeTo(-210.1396, 1E-4)));
    assertThat(model.aic(), is(closeTo(426.2792, 1E-4)));
  }

  @Test
  public void whenModelFitThenCorrectIntercept() {
    ModelCoefficients coefficients = ModelCoefficients.newBuilder().setMean(4.85376578).setARCoeffs(0.01803952)
                                                      .setDifferences(1).build();
    Arima model = Arima.model(TestData.livestock(), coefficients, Arima.FittingStrategy.ML);
    assertThat(model.coefficients().intercept(), is(closeTo(4.85376578 * (1 - 0.01803952), 1E-4)));
  }

  @Test
  public void testEqualsAndHashCode() {
    TimeSeries series = TestData.livestock();
    ModelOrder order = Arima.order(1, 1, 1);
    ModelOrder order2 = Arima.order(1, 0, 1, true);
    Arima model1 = Arima.model(series, order);
    Arima model2 = Arima.model(series, order2, Arima.FittingStrategy.CSS);
    assertThat(model1, is(model1));
    assertThat(model1, is(not(new Object())));
    assertThat(model1.equals(null), is(false));
    assertThat(model1, is(not(model2)));

    Arima model3 = Arima.model(series, order);
    assertThat(model1, is(model3));
    assertThat(model1.hashCode(), is(model3.hashCode()));
  }

  @Test
  public void testModelInfoEqualsAndHashCode() {
    Arima.ModelInformation info1 = new Arima.ModelInformation(2, 50.0, -100.0,
        newArray(), newArray());
    Arima.ModelInformation info2 = new Arima.ModelInformation(2, 45.0, -90.0,
        newArray(), newArray());
    Arima.ModelInformation info3 = new Arima.ModelInformation(2, 50.0, -100.0,
        newArray(), newArray());
    assertThat(info1, is(info1));
    assertThat(info1.hashCode(), is(info3.hashCode()));
    assertThat(info1, is(info3));
    assertThat(info1, is(not(info2)));
    assertThat(info1, is(not(new Object())));
    assertThat(info1.equals(null), is(false));
  }

  @Test
  public void testModelCoefficientsEqualsAndHashCode() {
    Arima.ModelCoefficients coeffs1 = Arima.ModelCoefficients.newBuilder().setARCoeffs(0.3).build();
    Arima.ModelCoefficients coeffs2 = Arima.ModelCoefficients.newBuilder().setMACoeffs(-0.2).build();
    Arima.ModelCoefficients coeffs3 = Arima.ModelCoefficients.newBuilder().setARCoeffs(0.3).build();
    assertThat(coeffs1, is(coeffs1));
    assertThat(coeffs1.hashCode(), is(coeffs3.hashCode()));
    assertThat(coeffs1, is(coeffs3));
    assertThat(coeffs1, is(not(coeffs2)));
    assertThat(coeffs1, is(not(new Object())));
    assertThat(coeffs1.equals(null), is(false));
  }

}
