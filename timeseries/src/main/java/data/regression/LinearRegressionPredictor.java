package data.regression;

import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;

import java.util.List;

/**
 * An agent with the ability to predict new observations using a linear regression model.
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
    LinearRegressionPrediction predict(Vector observation);

    List<LinearRegressionPrediction> predict(Matrix observations);
}
