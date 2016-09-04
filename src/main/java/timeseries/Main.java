package timeseries;

import data.TestData;
import timeseries.models.Forecast;
import timeseries.models.MeanForecast;
import timeseries.models.MeanModel;
import timeseries.models.Model;
import timeseries.models.RandomWalk;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries series = TestData.ausbeerSeries();
    Model model = new RandomWalk(series);
    Forecast fcst = model.newForecast(12, 0.05);
    fcst.plot();
    fcst.pastAndFuture();
//    MeanModel meanModel = new MeanModel(series);
//    MeanForecast meanFcst = new MeanForecast(meanModel, 12, 0.05);
//    meanFcst.plot();
  }

}