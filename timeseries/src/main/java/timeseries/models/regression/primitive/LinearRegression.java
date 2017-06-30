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
package timeseries.models.regression.primitive;

/**
 * A math.linear timeseries.models.regression model.
 */
public interface LinearRegression {

    /**
     * Get the prediction variables.
     *
     * @return the prediction variables.
     */
    double[][] predictors();

    double[][] designMatrix();

    /**
     * Get the response variable.
     *
     * @return the response variable.
     */
    double[] response();

    /**
     * Get the estimated coefficients.
     *
     * @return the estimated coefficients.
     */
    double[] beta();

    /**
     * Get the standard errors of the estimated coefficients.
     *
     * @return the standard errors of the estimated coefficients.
     */
    double[] standardErrors();

    /**
     * Get the model fitted values.
     *
     * @return the model fitted values.
     */
    double[] fitted();

    /**
     * Get the model residuals.
     *
     * @return the model residuals.
     */
    double[] residuals();

    /**
     * Get the model error variance estimate.
     *
     * @return the model error variance estimate.
     */
    double sigma2();

    /**
     * Get whether or not the model includes an intercept.
     *
     * @return whether or not the model includes an intercept.
     */
    boolean hasIntercept();
}
