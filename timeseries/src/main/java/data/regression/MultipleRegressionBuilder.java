package data.regression;

/**
 * A builder for a multiple linear regression model.
 *
 * @author Jacob Rachiele
 * Aug. 07, 2017
 */
public interface MultipleRegressionBuilder extends RegressionBuilder {

    /**
     * Copy the attributes of the given regression object to this builder and return this builder.
     *
     * @param regression the object to copy the attributes from.
     * @return this builder.
     */
    MultipleRegressionBuilder from(MultipleLinearRegression regression);

    @Override
    MultipleRegressionBuilder hasIntercept(boolean hasIntercept);

    @Override
    MultipleRegressionBuilder response(double... response);

    MultipleRegressionBuilder predictors(double[]... predictors);

    @Override
    MultipleLinearRegression build();
}
