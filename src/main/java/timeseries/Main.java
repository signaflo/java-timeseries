package timeseries;

import data.TestData;
import timeseries.models.MeanForecast;
import timeseries.models.MeanModel;
import timeseries.models.RandomWalk;
import timeseries.models.RandomWalkForecast;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries traffic = TestData.sydneyAir();
    RandomWalk model = new RandomWalk(traffic);
    RandomWalkForecast fcst = new RandomWalkForecast(model, 52, 0.05);
    fcst.plot();
    MeanModel meanModel = new MeanModel(traffic);
    MeanForecast meanFcst = new MeanForecast(meanModel, 52, 0.05);
    meanFcst.plot();
    System.out.println(model.residuals().stdDeviation());
    System.out.println(fcst.upperPredictionInterval(52, 0.05));
  }

}