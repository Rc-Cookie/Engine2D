package com.github.rccookie.engine2d.online.server;

import java.io.IOException;
import java.net.InetAddress;

import com.github.rccookie.engine2d.online.MessageType;
import com.github.rccookie.json.JsonDeserialization;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.json.JsonSerializable;

public class Message implements JsonSerializable {

    static {
        JsonDeserialization.register(Message.class, json -> new Message(json.get("content"), json.get("time").asLong(), MessageType.values()[json.get("type").asInt()], json.get("source").get()));
    }

    public final JsonElement content;
    public final long time;
    public final MessageType type;
    public final String source;

    private Message(JsonElement content, long time, MessageType type, String source) {
        this.content = content;
        this.time = time;
        this.type = type;
        this.source = source;
    }

    public Message(JsonElement content, MessageType type) {
        this(content, System.currentTimeMillis(), type, getIPAddress());
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

    @Override
    public Object toJson() {
        return new JsonObject(
                "content", content,
                "time", time,
                "type", type.ordinal(),
                "source", source
        );
    }

    private static String getIPAddress() {
        try { return InetAddress.getLocalHost().getHostAddress(); }
        catch(IOException e) { return "0.0.0.0"; }
    }

    static void init() { }
}
