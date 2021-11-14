package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.Vec2;
import org.jbox2d.collision.shapes.CircleShape;

public class CircleCollider extends AbstractCollider<CircleShape> {

    private final Vec2 offset = new Vec2();

    public CircleCollider(GameObject gameObject, float radius) {
        super(gameObject, new CircleShape());
        setRadius(radius);
    }

    public float getRadius() {
        return shape.m_radius;
    }

    public void setRadius(float radius) {
        shape.setRadius(radius * Convert.PIXELS_TO_UNITS);
    }

    @Override
    public Vec2 getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Vec2 offset) {
        this.offset.set(offset);
        shape.m_p.set(offset);
    }
}
