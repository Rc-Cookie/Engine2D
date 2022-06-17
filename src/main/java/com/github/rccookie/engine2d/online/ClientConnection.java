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
import com.github.rccookie.engine2d.coroutine.Execute;
import com.github.rccookie.engine2d.online.server.Message;
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
    private final Map<String, Consumer<OnlineData>> shareProcessors = new HashMap<>();
    {
        Set<Consumer<OnlineData>> set = new HashSet<>();
        set.add(data -> Execute.later(() -> data.json.asObject().forEach((k, d) -> {
            if(shareProcessors.containsKey(k))
                shareProcessors.get(k).accept(new OnlineData(data.json.get(k), data.delay, data.type));
        })));
        generalProcessors.put(MessageType.CLIENT_TO_CLIENT, set);
    }
    private final Map<String, Consumer<OnlineData>> sendProcessors = new HashMap<>();
    {
        Set<Consumer<OnlineData>> set = new HashSet<>();
        set.add(data -> Execute.later(() -> data.json.asObject().forEach((k, d) -> {
            if(sendProcessors.containsKey(k))
                sendProcessors.get(k).accept(new OnlineData(data.json.get(k), data.delay, data.type));
        })));
        generalProcessors.put(MessageType.SERVER_TO_CLIENT, set);
    }
    private final JsonObject queuedShareData = new JsonObject();
    private final JsonObject queuedSendData = new JsonObject();
    private final Thread outputThread = new Thread("Online Output Thread") {
        {
            setDaemon(true);
        }
        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while(true) {
                try { join(); } catch(InterruptedException ignored) { }
                String messageString;
                synchronized (queuedShareData) {
                    if(queuedShareData.isEmpty()) messageString = null;
                    else {
                        messageString = Json.toString(new Message(JsonElement.wrap(queuedShareData), MessageType.CLIENT_TO_CLIENT), false);
                        queuedShareData.clear();
                    }
                }
                if(messageString != null) out.println(messageString);
                synchronized (queuedSendData) {
                    if(queuedSendData.isEmpty()) messageString = null;
                    else {
                        messageString = Json.toString(new Message(JsonElement.wrap(queuedSendData), MessageType.CLIENT_TO_SERVER), false);
                        queuedSendData.clear();
                    }
                }
                if(messageString != null) out.println(messageString);
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

    /**
     * Executed before connecting. Used by {@link HostClientConnection}.
     */
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
        Console.log("Disconnected from server");
    }

    private void checkForData() {
        boolean queued = false;
        synchronized (queuedShareData) {
            if(!queuedShareData.isEmpty()) queued = true;
        }
        if(!queued) synchronized (queuedSendData) {
            if(!queuedSendData.isEmpty()) queued = true;
        }
        if(queued)
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

    public void registerShareProcessor(String key, Consumer<OnlineData> processor) {
        checkClosed();
        shareProcessors.put(Arguments.checkNull(key, "key"),
                Arguments.checkNull(processor, "processor"));
    }

    public void registerSendProcessor(String key, Consumer<OnlineData> processor) {
        checkClosed();
        sendProcessors.put(Arguments.checkNull(key, "key"),
                Arguments.checkNull(processor, "processor"));
    }

    public void share(String key, Object jsonData) {
        checkClosed();
        synchronized (queuedShareData) {
            queuedShareData.put(key, jsonData);
        }
    }

    public <T> void share(String key, T jsonData, BinaryOperator<T> combiner) {
        checkClosed();
        synchronized (queuedShareData) {
            if(combiner != null && queuedShareData.containsKey(key))
                queuedShareData.put(key, combiner.apply(jsonData, queuedShareData.getElement(key).get()));
            else queuedShareData.put(key, jsonData);
        }
    }

    public void send(String key, Object jsonData) {
        checkClosed();
        synchronized (queuedSendData) {
            queuedSendData.put(key, jsonData);
        }
    }

    public <T> void send(String key, T jsonData, BinaryOperator<T> combiner) {
        checkClosed();
        synchronized (queuedSendData) {
            if(combiner != null && queuedSendData.containsKey(key))
                queuedSendData.put(key, combiner.apply(jsonData, queuedSendData.getElement(key).get()));
            else queuedSendData.put(key, jsonData);
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
