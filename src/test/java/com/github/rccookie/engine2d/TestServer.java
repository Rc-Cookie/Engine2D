package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.online.server.Client;
import com.github.rccookie.engine2d.online.server.Message;
import com.github.rccookie.engine2d.online.server.Server;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.util.Console;

class TestServer extends Server {



    @Override
    protected boolean accept(Client client) {
        return true;
    }

    @Override
    protected void onConnect(Client client) {
    }

    @Override
    protected void onMessage(Client source, Message message) {
        Console.map("Message received from", source);
        Console.log(message.toJson());
        send(new JsonObject("messageReport", new JsonObject("action", "acknowledged")), source);
    }

    @Override
    protected void onDisconnect(Client client) {

    }

    public static void main(String[] args) {
        new TestServer();
    }
}
