package data.regression;

import math.linear.doubles.Matrix;
import math.linear.doubles.Vector;

import java.util.List;

/**
 * A predictor that uses linear regression models to make predictions.
 *
 * @author Jacob Rachiele
 *         Jul. 22, 2017
 */
public interface LinearRegressionPredictor {

    /**
     * Predict a response using the given observation.
     *
     * @param observation the new observation.
     * @return the prediction of the response.
     */
    LinearRegressionPrediction predict(Vector observation);

    /**
     * Predict a series of responses, one for each row in the observations matrix.
     *
     * @param observations the new observations.
     * @return a list of predictions, one for each row in the observation matrix.
     */
    List<LinearRegressionPrediction> predict(Matrix observations);
}
