package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.Vec2;
import org.jbox2d.collision.shapes.PolygonShape;

import java.util.Arrays;

public class PolygonCollider extends AbstractCollider<PolygonShape> {

    public PolygonCollider(GameObject gameObject, Vec2... vertices) {
        super(gameObject, new PolygonShape());
        setVertices(vertices);
    }

    public Vec2[] getVertices() {
        return Arrays.copyOf(shape.m_vertices, shape.m_vertices.length);
    }

    public Vec2 getVertex(int index) {
        return shape.getVertex(index);
    }

    public int vertexCount() {
        return shape.m_count;
    }

    public void setVertices(Vec2[] vertices) {
        shape.set(vertices, vertices.length);
    }

    @Override
    public Vec2 getOffset() {
        return shape.m_centroid;
    }

    @Override
    public void setOffset(Vec2 offset) {
        shape.m_centroid.set(offset);
    }
}
