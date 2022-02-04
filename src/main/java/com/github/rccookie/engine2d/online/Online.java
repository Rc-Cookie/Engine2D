package com.github.rccookie.engine2d.online;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import com.github.rccookie.json.JsonElement;
import com.github.rccookie.json.JsonObject;

public class Online {

    public static final int DEFAULT_PORT = 62200;

    static ClientConnection clientConnection = null;
    private static Server server = null;

    private Online() {
        throw new UnsupportedOperationException("Online does not allow instances");
    }

    public static void connect(String host) {
        connect(host, DEFAULT_PORT);
    }

    public synchronized static void connect(String host, int port) {
        if(clientConnection != null)
            throw new IllegalStateException("Already connected");
        clientConnection = new ClientConnection(host, port);
    }

    public void hostAndConnect() {
        hostAndConnect(DEFAULT_PORT);
    }

    public synchronized static void hostAndConnect(int port) {
        if(clientConnection != null)
            throw new IllegalStateException("Already connected");
        server = new Server(port);
        clientConnection = new ClientConnection("localhost", port);
    }

    public static synchronized void disconnect() {
        if(clientConnection == null) return;
        clientConnection.close();
        clientConnection = null;
    }

    public static boolean isConnected() {
        return clientConnection != null;
    }

    public static void registerProcessor(MessageType type, Consumer<OnlineData> processor) {
        checkConnected();
        clientConnection.registerProcessor(type, processor);
    }

    public static void registerProcessor(String key, Consumer<OnlineData> processor) {
        checkConnected();
        clientConnection.registerProcessor(key, processor);
    }

    public static void submitData(String key, Object jsonValue) {
        checkConnected();
        clientConnection.submit(key, jsonValue);
    }

    public static <T> void submitData(String key, T jsonValue, BinaryOperator<T> combiner) {
        checkConnected();
        clientConnection.submit(key, jsonValue, combiner);
    }



    static JsonElement createMessage(Object jsonContent, MessageType type) {
        JsonObject data = new JsonObject();
        data.put("content", jsonContent);
        data.put("type", type.ordinal());
        data.put("time", System.currentTimeMillis());
        return data.asElement();
    }

    private static void checkConnected() throws IllegalStateException {
        if(clientConnection == null)
            throw new IllegalStateException("Not connected");
    }
}
