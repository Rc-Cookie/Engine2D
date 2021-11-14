package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.Vec2;
import org.jbox2d.collision.shapes.EdgeShape;

public class EdgeCollider extends AbstractCollider<EdgeShape> {


    public EdgeCollider(GameObject gameObject, Vec2 a, Vec2 b) {
        super(gameObject, new EdgeShape());
        setEnds(a, b);
    }

    public Vec2 getA() {
        return shape.m_vertex1;
    }

    public Vec2 getB() {
        return shape.m_vertex2;
    }

    public Vec2[] getEnds() {
        return new Vec2[] { shape.m_vertex1, shape.m_vertex2 };
    }

    public void setEnds(Vec2 a, Vec2 b) {
        shape.set(a, b);
    }



    @Override
    public Vec2 getOffset() {
        return Vec2.ZERO;
    }

    @Override
    public void setOffset(Vec2 offset) {
        throw new UnsupportedOperationException();
    }
}
