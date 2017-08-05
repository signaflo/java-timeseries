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

import data.Pair;
import lombok.NonNull;
import lombok.ToString;
import math.linear.doubles.Matrix;
import math.linear.doubles.QuadraticForm;
import math.linear.doubles.Vector;
import math.stats.distributions.Distribution;
import math.stats.distributions.StudentsT;

@ToString
class MultipleLinearRegressionPredictor implements LinearRegressionPredictor {

    private final LinearRegressionModel model;
    private final Matrix XtXInverse;
    private final int degreesOfFreedom;

    MultipleLinearRegressionPredictor(@NonNull MultipleLinearRegressionModel model) {
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
        Distribution T = new StudentsT(this.degreesOfFreedom);
        double tValue = T.quantile(1 - (alpha / 2.0));
        // send in predictor instead of newData since predict method also updates for intercept.
        double predicted = predict(predictor);
        double seFit = standardErrorFit(predictor);
        return getInterval(predicted, tValue, seFit);
    }

    Pair<Double, Double> predictionInterval(double alpha, Vector predictor) {
        Distribution T = new StudentsT(this.degreesOfFreedom);
        double tValue = T.quantile(1 - (alpha / 2.0));
        double predicted = predict(predictor);
        double seFit = standardErrorFit(predictor);
        double standardError = Math.sqrt(model.sigma2() + seFit * seFit);
        return getInterval(predicted, tValue, standardError);
    }

    private Pair<Double, Double> getInterval(double sampleEstimate, double tValue, double standardError) {
        double lowerValue = sampleEstimate - tValue * standardError;
        double upperValue = sampleEstimate + tValue * standardError;
        return Pair.newPair(lowerValue, upperValue);
    }

    @Override
    public double predict(Vector observation) {
        Vector data = predictorWithIntercept(observation);
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
            Vector ones = Vector.ones(newData.nrow());
            return newData.pushColumn(ones);
        }
        return newData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MultipleLinearRegressionPredictor that = (MultipleLinearRegressionPredictor) o;

        if (!model.equals(that.model)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return model.hashCode();
    }
}
