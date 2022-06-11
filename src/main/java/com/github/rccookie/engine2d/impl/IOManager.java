package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.util.Future;

import org.jetbrains.annotations.NotNull;

public interface IOManager {

    /**
     * Reads the contents of the specified file into a string and returns it.
     * The file path is always relative to the 'resources' directory (not the maven
     * one, inside that one).
     *
     * @param file The path to the file
     * @return The contents of the file
     * @throws RuntimeIOException If an I/O exception occurs
     */
    String read(String file) throws RuntimeIOException;

    /**
     * Returns a list of all files (recursively) inside the resources
     * directory, with its relative path to that folder.
     *
     * @return All files in the resources directory
     */
    String @NotNull[] listFiles();

    /**
     * Reads the clipboard contents. Returns an empty string if clipboard
     * is not accessible.
     *
     * @return A future to the clipboard content
     */
    Future<String> getClipboard();

    /**
     * Sets the clipboard contents. This operation may be asynchronous, so
     * the clipboard may not be set immediately after the return of this call.
     *
     * @param content The content to set
     */
    void setClipboard(@NotNull String content);
}
