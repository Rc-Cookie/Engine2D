package com.github.rccookie.engine2d.online;

import com.github.rccookie.engine2d.Component;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.json.JsonObject;

/**
 * Simple component that regularly sends the location of this gameobject
 * to all other clients. To be used with {@link LocationSyncApplier}.
 */
public class LocationSyncHost extends Component {

    /**
     * The next free id to use.
     */
    private static long nextId = 0;

    /**
     * The id of this gameobject synchronization.
     */
    private final long id = nextId++;


    /**
     * Creates a new location sync host.
     *
     * @param gameObject The gameobject to synchronize
     */
    public LocationSyncHost(GameObject gameObject) {
        super(gameObject);
        lateUpdate.add(this::update);
    }

    /**
     * Returns the location sync host's id. The receiving {@link LocationSyncApplier}
     * must use the same id.
     *
     * @return This location host's id
     */
    public long getId() {
        return id;
    }


    /**
     * Sends the current location to all other clients.
     */
    private void update() {
        JsonObject content = new JsonObject(
                "loc", gameObject.location,
                "vel", gameObject.velocity,
                "angle", gameObject.angle,
                "rot", gameObject.rotation
        );
        Online.share("locationSync" + id, content);
    }
}
