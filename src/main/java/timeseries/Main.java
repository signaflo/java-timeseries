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
    ModelCoefficients coeffs = ModelCoefficients.newBuilder()//.setMaCoeffs(-0.3975232 )
        //.setSarCoeffs(-0.3947254).setSmaCoeffs(-0.5061460)//.setMaCoeffs(-0.5035514).
        .setDiff(1).setSeasDiff(1).build();
    Arima model = new Arima(series, coeffs, TimePeriod.oneYear(), FittingStrategy.USS);
    Arima mod = new Arima(series, new ModelOrder(0, 1, 0, 0, 1, 1, false), TimePeriod.oneYear(), FittingStrategy.CSS);
    //System.out.println(model.order());
    ArimaForecast fcst = ArimaForecast.forecast(mod, 12, 0.05);
    System.out.println(mod.sigma2());
    System.out.println(mod.logLikelihood());
    System.out.println(mod.coefficients());
    System.out.println(fcst);
//    fcst.plotForecast();
    fcst.plot();
  }
}