package com.github.signaflo.math;

import lombok.NonNull;

/**
 * Represents a missing value. Note that if any method of this class is called whose return type does not
 * correspond to the type specified by {@link T}, then an {@link java.lang.UnsupportedOperationException}
 * will be thrown.
 *
 * @author Jacob Rachiele
 * Nov. 19, 2017
 */
public final class NA<T extends Number> extends Number {

    private static final String unsupportedString = "The underlying representation of this NA value " +
                                                    "is of type %s, making a call to %s inappropriate.";
    /**
     * <p>Represent a missing double value.</p>
     *
     * <p>
     * <b>Implementation Note:</b>
     * This construtor uses {@link java.lang.Double#NaN} as the underlying representation.
     * </p>
     * @return a new missing double value.
     */
    public static NA<Double> missingDouble() {
        return new NA<>(Double.NaN);
    }

    private final T representation;

    /**
     * Create a new missing value with the given representation.
     *
     * @param representation the number to use to represent missing values
     */
    NA(@NonNull T representation) {
        this.representation = representation;
    }

    @Override
    public byte byteValue() {
        throw new UnsupportedOperationException(String.format(unsupportedString,
                                                              representation.getClass().getSimpleName(),
                                                              "byteValue()")
        );
    }

    @Override
    public short shortValue() {
        throw new UnsupportedOperationException(String.format(unsupportedString,
                                                              representation.getClass().getSimpleName(),
                                                              "shortValue()")
        );
    }

    @Override
    public int intValue() {
        if (representation.getClass() != Integer.class) {
            throw new UnsupportedOperationException(String.format(unsupportedString,
                                                                  representation.getClass().getSimpleName(),
                                                                  "intValue()")
            );
        }
        return representation.intValue();
    }

    @Override
    public long longValue() {
        if (representation.getClass() != Long.class) {
            throw new UnsupportedOperationException(String.format(unsupportedString,
                                                                  representation.getClass().getSimpleName(),
                                                                  "longValue()")
            );
        }
        return representation.longValue();
    }

    @Override
    public float floatValue() {
        if (representation.getClass() != Float.class) {
            throw new UnsupportedOperationException(String.format(unsupportedString,
                                                                  representation.getClass().getSimpleName(),
                                                                  "floatValue()")
            );
        }
        return representation.floatValue();
    }

    @Override
    public double doubleValue() {
        if (representation.getClass() != Double.class) {
            throw new UnsupportedOperationException(String.format(unsupportedString,
                                                                  representation.getClass().getSimpleName(),
                                                                  "doubleValue()")
            );
        }
        return representation.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NA<?> na = (NA<?>) o;

        return representation.equals(na.representation);
    }

    @Override
    public int hashCode() {
        return representation.hashCode();
    }

    @Override
    public String toString() {
        return "NA";
    }
}


