/*
 * Copyright (c) 2016 Jacob Rachiele
 *
 */
package data;

import java.lang.annotation.Documented;

/**
 * Denotes an operation that applies to an entire list or array of data simultaneously. This
 * can be contrasted with operations that require a for loop to act on an entire array since
 * they apply to only a single data element at a time.
 *
 * @author Jacob Rachiele
 */
@Documented
public @interface Vectorized {
}
