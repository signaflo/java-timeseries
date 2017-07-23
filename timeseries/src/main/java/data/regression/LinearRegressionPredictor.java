package data.regression;

import math.linear.doubles.Vector;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jul. 22, 2017
 */
public interface LinearRegressionPredictor {

    double predict(Vector newData);
}
