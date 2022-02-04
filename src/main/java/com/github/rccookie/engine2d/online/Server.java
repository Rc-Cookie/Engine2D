package com.github.rccookie.engine2d.online;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import com.github.rccookie.engine2d.util.ModIterableArrayList;
import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.json.JsonParser;
import com.github.rccookie.util.Console;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Nullable;

public class Server {

    private final ServerSocket server;
    private final List<PrintStream> outs = new ModIterableArrayList<>();

    public Server() {
        this(Online.DEFAULT_PORT);
    }

    public Server(int port) {
        this(port, false);
    }

    public Server(int port, boolean standalone) {
        try {
            server = new ServerSocket(port);
            Console.map("Server started at", Inet4Address.getLocalHost().getHostAddress() + ":" + port);
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
        Thread connectionThread = new Thread(this::acceptConnections, "Server Connection Thread");
        connectionThread.setDaemon(!standalone);
        connectionThread.start();
    }

    @Blocking
    private void acceptConnections() {
        try {
            while(true) {
                Socket client = server.accept();
                InputStream in = client.getInputStream();
                PrintStream out = new PrintStream(client.getOutputStream());
                synchronized(outs) { outs.add(out); }
                Thread listener = new Thread(() -> listen(in, out, client), "Server Client Listener");
                listener.setDaemon(true);
                listener.start();
                Console.map("Client connected", client.getInetAddress().getHostAddress());
                JsonObject content = new JsonObject();
                content.put("message", "client connected");
                content.put("address", client.getInetAddress().getHostAddress());
                sendData(Online.createMessage(content, MessageType.SERVER_TO_CLIENT), out);
            }
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Blocking
    private void listen(InputStream in, PrintStream source, Socket client) {
        try(JsonParser parser = Json.getParser(in)) {
            for (JsonElement data : parser)
                sendData(data, source);
        } catch(UncheckedIOException e) {
            if(!(e.getCause() instanceof SocketException) || !"Connection reset".equals(e.getCause().getMessage()))
                throw e;
            synchronized (outs) { outs.remove(source); }
            Console.map("Disconnected", client.getInetAddress().getHostAddress());
            JsonObject content = new JsonObject();
            content.put("message", "client disconnected");
            content.put("address", client.getInetAddress().getHostAddress());
            sendData(Online.createMessage(content, MessageType.SERVER_TO_CLIENT), null);
        }
    }

    private void sendData(JsonElement data, @Nullable PrintStream source) {
        String stringData = Json.toString(data, false);
        outs.stream().parallel().filter(o -> o != source).forEach(o -> o.println(stringData));
    }


    public static void main(String[] args) {
        new Server(Online.DEFAULT_PORT, true);
    }
}
