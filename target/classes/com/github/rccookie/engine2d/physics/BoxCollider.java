package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.Vec2;
import org.jbox2d.collision.shapes.PolygonShape;

public class BoxCollider extends AbstractCollider<PolygonShape> {

    private final Vec2 size = new Vec2();
    private final Vec2 offset = new Vec2();

    public BoxCollider(GameObject gameObject, Vec2 size) {
        super(gameObject, new PolygonShape());
        setSize(size);
    }

    public Vec2 getSize() {
        return size;
    }

    public void setSize(Vec2 size) {
        this.size.set(size);
        shape.setAsBox(size.x * 0.5f * Convert.PIXELS_TO_UNITS, size.y * 0.5f * Convert.PIXELS_TO_UNITS);
    }

    @Override
    public Vec2 getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Vec2 offset) {
        this.offset.set(offset);
        shape.m_centroid.set(offset);
    }
}
