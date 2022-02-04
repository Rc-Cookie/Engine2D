package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Application;
import org.jetbrains.annotations.NotNull;

public class HostClientConnection extends ClientConnection {

    private final int port;

    @SuppressWarnings("ConstantConditions")
    @NotNull
    private Server server = null;

    public HostClientConnection() {
        this(Online.DEFAULT_PORT);
    }

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
