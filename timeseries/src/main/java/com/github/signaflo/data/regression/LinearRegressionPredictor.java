package com.github.signaflo.data.regression;

import com.github.signaflo.math.linear.doubles.Matrix;
import com.github.signaflo.math.linear.doubles.Vector;

import java.util.List;

/**
 * A predictor that makes predictions using a linear regression model.
 *
 * @author Jacob Rachiele
 *         Jul. 22, 2017
 */
public interface LinearRegressionPredictor {

    /**
     * Predict a response using the given observation and significance level.
     *
     * @param observation the new observation.
     * @param alpha the significance level.
     * @return the prediction of the response.
     */
    LinearRegressionPrediction predict(Vector observation, double alpha);

    /**
     * Predict a response using the given observation, with a default significance level of 0.05.
     *
     * @param observation the new observation.
     * @return the prediction of the response.
     */
    default LinearRegressionPrediction predict(Vector observation) {
        return predict(observation, 0.05);
    }

    /**
     * Predict a series of responses, one for each row in the observation matrix.
     *
     * @param observations the new observations.
     * @param alpha the significance level.
     * @return a list of predictions, one for each row in the observation matrix.
     */
    List<LinearRegressionPrediction> predict(Matrix observations, double alpha);

    /**
     * Predict a series of responses, one for each row in the observation matrix. This method
     * uses a default significance level of 0.05.
     *
     * @param observations the new observations.
     * @return a list of predictions, one for each row in the observation matrix.
     */
    default List<LinearRegressionPrediction> predict(Matrix observations) {
        return predict(observations, 0.05);
    }
}
