package com.github.rccookie.engine2d.components;

import com.github.rccookie.engine2d.Component;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.engine2d.Map;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A component that makes the gameobject follow another gameobject, optional
 * with an offset.
 */
public class Follower extends Component {

    /**
     * The gameobject to follow.
     */
    @NotNull
    private GameObject target;

    /**
     * Location offset to the followed object.
     */
    private final float2 offset = new float2();
    /**
     * Angle offset to the followed object.
     */
    private float angleOffset = 0;
    /**
     * Should the location be followed?
     */
    private boolean followLocation = true;
    /**
     * Should the angle be followed?
     */
    private boolean followAngle = true;
    /**
     * Should the offset be relative to the target object's rotation?
     */
    private boolean angledOffset = false;

    /**
     * The action to be called when the target object changes
     * map.
     */
    private final ParamAction<Map> mapChangeListener;

    /**
     * Creates a new component attached to the given gameobject.
     *
     * @param gameObject The gameobject to attach to
     */
    public Follower(@NotNull GameObject gameObject, @NotNull GameObject target) {
        super(gameObject);
        this.target = Arguments.checkNull(target, "target");
        mapChangeListener = gameObject::setMap;
        target.onMapChange.add(mapChangeListener);
        gameObject.setMap(target.getMap());
        lateUpdate.add(this::update);
    }

    /**
     * Returns the object that the follower follows.
     *
     * @return The followed object
     */
    @NotNull
    public GameObject getTarget() {
        return target;
    }

    /**
     * Sets the object to be followed.
     *
     * @param target The object to be followed
     */
    public void setTarget(@NotNull GameObject target) {
        this.target.onMapChange.remove(mapChangeListener);
        this.target = Arguments.checkNull(target, "target");
        target.onMapChange.add(mapChangeListener);
    }

    /**
     * Returns the following offset to the target object's location. Depending on
     * what was set using {@link #isAngledOffset()} the offset may be relative
     * to the target's rotation, or absolute.
     *
     * @return The current offset
     */
    public float2 getOffset() {
        return offset.clone();
    }

    /**
     * Returns the angle offset to the followed object.
     *
     * @return The current angle offset
     */
    public float getAngleOffset() {
        return angleOffset;
    }

    /**
     * Returns whether the location should be followed.
     *
     * @return Whether the location is being followed
     */
    public boolean isFollowLocation() {
        return followLocation;
    }

    /**
     * Returns whether the angle should be followed.
     *
     * @return Whether the angle is being followed
     */
    public boolean isFollowAngle() {
        return followAngle;
    }

    /**
     * Returns whether the offset is relative to the followed object's
     * angle, or absolute. Off by default.
     *
     * @return Whether the offset is relative to the target's angle
     */
    public boolean isAngledOffset() {
        return angledOffset;
    }

    /**
     * Sets the following offset to the target object's location. Depending on
     * what was set using {@link #isAngledOffset()} the offset may be relative
     * to the target's rotation, or absolute.
     *
     * @param offset The offset to set
     */
    public void setOffset(float2 offset) {
        this.offset.set(offset);
    }

    /**
     * Sets the following angle offset.
     *
     * @param angleOffset The offset to the target object's angle to use
     */
    public void setAngleOffset(float angleOffset) {
        this.angleOffset = angleOffset;
    }

    /**
     * Sets whether the location should be followed. On by default.
     *
     * @param followLocation Whether to follow the location
     */
    public void setFollowLocation(boolean followLocation) {
        this.followLocation = followLocation;
    }

    /**
     * Sets whether the angle should be followed. On by default.
     *
     * @param followAngle Whether to follow the angle
     */
    public void setFollowAngle(boolean followAngle) {
        this.followAngle = followAngle;
    }

    /**
     * Sets whether the offset should be interpreted as relative to the
     * target object's angle, or absolute. Off by default.
     *
     * @param angledOffset Whether the offset should be relative to the target's angle
     */
    public void setAngledOffset(boolean angledOffset) {
        this.angledOffset = angledOffset;
    }

    /**
     * Follows the target object.
     */
    private void update() {
        if(followAngle)
            gameObject.angle = target.angle + angleOffset;
        if(followLocation) {
            gameObject.location.set(target.location);
            if(angledOffset && !offset.isZero())
                gameObject.location.add(offset.rotated(target.angle));
            else gameObject.location.add(offset);
        }
    }
}
