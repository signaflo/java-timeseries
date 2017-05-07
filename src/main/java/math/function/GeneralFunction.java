/*
 * Copyright (c) 2016 Jacob Rachiele
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
package math.function;

/**
 * A thin wrapper for a {@link Function Function}. Pass a lambda expression representing the function to the constructor
 * to quickly obtain a concrete {@link AbstractFunction} instead of writing a new class that extends AbstractFunction.
 *
 * @author Jacob Rachiele
 */
public class GeneralFunction extends AbstractFunction {

    private final Function f;
    private Function df;

    public GeneralFunction(final Function f) {
        this.f = f;
    }

    public GeneralFunction(final Function f, final Function df) {
        this.f = f;
        this.df = df;
    }

    @Override
    public double at(double point) {
        functionEvaluations++;
        return f.at(point);
    }

    @Override
    public double slopeAt(double point) {
        if (isSet(df)) {
            return df.at(point);
        } else {
            return super.slopeAt(point);
        }
    }

    public void setDf(final Function df) {
        this.df = df;
    }

    private boolean isSet(Function f) {
        return f != null;
    }
}
