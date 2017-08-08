package data.regression;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Aug. 07, 2017
 */
public interface MultipleLinearRegression extends LinearRegression {

    /**
     * Create and return a new builder for a linear regression model.
     *
     * @return a new builder for a linear regression model.
     */
    static MultipleRegressionBuilder builder() {
        return new MultipleLinearRegressionModel.MultipleLinearRegressionBuilder();
    }

    /**
     * Get the prediction variables.
     *
     * @return the prediction variables.
     */
    double[][] predictors();

    double[][] XtXInverse();

    double[][] designMatrix();
}
