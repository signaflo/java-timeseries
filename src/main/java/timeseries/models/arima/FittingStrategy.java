package timeseries.models.arima;

/**
 * The strategy to be used for fitting an ARIMA model.
 * @author Jacob Rachiele
 *
 */
public enum FittingStrategy {
  
  /**
   * Conditional sum-of-squares.
   */
  CSS, 
  
  /**
   * Unconditional sum-of-squares.
   */
  USS,
//  
//  /**
//   * Maximum likelihod.
//   */
//  ML;

}
