package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Component;
import com.github.rccookie.engine2d.GameObject;
import com.github.rccookie.engine2d.Input;
import com.github.rccookie.geometry.performance.float2;

/**
 * A simple controller for a player gameobject.
 */
public class SimplePlayerController extends Component {

    /**
     * Movement speed.
     */
    private float speed = 100;
    /**
     * Turning rotation speed.
     */
    private float turningSpeed = 135;
    /**
     * Speed increment factor when shift is pressed.
     */
    private float boostFactor = 1.5f;


    /**
     * The velocity added in the last frame.
     */
    private final float2 lastAdded = new float2();
    /**
     * The rotational velocity added in the last frame
     */
    private float lastAddedRot = 0;


    /**
     * Creates a new simple player controller for the given gameobject.
     *
     * @param gameObject The gameobject to attach to
     */
    public SimplePlayerController(GameObject gameObject) {
        super(gameObject);
        update.add(this::update);
    }


    /**
     * Moves the gameobject if needed.
     */
    private void update() {
        float2 newVel = gameObject.velocity.subed(lastAdded);
        if(newVel.dot(lastAdded) > 0) // Braking would negate direction
            gameObject.velocity.setZero();
        else gameObject.velocity.sub(lastAdded);

        float newRot = gameObject.rotation - lastAddedRot;
        if(Math.signum(newRot) != Math.signum(gameObject.rotation))
            gameObject.rotation = 0;
        else gameObject.rotation -= lastAddedRot;

        lastAdded.setZero();
        if(Input.getKeyState("w") || Input.getKeyState("up")) lastAdded.sub(float2.angled(gameObject.angle + 90, speed * (Input.getKeyState("shift") ? boostFactor : 1)));
        if(Input.getKeyState("s") || Input.getKeyState("down")) lastAdded.add(float2.angled(gameObject.angle + 90, speed));
        if(Input.getKeyState("d") || Input.getKeyState("right")) lastAdded.add(float2.angled(gameObject.angle, speed));
        if(Input.getKeyState("a") || Input.getKeyState("left")) lastAdded.sub(float2.angled(gameObject.angle, speed));
        gameObject.velocity.add(lastAdded);

        lastAddedRot = 0;
        if(Input.getKeyState("e")) lastAddedRot += turningSpeed;
        if(Input.getKeyState("q")) lastAddedRot -= turningSpeed;
        gameObject.rotation += lastAddedRot;
    }


    /**
     * Returns the movement speed when moving.
     *
     * @return The movement speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Returns the speed increment factor when shift is pressed.
     *
     * @return The boost factor
     */
    public float getBoostFactor() {
        return boostFactor;
    }

    /**
     * Returns the turning rotation speed when turning.
     *
     * @return The turning speed
     */
    public float getTurningSpeed() {
        return turningSpeed;
    }

    /**
     * Sets the movement speed.
     *
     * @param speed The speed to set
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Sets the speed increment factor when shift is pressed.
     *
     * @param boostFactor The boost factor to set
     */
    public void setBoostFactor(float boostFactor) {
        this.boostFactor = boostFactor;
    }

    /**
     * Sets the turning rotation speed.
     *
     * @param turningSpeed The speed to set
     */
    public void setTurningSpeed(float turningSpeed) {
        this.turningSpeed = turningSpeed;
    }
}
