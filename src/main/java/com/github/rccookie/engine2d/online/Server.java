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

import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.json.JsonParser;
import com.github.rccookie.util.Args;
import com.github.rccookie.util.ArgsParser;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ModIterableArrayList;

import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Nullable;

/**
 * A server connects to multiple clients and forwards messages sent by them to all others.
 * It can run as standalone application.
 */
public class Server {

    /**
     * The websocket of this server.
     */
    private final ServerSocket server;
    /**
     * List of outputs to the client sockets.
     */
    private final List<PrintStream> outs = new ModIterableArrayList<>();


    /**
     * Starts a new server on the default port.
     */
    public Server() {
        this(Online.DEFAULT_PORT);
    }

    /**
     * Starts a new server on the specified port.
     *
     * @param port The port for connections to this server
     */
    public Server(int port) {
        this(port, false);
    }

    /**
     * Starts a new server.
     *
     * @param port The port for connections to this server
     * @param standalone Must be set to {@code true} when running the server
     *                   as standalone application to avoid the server threads
     *                   stopping when the main thread stops
     */
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

    /**
     * Blocks infinitely and accepts connection requests from clients, and starts
     * listening and writing to them.
     */
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
                sendData(createMessage(content, MessageType.SERVER_TO_CLIENT), out);
            }
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Listens to the given client socket, processing any received messages. This method blocks
     * until the client disconnects.
     *
     * @param in The input stream of the client socket
     * @param out A print writer to the output stream of the client
     * @param client The client socket
     */
    @Blocking
    private void listen(InputStream in, PrintStream out, Socket client) {
        try(JsonParser parser = Json.getParser(in)) {
            for (JsonElement data : parser) {
                if(data.get("type").asInt() == MessageType.CLIENT_TO_CLIENT.ordinal())
                    sendData(data, out);
                else {
                    float delay = (System.currentTimeMillis() - data.get("time").asInt()) / 1000f;
                    JsonElement content = data.get("content");
                    MessageType type = MessageType.values()[data.get("type").asInt()];
                    onMessage(new OnlineData(content, delay, type));
                }
            }
        } catch(UncheckedIOException e) {
            if(!(e.getCause() instanceof SocketException) || !"Connection reset".equals(e.getCause().getMessage()))
                throw e;
            synchronized (outs) { outs.remove(out); }
            Console.map("Disconnected", client.getInetAddress().getHostAddress());
            JsonObject content = new JsonObject();
            content.put("message", "client disconnected");
            content.put("address", client.getInetAddress().getHostAddress());
            sendData(createMessage(content, MessageType.SERVER_TO_CLIENT), null);
        }
    }

    protected void onConnect() {

    }

    protected void onMessage(OnlineData message) {
    }


    /**
     * Creates a new json message from the given content.
     *
     * @param jsonContent The content to be used
     * @param type The type of message to create
     * @return The message as json element
     */
    static JsonElement createMessage(Object jsonContent, MessageType type) {
        JsonObject data = new JsonObject();
        data.put("content", jsonContent);
        data.put("type", type.ordinal());
        data.put("time", System.currentTimeMillis());
        return data.asElement();
    }

    /**
     * Writes the given data to all client's output streams except to the source client.
     *
     * @param data The data to send
     * @param source The print stream associated with the source client. {@code null} means
     *               a message from the server itself to all clients
     */
    private void sendData(JsonElement data, @Nullable PrintStream source) {
        String stringData = Json.toString(data, false);
        outs.stream().parallel().filter(o -> o != source).forEach(o -> o.println(stringData));
    }


    /**
     * Runs a new server in standalone mode on the default port.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        ArgsParser parser = new ArgsParser();
        parser.addDefaults();
        parser.setName("Engine2D Server");
        parser.setDescription("Standalone server for Engine2D applications");
        parser.addOption('p', "port", true, "The port to open the server at");
        Args options = parser.parse(args);

        new Server(options.getIntOr("port", Online.DEFAULT_PORT), true);
    }
}
