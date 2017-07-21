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

package math.linear;

import math.Rational;
import math.Real;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 26, 2017
 */
public interface Field<T> {

    T zero();
    T one();

    static RealField real() {
        return RealField.getInstance();
    }

    static RationalField rational() {
        return RationalField.getInstance();
    }
}

class RealField implements Field<Real> {

    private RealField() {}
    private static RealField realField;

    static RealField getInstance() {
        if (realField == null) {
            realField = new RealField();
            return realField;
        }
        return realField;
    }

    @Override
    public Real zero() {
        return Real.from(0.0);
    }

    @Override
    public Real one() {
        return Real.from(1.0);
    }
}

class RationalField implements Field<Rational> {

    private RationalField() {}
    private static RationalField rationalField;

    static RationalField getInstance() {
        if (rationalField == null) {
            rationalField = new RationalField();
            return rationalField;
        }
        return rationalField;
    }

    @Override
    public Rational zero() {
        return Rational.from(0);
    }

    @Override
    public Rational one() {
        return Rational.from(1);
    }
}
