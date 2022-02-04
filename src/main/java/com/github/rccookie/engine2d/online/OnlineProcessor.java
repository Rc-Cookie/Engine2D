package com.github.rccookie.engine2d.online;

import com.github.rccookie.json.JsonElement;

public interface OnlineProcessor {

    String getKey();

    void processData(JsonElement data);
}
