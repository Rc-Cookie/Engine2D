package com.github.rccookie.engine2d.online;

import com.github.rccookie.json.JsonElement;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * Describes data received from the server.
 */
public class OnlineData {

    /**
     * The content of the message. The message is always formatted in json.
     */
    public final JsonElement json;
    /**
     * The delay since the creation of the request on a different machine.
     */
    public final float delay;
    /**
     * The type of the message that was received.
     */
    public final MessageType type;

    /**
     * Creates a new online data object.
     *
     * @param json The message content
     * @param delay The message delay
     * @param type The message type
     */
    public OnlineData(@NotNull JsonElement json, float delay, @NotNull MessageType type) {
        this.json = Arguments.checkNull(json, "json");
        this.delay = delay;
        this.type = Arguments.checkNull(type, "type");
    }

    @Override
    public String toString() {
        return "OnlineData{" +
                "json=" + json +
                ", delay=" + delay +
                ", type=" + type +
                '}';
    }
}
