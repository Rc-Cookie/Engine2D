package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Component;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.Vec2;

public class LocationSyncApplier extends Component {

    public LocationSyncApplier(GameObject gameObject, int id) {
        super(gameObject);
        Online.registerProcessor("locationSync" + id, this::update);
    }

    private void update(OnlineData data) {
        gameObject.location.set(data.json.get("loc").as(Vec2.class));
        gameObject.velocity.set(data.json.get("vel").as(Vec2.class));
        gameObject.angle = data.json.get("angle").asFloat();
        gameObject.rotation = data.json.get("rot").asFloat();
    }
}
