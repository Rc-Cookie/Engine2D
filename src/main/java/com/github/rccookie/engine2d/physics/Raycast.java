package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Collider;
import com.github.rccookie.geometry.performance.float2;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a raycast result.
 *
 * @deprecated Box2D raycasts don't seem to work. Use {@link com.github.rccookie.geometry.performance.Raycast}
 *             instead
 */
@Deprecated
public class Raycast {

    /**
     * Has the ray hit anything?
     */
    public final boolean collided;
    /**
     * The point of collision.
     */
    public final float2 point;
    /**
     * The collision normal vector.
     */
    public final float2 normal;
    /**
     * The collider that was hit.
     */
    public final Collider collider;


    /**
     * Creates a new raycast.
     *
     * @param collided Whether the raycast has hit anything
     * @param point The point of collision
     * @param normal The collision normal
     * @param collider The collider that was hit
     */
    public Raycast(boolean collided, @Nullable float2 point, @Nullable float2 normal, @Nullable Collider collider) {
        this.collided = collided;
        this.point = point;
        this.normal = normal;
        this.collider = collider;
    }

    /**
     * Creates a new raycast that did not hit anything.
     */
    public Raycast() {
        this(false, null, null, null);
    }
}
