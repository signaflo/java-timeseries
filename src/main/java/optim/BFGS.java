/*
 * Copyright (c) 2016 Jacob Rachiele
 * 
 */
package optim;

import linear.doubles.Matrices;
import linear.doubles.Matrix;
import linear.doubles.Vector;
import math.function.AbstractMultivariateFunction;

import java.util.Arrays;

import static java.lang.Math.abs;
import static java.lang.Math.max;

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
  private final Matrix identity;
  private Vector iterate;
  private Vector gradient;
  private Vector searchDirection;
  private double functionValue = 0.0;
  private double rho = 0.0;
  private Vector s;
  private Vector y;
  private Matrix H;

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
   * @param gradientNormTolerance      the tolerance for the norm of the gradient of the function.
   * @param relativeChangeTolerance the tolerance for the change in function value.
   * @param initialHessian         The initial guess for the inverse Hessian approximation.
   */
  public BFGS(final AbstractMultivariateFunction f, final Vector startingPoint, final double gradientNormTolerance,
              final double relativeChangeTolerance, final Matrix initialHessian) {
    this.f = f;
    this.identity = Matrices.identity(startingPoint.size());
    this.H = initialHessian;
    this.iterate = startingPoint;
    int k = 0;
    double priorFunctionValue;
    functionValue = f.at(startingPoint);
    gradient = f.gradientAt(startingPoint, functionValue);
    int maxIterations = 100;
    if (gradient.size() > 0) {
      double relativeChange;
      double relativeChangeDenominator;
      double stepSize;
      double slopeAt0;
      double yDotS;
      Vector nextIterate;
      Vector nextGradient;
      Vector scaledSearchDirection;
      boolean stop = false;
      int iterationsSinceIdentity = 0;

      while (!stop) {
        if (iterationsSinceIdentity > 2 * iterate.size()) {
          H = identity;
          iterationsSinceIdentity = 0;
        }
        iterationsSinceIdentity++;
        searchDirection = (H.times(gradient).scaledBy(-1.0));
        slopeAt0 = searchDirection.dotProduct(gradient);
        if (slopeAt0 > 0) {
          H = this.identity;
          searchDirection = (H.times(gradient).scaledBy(-1.0));
          slopeAt0 = searchDirection.dotProduct(gradient);
        }
//        try {
//          stepSize = updateStepSize(functionValue);
//        } catch (NaNStepLengthException | ViolatedTheoremAssumptionsException e) {
//          stop = true;
//          continue;
//        }
        stepSize = 1.0;
        s = searchDirection.scaledBy(stepSize);
        nextIterate = iterate.plus(s);
        priorFunctionValue = functionValue;
        functionValue = f.at(nextIterate);
        while (!(Double.isFinite(functionValue) && functionValue < priorFunctionValue + c1 * stepSize * slopeAt0) &&
            !stop) {
          relativeChangeDenominator = max(abs(priorFunctionValue), abs(nextIterate.norm()));
          relativeChange = Math.abs((priorFunctionValue - functionValue) / relativeChangeDenominator);
          if (relativeChange <= relativeChangeTolerance) {
            stop = true;
          } else {
            stepSize *= 0.2;
            s = searchDirection.scaledBy(stepSize);
            nextIterate = iterate.plus(s);
            functionValue = f.at(nextIterate);
          }
        }
        nextGradient = f.gradientAt(nextIterate, functionValue);
        if (!stop) {
          relativeChangeDenominator = max(abs(priorFunctionValue), abs(nextIterate.norm()));
          //Hamming, Numerical Methods, 2nd edition, pg. 22
          relativeChange = Math.abs((priorFunctionValue - functionValue) / relativeChangeDenominator);
          if (relativeChange <= relativeChangeTolerance || nextGradient.norm() < gradientNormTolerance) {
            stop = true;
          }
        }
        y = nextGradient.minus(gradient);
        yDotS = y.dotProduct(s);
        if (yDotS > 0) {
          rho = 1 / yDotS;
          H = updateHessian();
        } else if (!stop) {
          H = identity;
          iterationsSinceIdentity = 0;
        }
        iterate = nextIterate;
        gradient = nextGradient;
        k += 1;
        if (k > maxIterations) {
          stop = true;
        }
      }
    }
  }

  static double[][] Lmatrix(final int n) {
    int i;
    double[][] m;
    m = new double[n][];
    for (i = 0; i < n; i++) {
      m[i] = new double[i + 1];
    }
    return m;
  }

  private double updateStepSize(double functionValue) {
    int maxAttempts = 10;
    final double slope0 = gradient.dotProduct(searchDirection);
    if (slope0 > 0) {
      System.out.println("The slope at step size 0 is positive");
    }
    double stepSize = 1.0;
    Vector nextIterate = iterate.plus(searchDirection.scaledBy(stepSize));
    s = nextIterate.minus(iterate);
    double priorFunctionValue = functionValue;
    functionValue = f.at(nextIterate);
    int k = 1;
    while (!(Double.isFinite(functionValue) && functionValue < priorFunctionValue + c1 * stepSize * slope0) && k < maxAttempts) {
      stepSize *= 0.2;
      nextIterate = iterate.plus(searchDirection.scaledBy(stepSize));
      s = nextIterate.minus(iterate);
      functionValue = f.at(nextIterate);
      k++;
    }
    return stepSize;
//    final QuasiNewtonLineFunction lineFunction = new QuasiNewtonLineFunction(this.f, iterate, searchDirection);
//    StrongWolfeLineSearch lineSearch = StrongWolfeLineSearch.newBuilder(lineFunction, functionValue, slope0).c1(c1)
//        .c2(c2).alphaMax(50).alpha0(1.0).build();
//    return lineSearch.search();
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

  public static void vmmin(final AbstractMultivariateFunction fminfn, final Vector startingPoint, final double
      gradientTolerance,
                           final double reltol, final double meanScale) {

    final int maxit = 100;
    final double abstol = Double.NEGATIVE_INFINITY;
    final double reltest = 10.0;
    final double stepredn = 0.2;
    final double acctol = 0.0001;
    final int n0 = startingPoint.size();
    double[] b = startingPoint.elements();


    boolean accpoint, enough;
    double[] g, t, X, c;
    int count, funcount, gradcount;
    double f, gradproj;
    int i, j, ilast, iter = 0;
    double s, steplength;
    double D1, D2;

    int[] l = new int[n0];
    int n = 0;
    for (i = 0; i < n0; i++) l[n++] = i;
    t = new double[n];
    X = new double[n];
    c = new double[n];
    double[][] B = Lmatrix(n);
    f = fminfn.at(Vector.from(b));
    double fmin = f;
    funcount = gradcount = 1;
    g = fminfn.gradientAt(Vector.from(b)).elements();
    iter++;
    ilast = gradcount;

    do {
      if (ilast == gradcount) {
        for (i = 0; i < n; i++) {
          for (j = 0; j < i; j++) B[i][j] = 0.0;
          if (i == n - 1) {
            B[i][i] = 1.0;
          } else {
            B[i][i] = 1.0;
          }
        }
      }
      for (i = 0; i < n; i++) {
        X[i] = b[l[i]];
        c[i] = g[l[i]];
      }
      gradproj = 0.0;
      for (i = 0; i < n; i++) {
        s = 0.0;
        for (j = 0; j <= i; j++) s -= B[i][j] * g[l[j]];
        for (j = i + 1; j < n; j++) s -= B[j][i] * g[l[j]];
        t[i] = s;
        gradproj += s * g[l[i]];
      }

      if (gradproj < 0.0) {	/* search direction is downhill */
        steplength = 1.0;
        accpoint = false;
        do {
          count = 0;
          for (i = 0; i < n; i++) {
            b[l[i]] = X[i] + steplength * t[i];
            if (reltest + X[i] == reltest + b[l[i]]) /* no change */
              count++;
          }
          if (count < n) {
            f = fminfn.at(Vector.from(b));
            funcount++;
            accpoint = Double.isFinite(f) &&
                (f <= fmin + gradproj * steplength * acctol);
            if (!accpoint) {
              steplength *= stepredn;
            }
          }
        } while (!(count == n || accpoint));
        enough = (f > abstol) &&
            Math.abs(f - fmin) > reltol * (abs(fmin) + reltol);
      /* stop if value if small or if relative change is low */
        if (!enough) {
          count = n;
          fmin = f;
        }
        if (count < n) {/* making progress */
          fmin = f;
          g = fminfn.gradientAt(Vector.from(b)).elements();
          gradcount++;
          iter++;
          D1 = 0.0;
          for (i = 0; i < n; i++) {
            t[i] = steplength * t[i];
            c[i] = g[l[i]] - c[i];
            D1 += t[i] * c[i];
          }
          if (D1 > 0) {
            D2 = 0.0;
            for (i = 0; i < n; i++) {
              s = 0.0;
              for (j = 0; j <= i; j++)
                s += B[i][j] * c[j];
              for (j = i + 1; j < n; j++)
                s += B[j][i] * c[j];
              X[i] = s;
              D2 += s * c[i];
            }
            D2 = 1.0 + D2 / D1;
            for (i = 0; i < n; i++) {
              for (j = 0; j <= i; j++)
                B[i][j] += (D2 * t[i] * t[j]
                    - X[i] * t[j] - t[i] * X[j]) / D1;
            }
          } else {	/* D1 < 0 */
            ilast = gradcount;
          }
        } else {	/* no progress */
          if (ilast < gradcount) {
            count = 0;
            ilast = gradcount;
          }
        }
      } else {		/* uphill search */
        count = 0;
        if (ilast == gradcount) count = n;
        else ilast = gradcount;
      /* Resets unless has just been reset */
      }
      if (iter >= maxit) break;
      if (gradcount - ilast > 2 * n)
        ilast = gradcount;	/* periodic restart */
    } while (count != n || ilast != gradcount);
    System.out.println(Arrays.toString(b));
    int fail = (iter < maxit) ? 0 : 1;
    int fncount = funcount;
    int grcount = gradcount;

  }

}
