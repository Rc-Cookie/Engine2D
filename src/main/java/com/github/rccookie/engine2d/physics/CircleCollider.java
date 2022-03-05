package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.engine2d.util.Convert;
import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.collision.shapes.CircleShape;

/**
 * A collider that has a circle form.
 */
public class CircleCollider extends AbstractCollider<CircleShape> {

    /**
     * Creates a new circle collider.
     *
     * @param gameObject The gameobject to attach to
     * @param radius The radius of the circle
     */
    public CircleCollider(GameObject gameObject, float radius) {
        super(gameObject, new CircleShape());
        setRadius(radius);
    }

    /**
     * Returns the current radius of the circle collider.
     *
     * @return The current radius
     */
    public float getRadius() {
        return shape.m_radius;
    }

    /**
     * Sets the radius of the circle collider.
     *
     * @param radius The radius to set
     */
    public void setRadius(float radius) {
        shape.setRadius(radius * Convert.PIXELS_TO_UNITS);
    }

    @Override
    public void setOffset(float2 offset) {
        this.offset.set(offset);
        shape.m_p.set(offset);
    }
}
