package data.regression;

/**
 * A builder for a regression object.
 *
 * @author Jacob Rachiele
 * Aug. 06, 2017
 */
public interface RegressionBuilder {

    RegressionBuilder response(double... response);

    RegressionBuilder hasIntercept(boolean hasIntercept);

    LinearRegression build();

}
