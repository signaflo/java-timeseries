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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import io.reactivex.Flowable;
import io.reactivex.subscribers.DefaultSubscriber;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.time.Instant;

public class StreamingSeriesSpec {

    private final long lastValue = 3L;
    private Publisher<Long> publisher = Flowable.just(1L, lastValue);
    private StreamingSeries<Long> series = StreamingSeries.getStreamingSeriesBuilder(publisher).build();
    private Long currentValue = 0L;
    private String status = "Uninitialized";

    private Subscriber<Long> subscriber = new DefaultSubscriber<>() {
        @Override
        public void onNext(Long l) {
            SortedMapping<Instant, Long> map = series.getObservations();
            if (!map.isEmpty()) {
                currentValue = map.lastValue().orElseThrow(RuntimeException::new);
            }
        }

        @Override
        public void onError(Throwable t) {
            t.printStackTrace();
        }

        @Override
        public void onComplete() {
            status = "Completed";
        }
    };

    @Test
    public void whenSeriesSubscribedToThenStateChangesVisible() {
        series.subscribe(subscriber);
        System.out.println(series.getObservations());
        assertThat(currentValue, is(lastValue));
        assertThat(status, is("Completed"));
        SortedMapping<Instant, Long> observations = series.getObservations();
        System.out.println(observations.comparator());
        System.out.println(observations.remove(observations.firstKey()));
        System.out.println(series.getObservations());
    }
}
