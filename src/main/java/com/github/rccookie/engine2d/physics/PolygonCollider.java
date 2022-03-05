package com.github.rccookie.engine2d.physics;

import java.util.Arrays;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.float2;

import org.jbox2d.collision.shapes.PolygonShape;

/**
 * A collider that has a custom polygon shape.
 */
public class PolygonCollider extends AbstractCollider<PolygonShape> {

    /**
     * Creates a new polygon collider from the given set of vertices.
     *
     * @param gameObject The gameobject to attach to
     * @param vertices The edge points of the polygon
     */
    public PolygonCollider(GameObject gameObject, float2... vertices) {
        super(gameObject, new PolygonShape());
        setVertices(vertices);
    }

    /**
     * Returns the vertices of the collider.
     *
     * @return The vertices
     */
    public float2[] getVertices() {
        return Arrays.copyOf(shape.m_vertices, shape.m_vertices.length);
    }

    /**
     * Returns a specific vertex of the collider.
     *
     * @param index The index of the vertex to get
     * @return The vertex at that index
     */
    public float2 getVertex(int index) {
        return shape.getVertex(index);
    }

    /**
     * Returns the number of vertices in this polygon.
     *
     * @return The number of vertices
     */
    public int vertexCount() {
        return shape.m_count;
    }

    /**
     * Sets the vertices of this collider.
     *
     * @param vertices The vertices to set
     */
    public void setVertices(float2... vertices) {
        shape.set(vertices, vertices.length);
    }

    @Override
    public float2 getOffset() {
        return shape.m_centroid;
    }

    @Override
    public void setOffset(float2 offset) {
        shape.m_centroid.set(offset);
    }
}
