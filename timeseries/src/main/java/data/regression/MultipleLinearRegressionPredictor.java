/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package data.regression;


import data.DoubleFunctions;
import data.Pair;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import math.linear.doubles.Matrix;
import math.linear.doubles.QuadraticForm;
import math.linear.doubles.Vector;
import math.stats.distributions.Distribution;
import math.stats.distributions.StudentsT;

import java.util.Iterator;

@EqualsAndHashCode @ToString
public class MultipleLinearRegressionPredictor implements LinearRegressionPredictor{

    private final LinearRegressionModel model;
    private final Matrix XtXInverse;
    private final int degreesOfFreedom;

    MultipleLinearRegressionPredictor(MultipleLinearRegressionModel model) {
        this.model = model;
        this.XtXInverse = Matrix.create(model.XtXInverse());
        this.degreesOfFreedom = model.response().length - model.designMatrix().length;
    }

    double standardErrorFit(Vector newPredictor) {
        Vector predictorWithIntercept = predictorWithIntercept(newPredictor);
        double product = QuadraticForm.multiply(predictorWithIntercept, XtXInverse);
        return Math.sqrt(model.sigma2() * product);
    }

    Vector standardErrorFit(Matrix newPredictors) {
        double[] seFit = new double[newPredictors.nrow()];
        for (int i = 0; i < seFit.length; i++) {
            seFit[i] = standardErrorFit(newPredictors.getRow(i));
        }
        return Vector.from(seFit);
    }

    Pair<Double, Double> confidenceInterval(double alpha, Vector predictor) {
        Vector newData = predictorWithIntercept(predictor);
        Distribution T = new StudentsT(this.degreesOfFreedom);
        double tValue = T.quantile(1 - (alpha / 2.0));
        // send in predictor instead of newData since predict method also updates for intercept.
        double predicted = predict(predictor);
        double seFit = standardErrorFit(newData);
        double lowerValue = predicted - tValue * seFit;
        double upperValue = predicted + tValue * seFit;
        return Pair.newPair(lowerValue, upperValue);
    }

    @Override
    public double predict(Vector newData) {
        Vector data = predictorWithIntercept(newData);
        return data.dotProduct(Vector.from(model.beta()));
    }

    public Vector predict(Matrix newData) {
        Vector beta = Vector.from(model.beta());
        Matrix predictionMatrix = predictorsWithIntercept(newData);
        return predictionMatrix.times(beta);
    }

    private Vector predictorWithIntercept(Vector newData) {
        if (model.hasIntercept()) {
            return newData.push(1.0);
        } else {
            return newData;
        }
    }

    private Matrix predictorsWithIntercept(Matrix newData) {
        if (model.hasIntercept()) {
            double[] ones = DoubleFunctions.fill(newData.nrow(), 1.0);
            return newData.push(ones, false);
        }
        return newData;
    }

//    private double[][] copy(double[][] values) {
//        double[][] copied = new double[values.length][];
//        for (int i = 0; i < values.length; i++) {
//            copied[i] = values[i].clone();
//        }
//        return copied;
//    }
}
