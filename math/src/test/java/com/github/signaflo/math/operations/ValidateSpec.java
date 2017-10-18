package com.github.signaflo.math.operations;

import com.github.signaflo.math.linear.doubles.Vector;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * [Insert class description]
 *
 * @author Jacob Rachiele
 * Jul. 29, 2017
 */
public class ValidateSpec {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void whenNullObjectPassedWithNonNullObjectToArgumentsNotNullThenCorrectNPE() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("null-valued Vector given as an argument.");
        Validate.argumentsNotNull(Vector.class, Vector.from(3.0), null);
    }

    @Test
    public void whenSingleNullObjectPassedToArgumentsNotNullThenCorrectNPE() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("null-valued Vector given as an argument.");
        Object[] args = null;
        Validate.argumentsNotNull(Vector.class, args);
    }
}
