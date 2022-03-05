package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Component;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.float2;

/**
 * Simple component that synchronizes the position of its gameobject with received
 * data from another client.
 */
public class LocationSyncApplier extends Component {

    /**
     * Creates a new location sync applier that listens for the specified id.
     *
     * @param gameObject The gameobject to attach to
     * @param id The id of the {@link LocationSyncHost} to listen to
     */
    public LocationSyncApplier(GameObject gameObject, int id) {
        super(gameObject);
        Online.registerProcessor("locationSync" + id, this::update);
    }

    /**
     * Update the location on received data.
     *
     * @param data The data received
     */
    private void update(OnlineData data) {
        gameObject.location.set(data.json.get("loc").as(float2.class));
        gameObject.velocity.set(data.json.get("vel").as(float2.class));
        gameObject.angle = data.json.get("angle").asFloat();
        gameObject.rotation = data.json.get("rot").asFloat();
    }
}
