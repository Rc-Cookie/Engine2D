package com.github.rccookie.engine2d;

import java.util.HashSet;
import java.util.Set;

import com.github.rccookie.engine2d.physics.Raycast;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.util.Utils;

import org.jbox2d.dynamics.Body;

/**
 * Abstract definition of a collider of a gameobject.
 */
public abstract class Collider extends Component {

    /**
     * Called on collision start with the collided object.
     */
    public final ParamEvent<Collider> onCollisionEnter = new CaughtParamEvent<>();

    /**
     * Called when a collision ends with a certain object.
     */
    public final ParamEvent<Collider> onCollisionExit = new CaughtParamEvent<>();

    private Set<Collider> colliding;


    /**
     * Creates a new collider.
     *
     * @param gameObject The gameobject to attach to
     */
    public Collider(GameObject gameObject) {
        super(gameObject);
        gameObject.colliders.add(this);
        if(gameObject.body != null)
            generateFixture(gameObject.body);
    }

    /**
     * Returns whether the collider contains the given point.
     *
     * @param p The point to test
     * @return Whether this collider contains the point
     */
    public abstract boolean contains(float2 p);

    /**
     * Returns the offset of the collider from the gameobject center.
     *
     * @return The current offset
     */
    public abstract float2 getOffset();

    /**
     * Sets the offset of the collider from the gameobject center.
     *
     * @param offset The offset to set
     */
    public abstract void setOffset(float2 offset);

    /**
     * Returns whether this collider is a sensor, meaning that it only
     * registers collisions without creating them.
     *
     * @return Whether this collider is a sensor
     */
    public abstract boolean isSensor();

    /**
     * Sets this collider to be a sensor or not, meaning that it will
     * only register collisions but not create them.
     *
     * @param sensor Whether this collider should be a sensor
     */
    public abstract void setSensor(boolean sensor);

    /**
     * Returns the mass density of this collider.
     *
     * @return The mass density
     */
    public abstract float getDensity();

    /**
     * Sets the mass density of this collider.
     *
     * @param density The density to set
     */
    public abstract void setDensity(float density);

    /**
     * Gets the edge friction of this collider to other colliders.
     *
     * @return The colliders friction
     */
    public abstract float getFriction();

    /**
     * Sets the edge friction of this collider to other colliders.
     *
     * @param friction The friction to set
     */
    public abstract void setFriction(float friction);

    /**
     * Returns the restitution ("bounciness") of this collider. Usually
     * between 0 and 1.
     *
     * @return The restitution of this collider
     */
    public abstract float getRestitution();

    /**
     * Sets the restitution ("bounciness") of this collider. Usually
     * between 0 and 1.
     *
     * @param restitution The restitution to set
     */
    public abstract void setRestitution(float restitution);

    /**
     * Calculates and returns a raycast against this collider.
     *
     * @param p The ray origin
     * @param d The ray direction
     * @param maxDLength The maximum length of the ray, measured in
     *                   lengths of {@code d}
     * @return The raycast result
     * @deprecated Does not seem to work. Use {@link com.github.rccookie.geometry.performance.Raycast}
     *             instead
     */
    @Deprecated
    public abstract Raycast raycast(float2 p, float2 d, float maxDLength);

    /**
     * Calculates and returns an infinite raycast against this collider.
     *
     * @param p The ray origin
     * @param d The ray direction
     * @return The raycast result
     * @deprecated Does not seem to work. Use {@link com.github.rccookie.geometry.performance.Raycast}
     *             instead
     */
    @Deprecated
    public Raycast raycast(float2 p, float2 d) {
        return raycast(p, d, 1000);
    }

    /**
     * Returns <b>a view</b> of all colliders that the collider intersects with.
     * The set will be updated automatically.
     * <p>There updates will only happen in the physics update. Thus, the returned
     * set will <b>always be empty</b> at first. Also, moving an object around will
     * not immediately be represented in the set but only after the next physics
     * update.</p>
     * <p>Subsequent calls to this method will always return the same instance and
     * will not cause a recalculation. They will also <b>not</b> update the set to
     * the exact state that is currently present.</p>
     *
     * @return A view of the colliders that this collider intersects
     */
    public Set<Collider> getColliding() {
        if(colliding != null) return colliding;
        Set<Collider> mutColliding = new HashSet<>();
        onCollisionEnter.add(mutColliding::add);
        onCollisionExit.add(mutColliding::remove);
        return colliding = Utils.view(mutColliding);
    }

    /**
     * Clears the used fixture. Internal method.
     */
    protected abstract void clearFixture();

    /**
     * Generate a fixture for the given body. Internal method.
     *
     * @param body The body to attach to
     */
    protected abstract void generateFixture(Body body);
}
