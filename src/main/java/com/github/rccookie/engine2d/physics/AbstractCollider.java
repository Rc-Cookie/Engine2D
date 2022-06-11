package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Collider;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.util.Arguments;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jetbrains.annotations.NotNull;

/**
 * Generic implementation of a collider.
 *
 * @param <S> The shape type of the collider
 */
abstract class AbstractCollider<S extends Shape> extends Collider {

    /**
     * The Box2D shape.
     */
    final S shape;
    /**
     * The currently used fixture.
     */
    Fixture fixture;
    /**
     * The factory for new fixtures.
     */
    final FixtureDef fixtureData;
    /**
     * The offset of the collider to the gameobject's center.
     */
    final float2 offset = float2.zero();


    /**
     * A body that is queued up for fixture generation.
     */
    private Body delayed = null;


    /**
     * Creates a new abstract collider.
     *
     * @param gameObject The gameobject to attach to
     * @param shape The shape of the collider
     */
    AbstractCollider(@NotNull GameObject gameObject, @NotNull S shape) {
        super(gameObject);
        this.shape = Arguments.checkNull(shape);
        fixtureData = new FixtureDef();
        fixtureData.shape = shape;
        fixtureData.friction = 0.5f;
        fixtureData.density = 1;
        fixtureData.restitution = 0.2f;
        fixtureData.userData = this;
        if(delayed != null)
            generateFixture(delayed);
    }

    @Override
    protected void clearFixture() {
        fixture = null;
    }

    @Override
    protected void generateFixture(Body body) {
        FixtureDef fixtureData = this.fixtureData;
        if(fixtureData == null) {
            delayed = body;
            return;
        }
        delayed = null;
        fixture = body.createFixture(fixtureData);
    }

    @Override
    public boolean contains(float2 p) {
        return shape.testPoint(new Transform(), new float2(p));
    }

    @Override
    public boolean isSensor() {
        return fixtureData.isSensor;
    }

    @Override
    public void setSensor(boolean sensor) {
        fixtureData.isSensor = sensor;
        if(fixture != null)
            fixture.setSensor(sensor);
    }

    @Override
    public float getDensity() {
        return fixtureData.density;
    }

    @Override
    public void setDensity(float density) {
        fixtureData.density = density;
        if(fixture != null)
            fixture.setDensity(density);
    }

    @Override
    public float getFriction() {
        return fixtureData.friction;
    }

    @Override
    public void setFriction(float friction) {
        fixtureData.friction = friction;
        if(fixture != null)
            fixture.setFriction(friction);
    }

    @Override
    public float getRestitution() {
        return fixtureData.restitution;
    }

    @Override
    public void setRestitution(float restitution) {
        fixtureData.restitution = restitution;
        if(fixture != null)
            fixture.setRestitution(restitution);
    }

    @Override
    public float2 getOffset() {
        return offset;
    }

    @Override
    @Deprecated
    public Raycast raycast(float2 p, float2 d, float maxDLength) {
        if(fixture == null) return null;
        RayCastInput input = new RayCastInput();
        input.p1.set(p);
        input.p2.set(d).scale(maxDLength).add(p);
        input.maxFraction = 1;
        RayCastOutput output = new RayCastOutput();
        if(fixture.raycast(output, input, 0))
            return new Raycast(true, d.scaled(output.fraction).add(p), output.normal, this);
        return new Raycast();
    }
}
