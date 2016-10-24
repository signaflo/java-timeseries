/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */

package data;

import java.lang.annotation.Documented;

/**
 * Denotes an operation such that, for each element of some list or array of data,
 * the operation is applied to the corresponding element of some other list or array of data.
 * By <i>corresponding element</i> we typically mean the matching index of the lists or arrays.
 *
 * @author Jacob Rachiele
 */
@Documented
public @interface ElementWise {
}
