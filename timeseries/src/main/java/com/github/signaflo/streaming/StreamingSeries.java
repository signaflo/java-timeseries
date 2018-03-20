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

import com.github.signaflo.timeseries.Observation;
import com.github.signaflo.timeseries.TimePeriod;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.Flow;

/**
 * A stream of observations generated sequentially in time, where observations are made at a fixed time interval.
 *
 * @param <T> The type of value observed.
 */
public class StreamingSeries<T extends Number> implements Flow.Processor<T, T> {

    private static final Logger logger = LoggerFactory.getLogger(StreamingSeries.class);

    private final String name;
    private final Publisher<T> publisher;
    private final TimePeriod samplingInterval;
    private OffsetDateTime currentObservationPeriod;
    private final SortedLinkedHashMap<OffsetDateTime, T> observations;

    private StreamingSeries(StreamingSeriesBuilder<T> seriesBuilder) {
        this.name = seriesBuilder.name;
        this.publisher = seriesBuilder.publisher;
        this.samplingInterval = seriesBuilder.samplingInterval;
        this.currentObservationPeriod = seriesBuilder.startPeriod;
        this.observations = new SortedLinkedHashMap<>(seriesBuilder.memory);
        this.publisher.subscribe(FlowAdapters.toSubscriber(this));
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T observed) {
        currentObservationPeriod = currentObservationPeriod.plus(samplingInterval.unitLength(),
                                                                 samplingInterval.timeUnit().temporalUnit());
        observations.put(currentObservationPeriod, observed);
    }

    @Override
    public void onError(Throwable throwable) {
        logger.error("Abnormal termination of {}", this.toString());
    }

    @Override
    public void onComplete() {
        logger.info("Time Series stream, {}, completed succesfully.", this.toString());
    }

    @Override
    public void subscribe(Flow.Subscriber<? super T> subscriber) {
        subscribe(FlowAdapters.toSubscriber(subscriber));
    }

    void subscribe(Subscriber<? super T> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    // Must be copied if made public.
    SortedLinkedHashMap<OffsetDateTime, T> getObservations() {
        return this.observations;
    }

    public static <T extends Number> StreamingSeriesBuilder<T> getStreamingSeriesBuilder(Flow.Publisher<T> publisher) {
        return new StreamingSeriesBuilder<>(FlowAdapters.toPublisher(publisher));
    }

    //TODO: Decide whether to expose reactive-streams interface publicly.
    static <T extends Number> StreamingSeriesBuilder<T> getStreamingSeriesBuilder(Publisher<T> publisher) {
        return new StreamingSeriesBuilder<>(publisher);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.name;
    }

    public static class StreamingSeriesBuilder<T extends Number> {

        private String name = "Unnamed";
        private int memory = 10000;
        private Publisher<T> publisher;
        private OffsetDateTime startPeriod = OffsetDateTime.now();
        private TimePeriod samplingInterval = TimePeriod.oneMonth();

        private StreamingSeriesBuilder(Publisher<T> publisher) {
            this.publisher = publisher;
        }

        public StreamingSeries<T> build() {
            return new StreamingSeries<>(this);
        }
    }
}
