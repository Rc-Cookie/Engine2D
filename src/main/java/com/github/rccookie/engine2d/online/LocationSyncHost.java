package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Component;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.json.JsonObject;

public class LocationSyncHost extends Component {

    private static long nextId = 0;

    private final long id = nextId++;

    public LocationSyncHost(GameObject gameObject) {
        super(gameObject);
        lateUpdate.add(this::update);
    }

    private void update() {
        JsonObject content = new JsonObject(
                "loc", gameObject.location,
                "vel", gameObject.velocity,
                "angle", gameObject.angle,
                "rot", gameObject.rotation
        );
        Online.submitData("locationSync" + id, content);
    }
}
