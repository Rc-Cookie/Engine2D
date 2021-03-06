package com.github.rccookie.engine2d.online;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/**
 * Utility class for communicating with a Engine2D server and other clients.
 */
public class Online {

    /**
     * Default port for the socket connection.
     */
    public static final int DEFAULT_PORT = 62200;

    /**
     * Current connection to the server.
     */
    static ClientConnection clientConnection = null;
    /**
     * The server if the client is client and server at the same time.
     */
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private static Server server = null;

    private Online() {
        throw new UnsupportedOperationException("Online does not allow instances");
    }


    /**
     * Connects to the given host at the default port.
     * <p>The first messages will not be processed until the end of the current frame,
     * meaning that there is no chance that processors may be attached to late for the
     * messages, if they are attached immediately after connection.</p>
     *
     * @param host The host to connect to, for example an ip address
     */
    public static void connect(String host) {
        connect(host, DEFAULT_PORT);
    }

    /**
     * Connects to the given host.
     * <p>The first messages will not be processed until the end of the current frame,
     * meaning that there is no chance that processors may be attached to late for the
     * messages, if they are attached immediately after connection.</p>
     *
     * @param host The host to connect to, for example an ip address
     * @param port The port to connect to
     */
    public synchronized static void connect(String host, int port) {
        if(clientConnection != null)
            throw new IllegalStateException("Already connected");
        clientConnection = new ClientConnection(host, port);
    }

    /**
     * Asynchronously starts a server on this machine and connects this
     * application as client to it.
     */
    @Deprecated
    public void hostAndConnect() {
        hostAndConnect(DEFAULT_PORT);
    }

    /**
     * Asynchronously starts a server on this machine and connects this
     * application as client to it.
     *
     * @param port The port to host the server on
     */
    @Deprecated
    public synchronized static void hostAndConnect(int port) {
        if(clientConnection != null)
            throw new IllegalStateException("Already connected");
        server = new Server(port);
        clientConnection = new ClientConnection("localhost", port);
    }

    /**
     * Disconnects from the server, if connected.
     */
    public static synchronized void disconnect() {
        if(clientConnection == null) return;
        clientConnection.close();
        clientConnection = null;
    }

    /**
     * Returns whether the application is currently connected to a server.
     *
     * @return Whether the client is connected
     */
    public static boolean isConnected() {
        return clientConnection != null;
    }

    /**
     * Registers a message processor for received messages of the specified message type.
     * <p>This method may only be called when connected.</p>
     *
     * @param type The type of message to listen to
     * @param processor The action to perform when a message of the specified type is
     *                  received
     */
    public static void registerProcessor(MessageType type, Consumer<OnlineData> processor) {
        checkConnected();
        clientConnection.registerProcessor(type, processor);
    }

    /**
     * Registers a message processor for received messages of the specified name that
     * were used with {@link #share(String, Object)}.
     * <p>This method may only be called when connected.</p>
     *
     * @param key The name of messages to listen to
     * @param processor The action to perform when a message of the specified type is
     *                  received
     */
    public static void registerShareProcessor(String key, Consumer<OnlineData> processor) {
        checkConnected();
        clientConnection.registerShareProcessor(key, processor);
    }

    public static void registerSendProcessor(String key, Consumer<OnlineData> processor) {
        checkConnected();
        clientConnection.registerSendProcessor(key, processor);
    }

    /**
     * Queues the given data to be sent to all other connected clients. If other data
     * is already queued up with the same name it will be replaced and the old data
     * will not be sent.
     *
     * @param key The name of the data, match this with {@link #registerShareProcessor(String, Consumer)}
     * @param jsonValue The message content, must be convertible to json
     */
    public static void share(String key, Object jsonValue) {
        checkConnected();
        clientConnection.share(key, jsonValue);
    }

    /**
     * Queues the given data to be sent to all other connected clients. If other data
     * is already queued up with the same name the new data will be combined with the
     * old data using the specified function. The first parameter will be the new data,
     * the second one the old one.
     *
     * @param key The name of the data, match this with {@link #registerShareProcessor(String, Consumer)}
     * @param jsonValue The message content, must be convertible to json
     */
    public static <T> void share(String key, T jsonValue, BinaryOperator<T> combiner) {
        checkConnected();
        clientConnection.share(key, jsonValue, combiner);
    }

    public static void send(String key, Object jsonValue) {
        checkConnected();
        clientConnection.send(key, jsonValue);
    }

    public static <T> void send(String key, T jsonValue, BinaryOperator<T> combiner) {
        checkConnected();
        clientConnection.send(key, jsonValue, combiner);
    }


    /**
     * Checks that the client is connected.
     *
     * @throws IllegalStateException If the client is not connected
     */
    private static void checkConnected() throws IllegalStateException {
        if(clientConnection == null)
            throw new IllegalStateException("Not connected");
    }
}
