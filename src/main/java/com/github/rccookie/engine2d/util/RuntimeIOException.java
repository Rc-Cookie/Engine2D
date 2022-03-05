package com.github.rccookie.engine2d.util;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.jetbrains.annotations.Nullable;

/**
 * An unchecked exception indicating some I/O exception occurred.
 * {@link IOException} and {@link UncheckedIOException} may not be available
 * in all implementations.
 */
public class RuntimeIOException extends RuntimeException {

    /**
     * Creates a new runtime io exception.
     */
    public RuntimeIOException() { }

    /**
     * Creates a new runtime io exception with the given message.
     *
     * @param message The error message
     */
    public RuntimeIOException(@Nullable String message) {
        super(message);
    }

    /**
     * Creates a new runtime io exception with the given cause.
     *
     * @param cause The cause for the exception to be thrown
     */
    public RuntimeIOException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new runtime io exception with the given message and cause.
     *
     * @param message The error message
     * @param cause The cause for the exception to be thrown
     */
    public RuntimeIOException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }
}
