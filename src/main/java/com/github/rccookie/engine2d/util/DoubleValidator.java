package com.github.rccookie.engine2d.util;

/**
 * A double to double function.
 */
@FunctionalInterface
public interface DoubleValidator {

    /**
     * Converts the given input into a valid output.
     *
     * @param d The input
     * @return The validated / corrected input
     */
    double validate(double d);
}
