package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.engine2d.util.Convert;
import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jetbrains.annotations.NotNull;

/**
 * A collider that has a rectangular from.
 */
public class BoxCollider extends AbstractCollider<PolygonShape> {

    /**
     * The size of the box.
     */
    private final float2 size = new float2();


    /**
     * Creates a new box collider with the specified size.
     *
     * @param gameObject The gameobject to attach to
     * @param size The size of the box
     */
    public BoxCollider(@NotNull GameObject gameObject, float2 size) {
        super(gameObject, new PolygonShape());
        setSize(size);
    }

    /**
     * Returns the size of the box collider.
     *
     * @return The box size
     */
    public float2 getSize() {
        return size;
    }

    /**
     * Sets the size of the box collider.
     *
     * @param size The box size
     */
    public void setSize(float2 size) {
        this.size.set(size);
        shape.setAsBox(size.x * 0.5f * Convert.PIXELS_TO_UNITS, size.y * 0.5f * Convert.PIXELS_TO_UNITS);
    }

    @Override
    public void setOffset(float2 offset) {
        this.offset.set(offset);
        shape.m_centroid.set(offset);
    }
}
