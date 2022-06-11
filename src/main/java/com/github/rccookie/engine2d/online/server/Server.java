package com.github.rccookie.engine2d.online.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import com.github.rccookie.engine2d.online.MessageType;
import com.github.rccookie.engine2d.online.Online;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.engine2d.util.annotations.Continuous;
import com.github.rccookie.engine2d.util.annotations.OverrideTarget;
import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.util.Console;

import org.jetbrains.annotations.Nullable;

public abstract class Server {

    static {
        Message.init();
    }

    private final ServerSocket socket;
    final Set<Client> clients = new HashSet<>();
    private final Set<Client> clientsView = Collections.unmodifiableSet(clients);

    private final Map<String, BiConsumer<Client, JsonElement>> processors = new HashMap<>();

    public Server() {
        this(Online.DEFAULT_PORT);
    }

    public Server(int port) {
        try {
            socket = new ServerSocket(port);
            Console.map("Server started at", InetAddress.getLocalHost().getHostAddress() + ":" + port);
        } catch(IOException e) {
            throw new RuntimeIOException(e);
        }
        new Thread(this::acceptConnections).start();
    }

    private void acceptConnections() {
        try {
            while(true) {
                Socket clientSocket = socket.accept();
                Client client = new Client(this, clientSocket);
                if(accept(client)) {
                    clients.add(client);
                    Thread listener = new Thread(() -> listenTo(client));
                    listener.setDaemon(true);
                    listener.start();
                    Console.map("Client connected", client);
                    onConnect(client);
                }
                else clientSocket.close();
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void listenTo(Client client) {
        try {
            for(JsonElement message : Json.getParser(client.socket.getInputStream()))
                processMessage(client, message.as(Message.class));
        } catch(Exception e) {
            if(!(e.getCause() instanceof SocketException) || !"Connection reset".equals(e.getCause().getMessage()))
                throw new RuntimeException(e);
            client.disconnect();
        }
    }

    private void processMessage(Client client, Message message) {
        if(message.type == MessageType.CLIENT_TO_CLIENT)
            onShare(client, message);
        else
            onMessage(client, message);
    }

    private void sendData(Message data, Iterator<Client> targets) {
        String dataString = Json.toString(data);
        while(targets.hasNext())
            targets.next().out.println(dataString);
    }



    public void send(JsonObject content, Client target) {
        sendData(new Message(content.asElement(), MessageType.SERVER_TO_CLIENT), Stream.of(target).iterator());
    }

    public void share(JsonObject content) {
        share(content, null);
    }

    public void share(JsonObject content, Client except) {
        sendData(new Message(content.asElement(), MessageType.SERVER_TO_CLIENT), clients.stream().filter(c -> c != except).iterator());
    }

    public Set<Client> getClients() {
        return clientsView;
    }

    public void registerProcessor(String key, @Nullable BiConsumer<Client, JsonElement> processor) {
        processors.put(key, processor);
    }


    protected abstract boolean accept(Client client);

    protected abstract void onConnect(Client client);

    @Continuous
    @OverrideTarget
    protected void onShare(Client source, Message message) {
        sendData(message, clients.stream().filter(c -> c != source).iterator());
    }

    @Continuous
    @OverrideTarget
    protected void onMessage(Client source, Message message) {
        if(message.type == MessageType.CLIENT_TO_SERVER) {
            message.content.forEach((k,v) -> {
                BiConsumer<Client, JsonElement> processor = processors.get(k);
                if(processor == null)
                    Console.error("Unknown command:", k);
                else try {
                    processor.accept(source, v);
                } catch(Exception e) {
                    Console.error("Exception in processor for", k, "-", v.isEmpty() ? "<no value present>" : v);
                    Console.error(e);
                }
            });
        }
        else Console.warn("Received unrelated message:", message);
    }

    protected abstract void onDisconnect(Client client);
}
