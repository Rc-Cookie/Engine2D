package com.github.rccookie.engine2d.impl;

import java.util.Map;

import com.github.rccookie.engine2d.online.HTTPRequest;
import com.github.rccookie.util.Future;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Generic definition of an online manager.
 */
public interface OnlineManager {

    /**
     * Sends the specified http response <b>asynchronous</b> and returns a future to
     * the result.
     *
     * @param url The url
     * @param method The request method
     * @param header Header fields
     * @param data Data to transmit, may be null when no data should be transmitted
     * @return A future referring to the result of the http request
     */
    Future<HTTPResponseData> sendHTTPRequest(@NotNull String url, @NotNull HTTPRequest.Method method, @NotNull Map<String,String> header, @Nullable String data);
}
