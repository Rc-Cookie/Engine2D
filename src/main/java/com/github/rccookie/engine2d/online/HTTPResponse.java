package com.github.rccookie.engine2d.online;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.rccookie.engine2d.impl.HTTPResponseData;
import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonElement;

import org.jetbrains.annotations.NotNull;

/**
 * A response from an HTTP request.
 */
public final class HTTPResponse {

    /**
     * The response code for the request. Anything &gt;= 400 means error.
     */
    public final int code;
    /**
     * The response data sent by the server.
     */
    @NotNull
    public final String data;
    /**
     * Header fields in the response.
     */
    @NotNull
    public final Map<String,String> header;
    /**
     * Whether the request was successful, which is true exactly when
     * {@link #code} &lt; 400.
     */
    public final boolean success;


    /**
     * Json generated from the data.
     */
    private JsonElement json = null;


    /**
     * Creates a new HTTP response from the given response data.
     *
     * @param responseData The response data to base this response on
     */
    HTTPResponse(@NotNull HTTPResponseData responseData) {
        this.code = responseData.code;
        this.data = responseData.data;
        //noinspection Java9CollectionFactory
        this.header = responseData.header != null ? Collections.unmodifiableMap(new HashMap<>(responseData.header)) : Collections.emptyMap();
        this.success = code < 400;
    }

    /**
     * Returns the response data parsed as json. Multiple calls to this method
     * will cache the result.
     *
     * @return The json element representing the json data.
     */
    public JsonElement json() {
        return json != null ? json : (json = Json.parse(data));
    }

    /**
     * Returns the response code and the response data.
     *
     * @return A string representation of this object
     */
    @Override
    public String toString() {
        return "Response code: " + code + "\n" + data;
    }
}
