package timeseries.models;

import timeseries.TimeSeries;

public final class Arima {
	
	private final TimeSeries observations;
	private final ModelOrder order;
	
	private Arima(final TimeSeries observations, final ModelOrder order) {
		this.observations = observations.copy();
		this.order = order.copy();
		double[] initialParameters = setInitialParameters();
	}
	
	private final double[] setInitialParameters() {
	  // Set initial constant to the mean and all other parameters to zero.
	  double[] initParams = new double[order.sumARMA() + order.constant];
	  if (order.constant == 1) {
	    initParams[initParams.length - 1] = observations.mean();
	  }
	  return initParams;
	}
	
	public static final class ModelOrder {
		private final int p;
		private final int d;
		private final int q;
		private final int P;
		private final int D;
		private final int Q;
		private final int constant;
		
		public ModelOrder(final int p, final int d, final int q, final int P, final int D, final int Q, 
				final boolean constant) {
			this.p = p;
			this.d = d;
			this.q = q;
			this.P = P;
			this.D = D;
			this.Q = Q;
			this.constant = (constant == true)? 1 : 0;
		}
		
		public final ModelOrder copy() {
			return new ModelOrder(this);
		}
		
		private final int sumARMA() {
		  return this.p + this.q + this.P + this.Q;
		}
		
		private ModelOrder(final ModelOrder original) {
			this.p = original.p;
			this.d = original.d;
			this.q = original.q;
			this.P = original.P;
			this.D = original.D;
			this.Q = original.Q;
			this.constant = original.constant;
		}
	}

}
