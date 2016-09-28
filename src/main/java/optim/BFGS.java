package optim;

import linear.doubles.Matrices;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import optim.doubles.QuasiNewtonLineFunction;
import optim.doubles.WolfePowellLineSearch;

public final class BFGS {
  
  private static final int size = 101;
  
  private static final double c1 = 1E-4;
  private static final double c2 = 0.9;
  
  private final AbstractMultivariateFunction f;
  private Vector[] iterate = new Vector[size];
  private Vector[] gradient = new Vector[size];
  private Vector[] searchDirection = new Vector[size];
  private double[] stepSize = new double[size];
  private final double tol;
  private double[] rho = new double[size];
  private Vector[] s = new Vector[size];
  private Vector[] y = new Vector[size];
  private Matrix[] B = new Matrix[size];
  private Matrix[] H = new Matrix[size];
  private final Matrix identity;
  
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double tol, final Matrix initialHessian) {
    this.f = f;
    this.identity = Matrices.identity(startingPoint.size());
    this.tol = tol;
    this.H[0] = initialHessian;
    this.iterate[0] = startingPoint;
    int k = 0;
    double functionValue = f.at(startingPoint);
    gradient[k] = f.gradientAt(startingPoint);
    while (gradient[k].norm() > tol) {
      searchDirection[k] = (H[k].times(gradient[k]).scaledBy(-1.0));
      stepSize[k] = updateStepSize(k, functionValue);
      iterate[k + 1] = iterate[k].plus(searchDirection[k].scaledBy(stepSize[k]));
      s[k] = iterate[k + 1].minus(iterate[k]);
      functionValue = f.at(iterate[k + 1]);
      gradient[k + 1] = f.gradientAt(iterate[k + 1]);
      y[k] = gradient[k + 1].minus(gradient[k]);
      rho[k] = 1 / y[k].dotProduct(s[k]);
      H[k + 1] = updateHessian(k);
      k += 1;
    }
  }
  
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double tol) {
    this(f, startingPoint, tol, Matrices.identity(startingPoint.size()));
  }
  
  private final double updateStepSize(final int k, final double functionValue) {
    final double slope0 = gradient[k].dotProduct(searchDirection[k]);
    final QuasiNewtonLineFunction lineFunction = new QuasiNewtonLineFunction(this.f, iterate[k], searchDirection[k]);
    final double alpha1 = lineFunction.at(1.0);
    if (alpha1 <= functionValue + c1 * slope0 && Math.abs(alpha1) <= c2 * Math.abs(slope0)) {
      return 1.0;
    }
    WolfePowellLineSearch lineSearch = new WolfePowellLineSearch(lineFunction, c1, c2, 
            functionValue, slope0);
    return lineSearch.search();
  }
  
  private final Matrix updateHessian(final int k) {
    Matrix piece1 = identity.minus(s[k].outerProduct(y[k]).scaledBy(rho[k]));
    Matrix piece2 = identity.minus(y[k].outerProduct(s[k]).scaledBy(rho[k]));
    Matrix piece3 = s[k].outerProduct(s[k]).scaledBy(rho[k]);
    return piece1.times(H[k]).times(piece2).plus(piece3);
  }

}
