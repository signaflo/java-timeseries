package analysis;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

abstract class Function {
	
	final int nVariables;
	final DerivativeStructure[] variables;
	
	Function(final int nVariables, final int order) {
		this(nVariables, order, new double[nVariables]);
	}
	
	Function(final double[] initialValues, final int order) {
		this(initialValues.length, order, initialValues);
	}
	
	Function(final int nVariables, final int order, final double[] initialValues) {
		this.nVariables = nVariables;
		this.variables = new DerivativeStructure[nVariables];
		for (int i = 0; i < nVariables; i++) {
			variables[i] = new DerivativeStructure(nVariables, order, i, initialValues[i]);
		}
	}
	
}
