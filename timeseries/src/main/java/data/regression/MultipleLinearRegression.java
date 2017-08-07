package data.regression;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Aug. 07, 2017
 */
public interface MultipleLinearRegression extends LinearRegression {
    /**
     * Get the prediction variables.
     *
     * @return the prediction variables.
     */
    double[][] predictors();
}
