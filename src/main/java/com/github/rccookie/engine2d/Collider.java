package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.physics.Raycast;
import com.github.rccookie.geometry.performance.Vec2;
import org.jbox2d.dynamics.Body;

public abstract class Collider extends Component {

    public Collider(GameObject gameObject) {
        super(gameObject);
        gameObject.colliders.add(this);
        if(gameObject.body != null)
            generateFixture(gameObject.body);
    }

    public abstract boolean contains(Vec2 p);

    public abstract Vec2 getOffset();

    public abstract void setOffset(Vec2 offset);

    public abstract boolean isSensor();

    public abstract void setSensor(boolean sensor);

    public abstract float getDensity();

    public abstract void setDensity(float density);

    public abstract float getFriction();

    public abstract void setFriction(float friction);

    public abstract float getRestitution();

    public abstract void setRestitution(float restitution);

    public abstract Raycast raycast(Vec2 p, Vec2 d, float maxDLength);

    public Raycast raycast(Vec2 p, Vec2 d) {
        return raycast(p, d, 1000);
    }

    protected abstract void clearFixture();

    protected abstract void generateFixture(Body body);

    // TODO: Filter?
}
