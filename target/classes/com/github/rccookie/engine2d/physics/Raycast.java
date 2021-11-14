package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Collider;
import com.github.rccookie.geometry.performance.Vec2;

public class Raycast {

    public final boolean collided;
    public final Vec2 point;
    public final Vec2 normal;
    public final Collider collider;

    public Raycast(boolean collided, Vec2 point, Vec2 normal, Collider collider) {
        this.collided = collided;
        this.point = point;
        this.normal = normal;
        this.collider = collider;
    }

    public Raycast() {
        this(false, null, null, null);
    }
}
