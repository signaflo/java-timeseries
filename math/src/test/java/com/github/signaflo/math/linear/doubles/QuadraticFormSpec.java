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

package com.github.signaflo.math.linear.doubles;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class QuadraticFormSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Vector x = Vector.from(1.0, 3.0);
    private Matrix A = Matrix.create(2, 2, 2.0, 4.0, 6.0, 8.0);
    private QuadraticForm q = new QuadraticForm(x, A);

    @Test
    public void whenNonSquareMatrixThenIllegalArgument() {
        A = Matrix.create(2, 3, 2.0, 4.0, 6.0, 8.0, 5.5, 1.5);
        String expectedMessage = "The matrix must be square.";
        exception.expectMessage(expectedMessage);
        exception.expect(IllegalArgumentException.class);
        new QuadraticForm(x, A);
    }

    @Test
    public void whenIncorrectDimensionsThenIllegalArgument() {
        A = Matrix.create(3, 3, 2.0, 4.0, 6.0, 8.0, 5.5, 1.5, 9.0, -5.4, 7.5);
        String expectedMessage = "The number of matrix columns must be the same" +
                                 " as the size of the vector.";
        exception.expectMessage(expectedMessage);
        exception.expect(IllegalArgumentException.class);
        new QuadraticForm(x, A);
    }

    @Test
    public void whenMultiplyTwoByTwoThenCorrectResult() {
        assertThat(q.multiply(), is(104.0));
        assertThat(QuadraticForm.multiply(x, A), is(104.0));
    }

    @Test
    public void whenMultiplyThreeByThreeThenCorrectResult() {
        double[][] data = {{2.0, 8.0, 14.0}, {4.0, 10.0, 16.0}, {6.0, 12.0, 18.0}};
        x = Vector.from(1.0, 3.0, 5.0);
        A = Matrix.create(Matrix.Layout.BY_COLUMN, data);
        q = new QuadraticForm(x, A);
        assertThat(q.multiply(), is(1098.0));
        assertThat(QuadraticForm.multiply(x, A), is(1098.0));
    }

    @Test
    public void testEqualsAndHashCode() {
        double[][] data = {{2.0, 8.0, 14.0}, {4.0, 10.0, 16.0}, {6.0, 12.0, 18.0}};
        Vector y = Vector.from(1.0, 3.0, 5.0);
        Matrix B = Matrix.create(Matrix.Layout.BY_COLUMN, data);
        QuadraticForm r = new QuadraticForm(y, B);
        QuadraticForm s = new QuadraticForm(x, A);
        assertThat(q, is(q));
        assertThat(q, is(s));
        assertThat(q.hashCode(), is(s.hashCode()));
        assertThat(r, is(not(s)));
        assertThat(q, is(notNullValue()));
    }
}
