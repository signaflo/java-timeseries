package data.regression;

/**
 * [Insert class description]
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

    MultipleRegressionBuilder predictors(double[]... predictors);
}
