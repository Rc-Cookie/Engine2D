package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.json.JsonElement;

public abstract class OnlineService extends Thread {

    public OnlineService() {
        if(!Application.getImplementation().supportsMultithreading())
            throw new UnsupportedOperationException("Online services are not supported in single-threaded environments");
    }

    @Override
    public void run() {
        new Thread(() -> { while(true) sendCurrentSyncData(); }).start();
        receiveSyncData();
    }

    protected abstract void receiveSyncData();

    protected abstract void sendCurrentSyncData();

    protected abstract void processData(JsonElement info);
}
