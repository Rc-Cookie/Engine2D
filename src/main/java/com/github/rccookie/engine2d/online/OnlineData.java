package com.github.rccookie.engine2d.online;

import com.github.rccookie.json.JsonElement;

public class OnlineData {

    public final JsonElement json;
    public final float delay;
    public final MessageType type;

    public OnlineData(JsonElement json, float delay, MessageType type) {
        this.json = json;
        this.delay = delay;
        this.type = type;
    }
}
