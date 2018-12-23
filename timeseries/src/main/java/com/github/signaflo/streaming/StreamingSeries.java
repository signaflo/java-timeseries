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
import io.reactivex.Flowable;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Flow;

/**
 * A stream of observations generated sequentially in time, where observations are made at a
 * fixed time interval.
 *
 */
public class StreamingSeries implements Flow.Processor<Observation, Observation> {

    private static final Logger logger = LoggerFactory.getLogger(StreamingSeries.class);

    private final String name;
    private final Flowable<Observation> publisher;
    private final TimePeriod samplingInterval;
    private Instant currentObservationPeriod;
    private final SortedMapping<Instant, Observation> observations;

    private StreamingSeries(StreamingSeriesBuilder seriesBuilder) {
        this.name = seriesBuilder.name;
        this.publisher = Flowable.fromPublisher(seriesBuilder.publisher);
        this.samplingInterval = seriesBuilder.samplingInterval;
        this.currentObservationPeriod = seriesBuilder.startPeriod;
        this.observations = new SortedMapping<>(seriesBuilder.memory);
        this.publisher.subscribe(FlowAdapters.toSubscriber(this));
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Observation observed) {
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
    public void subscribe(Flow.Subscriber<? super Observation> subscriber) {
        subscribe(FlowAdapters.toSubscriber(subscriber));
    }

    void subscribe(Subscriber<? super Observation> subscriber) {
        this.publisher.subscribe(subscriber);
    }

    // Must be copied if made public.
    SortedMapping<Instant, Observation> getObservations() {
        return this.observations;
    }

    public static StreamingSeriesBuilder getStreamingSeriesBuilder(Flow.Publisher<Observation> publisher) {
        return new StreamingSeriesBuilder(FlowAdapters.toPublisher(publisher));
    }

    //TODO: Decide whether to expose reactive-streams interface publicly.
    static StreamingSeriesBuilder getStreamingSeriesBuilder(Publisher<Observation> publisher) {
        return new StreamingSeriesBuilder(publisher);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + this.name;
    }

    public static class StreamingSeriesBuilder {

        private String name = "Unnamed";
        private int memory = 10000;
        private Publisher<Observation> publisher;
        private Instant startPeriod = Instant.now();
        private TimePeriod samplingInterval = TimePeriod.oneMonth();

        private StreamingSeriesBuilder(Publisher<Observation> publisher) {
            this.publisher = publisher;
        }

        public StreamingSeries build() {
            return new StreamingSeries(this);
        }
    }
}
