package optim;

import linear.doubles.Matrices;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import optim.doubles.QuasiNewtonLineFunction;
import optim.doubles.WolfePowellLineSearch;

public final class BFGS {
  
  private static final double c1 = 1E-4;
  private static final double c2 = 0.9;
  
  private final AbstractMultivariateFunction f;
  private Vector iterate;
  private Vector nextIterate;
  private Vector gradient;
  private Vector nextGradient;
  private Vector searchDirection;
  private double stepSize = 0.0;
  private double rho = 0.0;
  private Vector s;
  private Vector y;
  private Matrix H;
  private final Matrix identity;
  
  // Note that the objects we use are immutable and hence no copy operations are needed.
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double tol, final Matrix initialHessian) {
    this.f = f;
    this.identity = Matrices.identity(startingPoint.size());
    this.H = initialHessian;
    this.iterate = startingPoint;
    int k = 0;
    double functionValue = f.at(startingPoint);
    gradient = f.gradientAt(startingPoint);
    while (gradient.norm() > tol) {
      searchDirection = (H.times(gradient).scaledBy(-1.0));
      stepSize = updateStepSize(k, functionValue);
      nextIterate = iterate.plus(searchDirection.scaledBy(stepSize));
      s = nextIterate.minus(iterate);
      functionValue = f.at(nextIterate);
      nextGradient = f.gradientAt(nextIterate);
      y = nextGradient.minus(gradient);
      rho = 1 / y.dotProduct(s);
      H = updateHessian(k);
      iterate = nextIterate;
      gradient = nextGradient;
      k += 1;
    }
  }
  
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double tol) {
    this(f, startingPoint, tol, Matrices.identity(startingPoint.size()));
  }
  
  private final double updateStepSize(final int k, final double functionValue) {
    final double slope0 = gradient.dotProduct(searchDirection);
    final QuasiNewtonLineFunction lineFunction = new QuasiNewtonLineFunction(this.f, iterate, searchDirection);
    final double alpha1 = lineFunction.at(1.0);
    if (alpha1 <= functionValue + c1 * slope0 && Math.abs(alpha1) <= c2 * Math.abs(slope0)) {
      return 1.0;
    }
    WolfePowellLineSearch lineSearch = WolfePowellLineSearch.newBuilder(lineFunction, functionValue, slope0)
            .c1(c1).c2(c2).build();
    return lineSearch.search();
  }
  
  private final Matrix updateHessian(final int k) {
    Matrix piece1 = identity.minus(s.outerProduct(y).scaledBy(rho));
    Matrix piece2 = identity.minus(y.outerProduct(s).scaledBy(rho));
    Matrix piece3 = s.outerProduct(s).scaledBy(rho);
    return piece1.times(H).times(piece2).plus(piece3);
  }

}
