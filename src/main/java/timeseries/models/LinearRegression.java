package timeseries.models;

public final class LinearRegression {
  
  private final double[] dependentData;
  private final double[] independentData;
  
  public LinearRegression(final double[] independentData, final double[] dependentData) {
    this.dependentData = dependentData.clone();
    this.independentData = independentData.clone();
  }
  
  

}
