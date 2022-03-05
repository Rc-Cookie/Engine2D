package com.github.rccookie.engine2d.util;

/**
 * A long to long function.
 */
@FunctionalInterface
public interface LongValidator {

    /**
     * Converts the given input into a valid output.
     *
     * @param l The input
     * @return The validated / corrected input
     */
    long validate(long l);
}
