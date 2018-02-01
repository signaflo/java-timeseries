/*
 * Copyright (c) 2018 Jacob Rachiele
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

package com.github.signaflo.streaming;

import com.github.signaflo.data.Range;
import com.github.signaflo.math.stats.distributions.Distribution;
import com.github.signaflo.math.stats.distributions.Normal;
import org.junit.Test;

public class FixedSizeStreamingSeriesSpec {

    Distribution dist = new Normal(0.0, 1.0);
    private final double[] x = Range.inclusiveRange(1.0, 1000.0, 2).asArray();
    private final double[] y = Range.inclusiveRange(2.0, 1000.0, 2).asArray();

    @Test
    public void testSeries() {
        FixedSizeStreamingSeries<Double> series = new FixedSizeStreamingSeries<>(100);
        Thread t1 = new Thread(() -> {
            for (double elem : x) {
                series.add(elem);
            }
        });
        Thread t2 = new Thread(() -> {
            for (double elem : y) {
                series.add(elem);
            }
        });
        t1.start();
        t2.start();
        System.out.println(series.elements.size());
    }
}
