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
    ModelCoefficients coeffs = ModelCoefficients.newBuilder().setArCoeffs()
        .setSarCoeffs(-0.2312411).setSmaCoeffs(-0.5314621768198619 )//.setMaCoeffs(-0.5035514).
        .setDiff(1).setSeasDiff(1).build();
    Arima model = new Arima(series, coeffs, TimePeriod.oneYear(), FittingStrategy.USS);
    //Arima model = new Arima(series, new ModelOrder(0, 1, 0, 1, 1, 1, false), TimePeriod.oneYear());
    //System.out.println(model.order());
    ArimaForecast fcst = ArimaForecast.forecast(model, 12, 0.05);
    System.out.println(fcst);
    fcst.plotForecast();
    fcst.plot();
  }
}