package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.impl.IOManager;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.util.Future;

import org.jetbrains.annotations.NotNull;

public enum IO {
    ; // No instance

    private static final IOManager IO_MANAGER;
    static {
        IO_MANAGER = Application.getImplementation().getIOManager();
    }


    /**
     * Reads the contents of the specified file into a string and returns it.
     * The file path is always relative to the 'resources' directory (in case of maven,
     * not the maven 'resources' directory, but the 'resources/resources' one).
     *
     * @param file The path to the file
     * @return The contents of the file
     * @throws RuntimeIOException If an I/O exception occurs
     */
    @NotNull
    public static String readFile(String file) throws RuntimeIOException {
        return IO_MANAGER.read(file);
    }

    /**
     * Lists all files in the 'resources' directory recursively with their respective
     * relative path.
     *
     * @return All files in the resources directory
     * @throws RuntimeIOException If an I/O exception occurs
     */
    @NotNull
    public static String[] listFiles() throws RuntimeIOException {
        return IO_MANAGER.listFiles();
    }

    /**
     * Reads the clipboard contents. Returns an empty string if clipboard
     * is not accessible.
     *
     * @return A future to the clipboard content
     */
    public static Future<String> getClipboard() {
        return IO_MANAGER.getClipboard();
    }

    /**
     * Sets the clipboard contents. This operation may be asynchronous, so
     * the clipboard may not be set immediately after the return of this call.
     *
     * @param content The content to set
     */
    public static void setClipboard(String content) {
        IO_MANAGER.setClipboard(content);
    }
}
