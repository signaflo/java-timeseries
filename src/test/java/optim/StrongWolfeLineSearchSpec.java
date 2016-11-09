/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package optim;

import math.function.AbstractFunction;
import org.junit.Test;

public final class StrongWolfeLineSearchSpec {
  
  @Test
  public void testLineSearch() {
    AbstractFunction f = new TestFunction3();
    final double f0 = f.at(0);
    final double slope0 = f.slopeAt(0);
    final double c1 = 0.1;
    final double c2 = 0.1;
    StrongWolfeLineSearch lineSearch = StrongWolfeLineSearch.newBuilder(f, f0, slope0)
            .c1(c1).c2(c2).alpha0(1E-1).alphaMax(100).build();
    System.out.println(lineSearch.search());
    System.out.println(f.functionEvaluations());
    System.out.println(f.slopeEvaluations());
    System.out.println(lineSearch.m);
  }

}
