package com.github.rccookie.engine2d.online.server;

import com.github.rccookie.engine2d.online.Online;
import com.github.rccookie.util.Args;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Console;

public class ShareOnlyServer extends Server {

    public ShareOnlyServer() {
    }

    public ShareOnlyServer(int port) {
        super(port);
    }

    @Override
    protected boolean accept(Client client) {
        return true;
    }

    @Override
    protected void onConnect(Client client) {
    }

    @Override
    protected void onMessage(Client source, Message message) {
        Console.warn("Received message from ", source);
        Console.warn(message);
        Console.warn("ShareOnlyServer cannot messages to the server");
    }

    @Override
    protected void onDisconnect(Client client) {
    }


    public static void main(String[] args) {
        ArgsParser parser = new ArgsParser();
        parser.addDefaults();
        parser.setName("Engine2D Server");
        parser.setDescription("Standalone share-only server for Engine2D applications");
        parser.addOption('p', "port", true, "The port to open the server at");
        Args options = parser.parse(args);

        new ShareOnlyServer(options.getIntOr("port", Online.DEFAULT_PORT));
    }
}
