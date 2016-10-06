package timeseries;

import data.TestData;
import timeseries.models.Forecast;
import timeseries.models.Model;
import timeseries.models.arima.Arima;
import timeseries.models.arima.ArimaForecast;
import timeseries.models.arima.FittingStrategy;
import timeseries.models.arima.Arima.ModelCoefficients;
import timeseries.models.arima.Arima.ModelOrder;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries series = TestData.livestock();
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setMaCoeffs()//.setMaCoeffs(-0.5035514).
        .setDiff(1).setMean(4.8576).build();
    Arima model = new Arima(series, coeffs, TimePeriod.oneYear(), FittingStrategy.USS);
    System.out.println(model.logLikelihood());
    ArimaForecast fcst = new ArimaForecast(model, 12, 0.05);
    fcst.plot();
    fcst.pastAndFuture();
  }
}