package data.regression;

/**
 * A multiple linear regression model.
 *
 * @author Jacob Rachiele
 * Aug. 07, 2017
 */
public interface MultipleLinearRegression extends LinearRegression {

    /**
     * Create and return a new builder for a multiple linear regression model.
     *
     * @return a new builder for a multiple linear regression model.
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

    /**
     * Get (<em>X</em><sup>T</sup><em>X</em>)<sup>-1</sup>, where <em>X</em> is the model design matrix.
     *
     * @return (<em>X</em><sup>T</sup><em>X</em>)<sup>-1</sup>, where <em>X</em> is the model design matrix.
     */
    double[][] XtXInverse();

    /**
     * Get the model design matrix.
     *
     * @return the model design matrix.
     */
    double[][] designMatrix();
}
