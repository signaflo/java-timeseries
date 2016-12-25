/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package optim;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import linear.doubles.Matrices;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;

/**
 * An implementation of the Broyden-Fletcher-Goldfarb-Shanno (BFGS) algorithm for unconstrained
 * nonlinear optimization.
 *
 * @author Jacob Rachiele
 */
public final class BFGS {

  private static final double c1 = 1E-4;
  private static final double c2 = 0.9;

  private final AbstractMultivariateFunction f;
  private Vector iterate;
  private Vector gradient;
  private Vector searchDirection;
  private double functionValue = 0.0;
  private double rho = 0.0;
  private Vector s;
  private Vector y;
  private Matrix H;
  private final Matrix identity;

  /**
   * Create a new BFGS object with the given information. The identity matrix will be used for
   * initial inverse Hessian approximation.
   *
   * @param f                       the function to be minimized.
   * @param startingPoint           the initial guess of the minimum.
   * @param gradientTolerance       the tolerance for the norm of the gradient of the function.
   * @param functionChangeTolerance the tolerance for the change in function value.
   */
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint, final double gradientTolerance,
              final double functionChangeTolerance) {
    this(f, startingPoint, gradientTolerance, functionChangeTolerance, Matrices.identity(startingPoint.size()));
  }

  /**
   * Create a new BFGS object with the given information.
   *
   * @param f                      the function to be minimized.
   * @param startingPoint          the initial guess of the minimum.
   * @param gradientTolerance      the tolerance for the norm of the gradient of the function.
   * @param relativeErrorTolerance the tolerance for the change in function value.
   * @param initialHessian         The initial guess for the inverse Hessian approximation.
   */
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint, final double gradientTolerance,
              final double relativeErrorTolerance, final Matrix initialHessian) {
    this.f = f;
    this.identity = Matrices.identity(startingPoint.size());
    this.H = initialHessian;
    this.iterate = startingPoint;
    int k = 0;
    double priorFunctionValue;
    functionValue = f.at(startingPoint);
    double relativeChange;
    double relativeChangeDenominator;
    gradient = f.gradientAt(startingPoint, functionValue);
    int maxIterations = 100;
    double stepSize;
    double yDotS;
    boolean stop = false;
    if (gradient.size() > 0) {
      while (gradient.norm() > gradientTolerance && !stop
          && k < maxIterations) {
        searchDirection = (H.times(gradient).scaledBy(-1.0));
        try {
          stepSize = updateStepSize(functionValue);
        } catch (NaNStepLengthException | ViolatedTheoremAssumptionsException e) {
          stop = true;
          continue;
        }
        Vector nextIterate = iterate.plus(searchDirection.scaledBy(stepSize));
        s = nextIterate.minus(iterate);
        priorFunctionValue = functionValue;
        functionValue = f.at(nextIterate);
        relativeChangeDenominator = max(abs(priorFunctionValue), abs(nextIterate.norm()));
        //Hamming, Numerical Methods, 2nd edition, pg. 22
        relativeChange = Math.abs((priorFunctionValue - functionValue) / relativeChangeDenominator);
        if (relativeChange <= relativeErrorTolerance) {
          stop = true;
        }
        Vector nextGradient = f.gradientAt(nextIterate, functionValue);
        y = nextGradient.minus(gradient);
        yDotS = y.dotProduct(s);
        if (yDotS > 0) {
          rho = 1 / yDotS;
          H = updateHessian();
          iterate = nextIterate;
          gradient = nextGradient;
        } else {
          stop = true;
        }
        k += 1;
      }
    }
  }

  private double updateStepSize(final double functionValue) {
    final double slope0 = gradient.dotProduct(searchDirection);
    if (slope0 > 0) {
      System.out.println("The slope at step size 0 is positive");
    }
    final QuasiNewtonLineFunction lineFunction = new QuasiNewtonLineFunction(this.f, iterate, searchDirection);
    StrongWolfeLineSearch lineSearch = StrongWolfeLineSearch.newBuilder(lineFunction, functionValue, slope0).c1(c1)
        .c2(c2).alphaMax(50).alpha0(1.0).build();
    return lineSearch.search();
  }

  private Matrix updateHessian() {
    Matrix a = identity.minus(s.outerProduct(y).scaledBy(rho));
    Matrix b = identity.minus(y.outerProduct(s).scaledBy(rho));
    Matrix c = s.outerProduct(s).scaledBy(rho);
    return a.times(H).times(b).plus(c);
  }

  /**
   * Return the final value of the target function.
   *
   * @return the final value of the target function.
   */
  public final double functionValue() {
    return this.functionValue;
  }

  /**
   * Return the final, optimized input parameters.
   *
   * @return the final, optimized input parameters.
   */
  public final Vector parameters() {
    return this.iterate;
  }

  /**
   * Return the final approximation to the inverse Hessian.
   *
   * @return the final approximation to the inverse Hessian.
   */
  public final Matrix inverseHessian() {
    return this.H;
  }

}
