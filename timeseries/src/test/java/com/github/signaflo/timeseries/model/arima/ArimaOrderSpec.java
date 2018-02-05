package com.github.signaflo.timeseries.model.arima;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ArimaOrderSpec {

    @Test
    public void whenDifferencingGreaterThanOneThenConstantIsIgnored() {
        ArimaOrder order  = ArimaOrder.order(0, 2, 0, Arima.Constant.INCLUDE);
        assertThat(order.constant(), is(Arima.Constant.EXCLUDE));
        assertThat(order.drift(), is(Arima.Drift.EXCLUDE));
    }

    @Test
    public void whenDifferencingGreaterThanOneAndDriftIncludedThenDriftOverriddenToExclude() {
        ArimaOrder order = ArimaOrder.order(0, 1, 0, 0, 1, 0, Arima.Drift.INCLUDE);
        assertThat(order.drift(), is(Arima.Drift.EXCLUDE));
        order = ArimaOrder.order(0, 2, 0, Arima.Drift.INCLUDE);
        assertThat(order.drift(), is(Arima.Drift.EXCLUDE));
    }

    @Test
    public void whenDifferencingEqualToZeroAndConstantThenConstantIsDriftTerm() {
        ArimaOrder order = ArimaOrder.order(0, 1, 0, Arima.Constant.INCLUDE);
        assertThat(order.constant(), is(Arima.Constant.EXCLUDE));
        assertThat(order.drift(), is(Arima.Drift.INCLUDE));
    }

    @Rule public ExpectedException exception = ExpectedException.none();
    @Test
    public void whenDGreaterThan0WithConstantAndDriftThenIllegalArgument() {
        exception.expect(IllegalArgumentException.class);
        ArimaOrder.order(0, 1, 0, Arima.Constant.INCLUDE, Arima.Drift.INCLUDE);
    }

    @Test
    public void testArimaOrderEqualsAndHashCode() {
        ArimaOrder order1 = ArimaOrder.order(0, 1, 0);
        ArimaOrder order2 = ArimaOrder.order(1, 0, 1);
        ArimaOrder order3 = ArimaOrder.order(0, 1, 0);
        ArimaOrder nullOrder = null;
        String aNonModelOrder = "";
        assertThat(order1, is(order1));
        assertThat(order1, is(order3));
        assertThat(order1.hashCode(), is(order3.hashCode()));
        assertThat(order1, is(not(order2)));
        assertThat(order1, is(not(nullOrder)));
        assertThat(order1, is(not(aNonModelOrder)));
    }

    @Test
    public void testToString() {
        String expected = "Seasonal ARIMA (1, 1, 0) x (1, 0, 0) with no constant and a drift term";
        ArimaOrder order = ArimaOrder.order(1, 1, 0, 1, 0, 0, Arima.Drift.INCLUDE);
        assertThat(order.toString(), is(expected));
    }
}
