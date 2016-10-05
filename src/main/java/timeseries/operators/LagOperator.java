package timeseries.operators;

import java.time.OffsetDateTime;

import timeseries.TimeSeries;

public final class LagOperator {
  
  /**
   * Apply the lag operator once at the given index.
   * @param series the series to apply the lag operation to.
   * @param index the index to apply the lag operation at.
   * @return the value of the series at lag 1 from the given index.
   */
  public static final double apply(final TimeSeries series, final int index) {
    return series.at(index - 1);
  }
  
  /**
   * Apply the lag operator once at the given index.
   * @param series the series to apply the lag operation to.
   * @param dateTime the date and time to apply the lag operation at.
   * @return the value of the series at lag 1 from the given index.
   */
  public static final double apply(final TimeSeries series, final OffsetDateTime dateTime) {
    return series.at(dateTime);
  }
  
  /**
   * Apply the lag operator the given number of times at the given index.
   * @param series the series to apply the lag operation to.
   * @param index the index to apply the lag operation at.
   * @param times the number of times to apply the lag operator.
   * @return the value of the series at the given number of lags from the given index.
   */
  public static final double apply(final TimeSeries series, final int index, final int times) {
    return series.at(index - times);
  }
  
  /**
   * Apply the lag operator the given number of times at the given index.
   * @param series the series to apply the lag operation to.
   * @param dateTime the date and time to apply the lag operation at.
   * @param times the number of times to apply the lag operator.
   * @return the value of the series at the given number of lags from the given index.
   */
  public static final double apply(final TimeSeries series, final OffsetDateTime dateTime, final int times) {
    return series.at(series.dateTimeIndex().get(dateTime) - times);
  }
  
  public static final double apply(final double[] series, final int index, final int times) {
    return series[index - times];
  }

}
