package optim;

import linear.doubles.Matrices;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import optim.doubles.QuasiNewtonLineFunction;
import optim.doubles.StrongWolfeLineSearch;

public final class BFGS {
  
  private static final double c1 = 1E-4;
  private static final double c2 = 0.9;
  
  private final AbstractMultivariateFunction f;
  private Vector iterate;
  private Vector nextIterate;
  private Vector gradient;
  private Vector nextGradient;
  private Vector searchDirection;
  private double functionValue = 0.0;
  private double stepSize = 0.0;
  private double rho = 0.0;
  private Vector s;
  private Vector y;
  private Matrix H;
  private final Matrix identity;
  
  // Note that the objects we use are immutable and hence no copy operations are needed.
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double gradientTolerance, final double pointTolerance, final Matrix initialHessian) {
    this.f = f;
    this.identity = Matrices.identity(startingPoint.size());
    this.H = initialHessian;
    this.iterate = startingPoint;
    int k = 0;
    double priorFunctionValue = Double.POSITIVE_INFINITY;
    functionValue = f.at(startingPoint);
    double absoluteChange = Double.MAX_VALUE;
    double relativeChange = Double.MAX_VALUE;
    gradient = f.gradientAt(startingPoint, functionValue);
    while (gradient.norm() > gradientTolerance && absoluteChange > pointTolerance && relativeChange > pointTolerance) {
      searchDirection = (H.times(gradient).scaledBy(-1.0));
      stepSize = updateStepSize(k, functionValue);
      nextIterate = iterate.plus(searchDirection.scaledBy(stepSize));
      s = nextIterate.minus(iterate);
      priorFunctionValue = functionValue;
      functionValue = f.at(nextIterate);
      absoluteChange = Math.abs(priorFunctionValue - functionValue);
      relativeChange = Math.abs((priorFunctionValue - functionValue) / priorFunctionValue);
      nextGradient = f.gradientAt(nextIterate, functionValue);
      y = nextGradient.minus(gradient);
      rho = 1 / y.dotProduct(s);
      H = updateHessian(k);
      iterate = nextIterate;
      gradient = nextGradient;
      k += 1;
    }
  }
  
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint,
      final double gradientTolerance, double pointChangeTolerance) {
    this(f, startingPoint, gradientTolerance, pointChangeTolerance, Matrices.identity(startingPoint.size()));
  }
  
  private final double updateStepSize(final int k, final double functionValue) {
    final double slope0 = gradient.dotProduct(searchDirection);
    final QuasiNewtonLineFunction lineFunction = new QuasiNewtonLineFunction(this.f, iterate, searchDirection);
    StrongWolfeLineSearch lineSearch = StrongWolfeLineSearch.newBuilder(lineFunction, functionValue, slope0)
            .c1(c1).c2(c2).alphaMax(1000).alpha0(1.0).build();
    return lineSearch.search();
  }
  
  private final Matrix updateHessian(final int k) {
    Matrix a = identity.minus(s.outerProduct(y).scaledBy(rho));
    Matrix b = identity.minus(y.outerProduct(s).scaledBy(rho));
    Matrix c = s.outerProduct(s).scaledBy(rho);
    return a.times(H).times(b).plus(c);
  }
  
  public final double functionValue() {
    return this.functionValue;
  }
  
  public final Vector iterate() {
    return this.iterate;
  }
  
  public final Matrix inverseHessian() {
    return this.H;
  }

}
