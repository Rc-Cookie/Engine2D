package com.github.rccookie.engine2d.impl;

import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an HTTP response.
 */
public final class HTTPResponseData {

    /**
     * The response code.
     */
    public final int code;
    /**
     * The response string.
     */
    @NotNull
    public final String data;
    /**
     * The response headers.
     */
    @Nullable
    public final Map<String,String> header;

    /**
     * Creates a new http response data.
     *
     * @param code The response code
     * @param data The response string
     * @param header The response headers
     */
    public HTTPResponseData(int code, @Nullable String data, @Nullable Map<String,String> header) {
        this.code = code;
        this.data = data == null ? "" : data;
        this.header = header;
    }
}
