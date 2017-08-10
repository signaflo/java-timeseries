package timeseries.model.arima;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class ArimaOrderSpec {

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
}
