package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Application;

import org.jetbrains.annotations.NotNull;

/**
 * A client connection that is its own host and manages a server instance as well
 * as a client instance.
 */
public class HostClientConnection extends ClientConnection {

    /**
     * The server port.
     */
    private final int port;

    /**
     * The server managed by the connection.
     */
    @SuppressWarnings({"ConstantConditions", "FieldCanBeLocal", "unused"})
    @NotNull
    private Server server = null;

    /**
     * Creates a new host client connection at the default port.
     */
    public HostClientConnection() {
        this(Online.DEFAULT_PORT);
    }

    /**
     * Creates a new host client connection at the given port.
     *
     * @param port The port to open the server at
     */
    public HostClientConnection(int port) {
        super("localhost", port);
        this.port = port;
    }

    @Override
    void beforeConnect() {
        server = new Server(port);
        Application.getImplementation().sleep(1000, 0);
    }
}
