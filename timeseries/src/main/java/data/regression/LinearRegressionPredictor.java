package data.regression;

import math.linear.doubles.Vector;

/**
 * An agent with the ability to predict new observations using linear regression models.
 *
 * @author Jacob Rachiele
 *         Jul. 22, 2017
 */
public interface LinearRegressionPredictor {

    /**
     * Predict a response using the given observation.
     *
     * @param observation the new observation data.
     * @return the prediction of the response.
     */
    double predict(Vector observation);
}
