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

package math.linear.doubles;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class QuadraticFormSpec {

    Vector a = Vector.from(1.0, 3.0);
    Matrix X = Matrix.create(2, 2, 2.0, 4.0, 6.0, 8.0);
    QuadraticForm Q = new QuadraticForm(a, X);

    @Test
    public void whenMultiplyTwoByTwoThenCorrectResult() {
        assertThat(Q.multiply(), is(104.0));
    }

    @Test
    public void whenMultiplyThreeByThreeThenCorrectResult() {
        double[][] data = {{2.0, 8.0, 14.0}, {4.0, 10.0, 16.0}, {6.0, 12.0, 18.0}};
        a = Vector.from(1.0, 3.0, 5.0);
        X = Matrix.create(data, Matrix.StorageMode.BY_COLUMM);
        Q = new QuadraticForm(a, X);
        assertThat(Q.multiply(), is(1098.0));
    }
}
