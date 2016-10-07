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
    TimeSeries series = TestData.ukcars();
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setArCoeffs(-0.3974)
        .setSarCoeffs().setSmaCoeffs(-0.9197)//.setMaCoeffs(-0.5035514).
        .setDiff(1).setSeasDiff(1).build();
    Arima model = new Arima(series, coeffs, TimePeriod.oneYear(), FittingStrategy.CSS);
    Arima mod = new Arima(series, new ModelOrder(1, 1, 0, 0, 1, 1, false), TimePeriod.oneYear(), FittingStrategy.USS);
    //System.out.println(model.order());
    ArimaForecast fcst = ArimaForecast.forecast(mod, 12, 0.05);
    System.out.println(model.sigma2());
//    fcst.plotForecast();
//    fcst.plot();
  }
}