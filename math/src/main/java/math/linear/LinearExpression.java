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

package math.linear;

import com.google.common.collect.ImmutableList;
import math.FieldElement;

import java.util.Collection;
import java.util.List;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 *         Jun. 26, 2017
 */
public class LinearExpression<T extends FieldElement<T>> {

    private final List<LinearTerm<T>> variableTerms;
    private final Field<T> field;

    LinearExpression(Collection<LinearTerm<T>> variableTerms, Field<T> field) {
        this.variableTerms = ImmutableList.copyOf(variableTerms);
        this.field = field;
    }

    private int numberOfTerms() {
        return this.variableTerms.size();
    }

    private LinearTerm<T> getTerm(int i) {
        return this.variableTerms.get(i);
    }

    public LinearExpression<T> times(T scalar) {
        ImmutableList.Builder<LinearTerm<T>> linearTermBuilder = ImmutableList.builder();
        for (LinearTerm<T> term : this.variableTerms) {
            linearTermBuilder.add(term.times(scalar));
        }
        return new LinearExpression<>(linearTermBuilder.build(), this.field);
    }

    public LinearExpression<T> plus(LinearExpression<T> otherExpression) {
        if (this.numberOfTerms() != otherExpression.numberOfTerms()) {
            throw new IllegalArgumentException("The two expressions must have corresponding terms.");
        }
        ImmutableList.Builder<LinearTerm<T>> linearTermBuilder = ImmutableList.builder();
        for (int i = 0; i < this.numberOfTerms(); i++) {
            if (this.getTerm(i).getVariable() != otherExpression.getTerm(i).getVariable()) {
                throw new IllegalArgumentException("The two expressions must have corresponding terms.");
            }
            linearTermBuilder.add(this.getTerm(i).plus(otherExpression.getTerm(i)));
        }
        return new LinearExpression<>(linearTermBuilder.build(), this.field);
    }

    @Override
    public String toString() {
        LinearTerm<T> term;
        StringBuilder s = new StringBuilder();
        if (variableTerms.size() > 0) {
            term = variableTerms.get(0);
            s.append(term.toString());
        }
        for (int i = 1; i < variableTerms.size(); i++) {
            term = variableTerms.get(i);
            s.append(" + ");
            s.append(term.toString());
        }
        return s.toString();
    }
}
