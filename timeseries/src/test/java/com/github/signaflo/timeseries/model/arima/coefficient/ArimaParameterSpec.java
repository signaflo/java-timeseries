package com.github.signaflo.timeseries.model.arima.coefficient;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.signaflo.timeseries.model.Parameter;
import org.junit.Test;

public class ArimaParameterSpec {

  private Parameter parameter;

  @Test
  public void whenParameterConstructedWithoutUncertaintyThenUncertaintyIsInfinity() {
    parameter = new ArimaParameter(5.0);
    assertThat(parameter.getUncertainty(), is(Double.POSITIVE_INFINITY));
  }

  @Test
  public void whenParameterConstructedWithoutValueThenValueIsZero() {
    parameter = new ArimaParameter();
    assertThat(parameter.getValue(), is(0.0));
  }

}
