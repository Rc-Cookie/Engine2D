package com.github.rccookie.engine2d.online;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.json.Json;
import com.github.rccookie.json.JsonElement;
import com.github.rccookie.json.JsonObject;
import com.github.rccookie.json.JsonParser;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import org.jetbrains.annotations.Blocking;

public class ClientConnection implements Closeable {

    private volatile boolean closed = false;
    private final InputStream in;
    private final PrintStream out;
    private final Map<MessageType, Set<Consumer<OnlineData>>> generalProcessors = new HashMap<>();
    private final Map<String, Consumer<OnlineData>> processors = new HashMap<>();
    {
        Set<Consumer<OnlineData>> set = new HashSet<>();
        set.add(data -> Execute.later(() -> data.json.asObject().forEach((k, d) -> {
            if(processors.containsKey(k))
                processors.get(k).accept(new OnlineData(data.json.get(k), data.delay, data.type));
        })));
        generalProcessors.put(MessageType.CLIENT_TO_CLIENT, set);
    }
    private final JsonObject queuedData = new JsonObject();
    private final Thread outputThread = new Thread("Online Output Thread") {
        {
            setDaemon(true);
        }
        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while(true) {
                try { join(); } catch(InterruptedException ignored) { }
                String dataString;
                synchronized (queuedData) {
                    if(queuedData.isEmpty()) continue;
                    dataString = Online.createMessage(queuedData, MessageType.CLIENT_TO_CLIENT).asObject().toString(false);
                    queuedData.clear();
                }
                out.println(dataString);
            }
        }
    };

    @Blocking
    public ClientConnection(String host) {
        this(host, Online.DEFAULT_PORT);
    }

    @Blocking
    public ClientConnection(String host, int port) {
        beforeConnect();
        Socket server;
        try {
            server = new Socket(host, port);
            in = server.getInputStream();
            out = new PrintStream(server.getOutputStream());
        } catch(IOException e) {
            throw new UncheckedIOException(e);
        }
        Application.lateUpdate.add(this::checkForData);
        outputThread.start();
        Thread inputThread = new Thread(this::readData, "Online Input Thread");
        inputThread.setDaemon(true);
        inputThread.start();
    }

    void beforeConnect() {

    }

    @Blocking
    private void readData() {
        try (this; JsonParser parser = Json.getParser(in)) {
            for (JsonElement data : parser) {
                if(closed) return;
                Execute.later(() -> processData(data));
            }
        } catch(UncheckedIOException e) {
            if(!(e.getCause() instanceof SocketException) || !"Connection reset".equals(e.getCause().getMessage()))
                throw e;
            if(Online.clientConnection == this)
                Online.disconnect();
        }
        Console.info("Disconnected from server");
    }

    private void checkForData() {
        synchronized (queuedData) {
            if(queuedData.isEmpty()) return;
        }
        outputThread.interrupt();
    }

    private void processData(JsonElement data) {
        if(closed) return;
        float delay = (System.currentTimeMillis() - data.get("time").asInt()) / 1000f;
        JsonElement content = data.get("content");
        MessageType type = MessageType.values()[data.get("type").asInt()];
        OnlineData onlineData = new OnlineData(content, delay, type);
        if(generalProcessors.containsKey(type) && !generalProcessors.get(type).isEmpty()) {
            for (Consumer<OnlineData> processor : generalProcessors.get(type))
                processor.accept(onlineData);
        } else {
            Console.map("Received message of type", type);
            System.out.println(content);
        }
    }

    public void registerProcessor(MessageType messageType, Consumer<OnlineData> processor) {
        checkClosed();
        generalProcessors.computeIfAbsent(Arguments.checkNull(messageType, "messageType"), $ -> new HashSet<>())
                .add(Arguments.checkNull(processor, "processor"));
    }

    public void registerProcessor(String key, Consumer<OnlineData> processor) {
        checkClosed();
        processors.put(Arguments.checkNull(key, "key"),
                Arguments.checkNull(processor, "processor"));
    }

    public void submit(String key, Object jsonData) {
        checkClosed();
        synchronized (queuedData) {
            queuedData.put(key, jsonData);
        }
    }

    public <T> void submit(String key, T jsonData, BinaryOperator<T> combiner) {
        checkClosed();
        synchronized (queuedData) {
            if(combiner != null && queuedData.containsKey(key))
                queuedData.put(key, combiner.apply(jsonData, queuedData.getElement(key).get()));
            else queuedData.put(key, jsonData);
        }
    }

    private void checkClosed() throws IllegalStateException {
        if(closed) throw new IllegalStateException("Connection closed");
    }

    @Override
    public void close() {
        closed = true;
    }
}
