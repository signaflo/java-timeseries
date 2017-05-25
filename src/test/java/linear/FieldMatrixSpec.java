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

package linear;

import math.Complex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldMatrixSpec {

    private Complex c1 = new Complex(3, 5);
    private Complex c2 = new Complex(2.5, 4.5);
    private Complex c3 = new Complex(2.4, 4.0);
    private Complex c4 = new Complex(6, 2);

    private FieldVector<Complex> vec1;
    private FieldVector<Complex> vec2;

    @Before
    public void beforeMethod() {
        List<Complex> l1 = new ArrayList<>(2);
        l1.add(c1);
        l1.add(c2);
        List<Complex> l2 = new ArrayList<>(2);
        l2.add(c3);
        l2.add(c4);
        vec1 = new FieldVector<>(l1);
        vec2 = new FieldVector<>(l2);
    }

    @Test
    public void testMatrixCreation() {
        FieldMatrix<Complex> matrix = new FieldMatrix<>(Arrays.asList(vec1, vec2));

    }
}
