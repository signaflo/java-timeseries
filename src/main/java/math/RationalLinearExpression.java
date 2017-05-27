/*
 * Copyright (c) 2017 Jacob Rachiele
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *
 * Jacob Rachiele
 */

package math;

import com.google.common.collect.ImmutableList;

import java.util.*;

public class RationalLinearExpression implements LinearExpression<Rational> {

    private final List<LinearTerm<Rational>> variableTerms;

    private RationalLinearExpression(Collection<LinearTerm<Rational>> variableTerms) {
        this.variableTerms = ImmutableList.copyOf(variableTerms);
    }

    RationalLinearExpression(Rational... coefficients) {
        ImmutableList.Builder<LinearTerm<Rational>> terms = ImmutableList.builder();
        for (int i = 0; i < coefficients.length; i++) {
            LinearTerm<Rational> term = new LinearTerm<>(i, coefficients[i]);
            terms.add(term);
        }
        this.variableTerms = terms.build();
    }

    @Override
    public LinearExpression<Rational> times(Rational scalar) {
        ImmutableList.Builder<LinearTerm<Rational>> linearTermBuilder = ImmutableList.builder();
        for (LinearTerm<Rational> term : this.variableTerms) {
            linearTermBuilder.add(term.times(scalar));
        }
        return new RationalLinearExpression(linearTermBuilder.build());
    }

    @Override
    public LinearExpression<Rational> plus(LinearExpression<Rational> otherExpression) {
        if (this.numberOfTerms() != otherExpression.numberOfTerms()) {
            throw new IllegalArgumentException("The two expressions must have corresponding terms.");
        }
        ImmutableList.Builder<LinearTerm<Rational>> linearTermBuilder = ImmutableList.builder();
        for (int i = 0; i < this.numberOfTerms(); i++) {
            if (this.getTerm(i).getVariable() != otherExpression.getTerm(i).getVariable()) {
                throw new IllegalArgumentException("The two expressions must have corresponding terms.");
            }
            linearTermBuilder.add(this.getTerm(i).plus(otherExpression.getTerm(i)));
        }
        return new RationalLinearExpression(linearTermBuilder.build());
    }

    @Override
    public int numberOfTerms() {
        return this.variableTerms.size();
    }

    @Override
    public LinearTerm<Rational> getTerm(int i) {
        return this.variableTerms.get(i);
    }

    @Override
    public String toString() {
        LinearTerm<Rational> term;
        StringBuilder s = new StringBuilder();
        if (variableTerms.size() > 0) {
            term = variableTerms.get(0);
            s.append(term.toString());
        }
        for (int i = 1; i < variableTerms.size(); i++) {
            term = variableTerms.get(i);
            if (term.getCoefficient().compareTo(Rational.zero()) >= 0) {
                s.append(" + ");
            } else {
                s.append(" - ");
            }
            s.append(term.getCoefficient().printAbs()).append("x").append(term.getVariable());
        }
        return s.toString();
    }
}
