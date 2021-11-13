package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Collider;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.Arguments;
import org.jbox2d.collision.RayCastInput;
import org.jbox2d.collision.RayCastOutput;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Transform;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

abstract class AbstractCollider<S extends Shape> extends Collider {

    final S shape;
    Fixture fixture;
    final FixtureDef fixtureData;

    AbstractCollider(GameObject gameObject, S shape) {
        super(gameObject);
        this.shape = Arguments.checkNull(shape);
        fixtureData = new FixtureDef();
        fixtureData.shape = shape;
        fixtureData.friction = 0.5f;
        fixtureData.userData = this;
    }

    @Override
    protected void clearFixture() {
        fixture = null;
    }

    @Override
    protected void generateFixture(Body body) {
        fixture = body.createFixture(fixtureData);
    }

    @Override
    public boolean contains(Vec2 p) {
        return shape.testPoint(new Transform(), new Vec2(p));
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
    public Raycast raycast(Vec2 p, Vec2 d, float maxDLength) {
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
