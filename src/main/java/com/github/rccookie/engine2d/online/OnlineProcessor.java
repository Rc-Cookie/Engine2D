package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.util.annotations.Constant;
import com.github.rccookie.json.JsonElement;

import org.jetbrains.annotations.NotNull;

/**
 * A processor for received messages from the server.
 */
public interface OnlineProcessor {

    /**
     * Returns the name of the key that this online processor is listening to.
     *
     * @return The key of this online processor
     */
    @Constant
    String getKey();

    /**
     * Processes the specified received data.
     *
     * @param data The data received
     */
    void processData(@NotNull JsonElement data);
}
