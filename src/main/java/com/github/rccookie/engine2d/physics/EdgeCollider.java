package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.util.Arguments;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jetbrains.annotations.NotNull;

/**
 * A collider that is a line.
 */
public class EdgeCollider extends AbstractCollider<EdgeShape> {

    /**
     * Creates a new edge collider.
     *
     * @param gameObject The gameobject to attach to
     * @param a The start point of the line, relative to the gameobject
     * @param b The end point of the line, relative to the gameobject
     */
    public EdgeCollider(@NotNull GameObject gameObject, @NotNull float2 a, @NotNull float2 b) {
        super(gameObject, new EdgeShape());
        setEnds(a, b);
    }

    /**
     * Returns the start point of the edge, relative to the gameobject.
     *
     * @return The start point
     */
    public float2 getA() {
        return shape.m_vertex1.subed(offset);
    }

    public float2 getB() {
        return shape.m_vertex2.subed(offset);
    }

    /**
     * Returns an array of length 2 with a and b.
     *
     * @return The ends of the edge
     */
    public float2[] getEnds() {
        return new float2[] { shape.m_vertex1.subed(offset), shape.m_vertex2.subed(offset) };
    }

    /**
     * Sets the start point of the edge.
     *
     * @param a The point to set
     */
    public void setA(float2 a) {
        shape.set(Arguments.checkNull(a, "a").added(offset), shape.m_vertex2);
    }

    /**
     * Sets the end point of the edge.
     *
     * @param b The point to set
     */
    public void setB(float2 b) {
        shape.set(shape.m_vertex1, Arguments.checkNull(b, "b").added(offset));
    }

    /**
     * Sets both end points of the edge.
     *
     * @param a The edge start
     * @param b The edge end
     */
    public void setEnds(float2 a, float2 b) {
        shape.set(Arguments.checkNull(a, "a").added(offset), Arguments.checkNull(b, "b").added(b));
    }



    @Override
    public void setOffset(float2 offset) {
        Arguments.checkNull(offset, "offset");
        shape.set(shape.m_vertex1.subed(this.offset).add(offset), shape.m_vertex2.subed(this.offset).add(offset));
        this.offset.set(offset);
    }
}
