package com.github.rccookie.engine2d.online.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import com.github.rccookie.util.Console;

public class Client {

    public final InetAddress ipAddress;

    final Server server;
    final Socket socket;
    final PrintStream out;

    private boolean connected = true;

    Client(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.ipAddress = socket.getLocalAddress();
        this.out = new PrintStream(socket.getOutputStream());
    }

    @Override
    public String toString() {
        return "Client at " + ipAddress;
    }

    public boolean isConnected() {
        return connected;
    }

    public void disconnect() {
        if(!connected) return;
        connected = false;
        server.clients.remove(this);
        try { socket.close(); }
        catch(Exception e) { throw new RuntimeException(e); }
        Console.map("Client disconnected", this);
        server.onDisconnect(this);
    }
}
