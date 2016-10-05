package timeseries;

import data.TestData;
import timeseries.models.Arima;
import timeseries.models.ArimaForecast;
import timeseries.models.Arima.ModelOrder;
import timeseries.models.Forecast;
import timeseries.models.Model;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries series = TestData.livestock();
    ModelOrder order = new ModelOrder(1, 1, 1, 0, 0, 0, false);
    Model model = new Arima(series, order, TimePeriod.oneYear());
    Forecast fcst = new ArimaForecast(model, 12, 0.05);
    fcst.plot();
  }

}