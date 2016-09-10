package timeseries;

import static data.DoubleFunctions.newArray;

import data.TestData;
import timeseries.models.Arima;
import timeseries.models.Arima.ModelCoefficients;

final class Main {

  public static void main(String[] args) throws Exception {
    TimeSeries series = TestData.ukcars();
    double[] arCoeffs = newArray( -0.2552);
    double[] sarCoeffs = newArray(-0.6188);
    ModelCoefficients.Builder builder = ModelCoefficients.newBuilder();
    ModelCoefficients modelCoeffs = builder.setArCoeffs(arCoeffs).setSarCoeffs(sarCoeffs)
        .setDiff(1).setSeasDiff(1).build();
    Arima model = new Arima(series, modelCoeffs, TimePeriod.oneYear());
    model.plotFit();
  }

}