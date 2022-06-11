package com.github.rccookie.engine2d;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.physics.BoxCollider;
import com.github.rccookie.engine2d.util.Convert;
import com.github.rccookie.engine2d.util.NamedLazyEvent;
import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.LazyEvent;
import com.github.rccookie.event.action.Action;
import com.github.rccookie.event.action.IAction;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.util.ModIterableArrayList;
import com.github.rccookie.util.Utils;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A gameobject is the base class for any object on a map. A gameobject has
 * a position and rotation, and an image. Additionally, components can be attached
 * to a gameobject. Gameobjects also support build-in physics simulation.
 */
public class GameObject {

    /**
     * The location of the gameobject on the map. May be modified.
     */
    public final float2 location = new float2();
    /**
     * The angle of the gameobject on the map. May be modified.
     */
    public float angle = 0;
    /**
     * The current velocity of the gameobject. May be modified.
     */
    public final float2 velocity = new float2();
    /**
     * The current angular velocity of the gameobject. May be modified.
     */
    public float rotation = 0;


    /**
     * The gameobject's image.
     */
    private Image image;

    /**
     * The map the gameobject is on.
     */
    Map map = null;

    /**
     * Underlying physics body.
     */// TODO: Move all physics into components
    Body body;
    /**
     * Body definition to create a new body when physics are
     * enabled.
     */
    private final BodyDef bodyData;


    /**
     * Count how many of the by update/lateUpdate invoked events are used. If none,
     * the respective event will be temporarily unregistered from the map update.
     */
    private int updateUses = 0, lateUpdateUses = 0;
    /**
     * Early update event for components to attach to.
     */
    final Event componentEarlyUpdate = new LazyEvent(this::incUpdateUse, this::decUpdateUse);
    /**
     * Update event for components to attach to.
     */
    final Event componentUpdate = new LazyEvent(this::incUpdateUse, this::decUpdateUse);
    /**
     * Late update event for components to attach to.
     */
    final Event componentLateUpdate = new LazyEvent(this::incLateUpdateUse, this::decLateUpdateUse);


    /**
     * Executed once per frame after the {@link Component#earlyUpdate} and before
     * the {@link Component#update} event and the physics update, if enabled.
     */
    public final Event update = new NamedLazyEvent(this::incUpdateUse, this::decUpdateUse, false, () -> "Gameobject.update on " + this) {
        @Override
        public boolean invoke() {
            componentEarlyUpdate.invoke();
            super.invoke();
            componentUpdate.invoke();
            return false;
        }
    };

    /**
     * Executed once per frame after physics update and {@link Component#update} event,
     * but before the {@link Component#lateUpdate} event.
     */
    public final Event lateUpdate = new NamedLazyEvent(this::incLateUpdateUse, this::decLateUpdateUse, false, () -> "GameObject.lateUpdate on " + this) {
        @Override
        public boolean invoke() {
            super.invoke();
            componentLateUpdate.invoke();
            return false;
        }
    };

    /**
     * The {@code update.invoke()} / {@code lateUpdate.invoke()} methods as {@link Action}.
     */
    private final Action updateAction = update::invoke, lateUpdateAction = lateUpdate::invoke;

    /**
     * Executed when the gameobject changes map, with the old and new map as parameters.
     */
    public final BiParamEvent<Map,Map> onMapChange = new CaughtBiParamEvent<>(false);

    /**
     * Local input manager only active when the gameobject is on the active map.
     */
    public final LocalInputManager input = new LocalInputManager.Impl(update, this::isOnActiveMap);

    /**
     * Local execution manager only active when the gameobject is on the active map.
     */
    public final LocalExecutionManager execute = new LocalExecutionManager(this::isOnActiveMap);


    /**
     * Components attached to this gameobject.
     */
    final List<Component> components = new ModIterableArrayList<>();

    /**
     * Subset of the components, the collides attached to this gameobject.
     */
    final Set<Collider> colliders = new HashSet<>();

    /**
     * Whether to use the image as collider.
     */
    private boolean useImageCollider = true;

    /**
     * The collider representing the image.
     */
    private BoxCollider imageCollider = null;


    /**
     * The action responsible for calling {@link #update}. Used to unregister
     * that call if the update method is found not to be overridden.
     */
    private IAction localUpdateAction;


    /**
     * Creates a new gameobject with no image.
     */
    public GameObject() {
        Application.checkSetup();
        localUpdateAction = update.add(this::update);
        bodyData = new BodyDef();
        bodyData.type = BodyType.KINEMATIC;
        bodyData.linearDamping = 0.2f;
        bodyData.angularDamping = 0.2f;
        bodyData.fixedRotation = false;
    }



    private void incUpdateUse() {
        if(updateUses++ == 0 && map != null)
            map.gameobjectUpdate.add(updateAction);
    }

    private void decUpdateUse() {
        if(--updateUses == 0 && map != null)
            map.gameobjectUpdate.remove(updateAction);
    }

    private void incLateUpdateUse() {
        if(lateUpdateUses++ == 0 && map != null)
            map.gameobjectLateUpdate.add(lateUpdateAction);
    }

    private void decLateUpdateUse() {
        if(--lateUpdateUses == 0 && map != null)
            map.gameobjectLateUpdate.remove(lateUpdateAction);
    }



    /**
     * Tests whether this gameobject is on a map and the camera is
     * rendering that map.
     *
     * @return Whether this gameobject is on the currently active map
     */
    private boolean isOnActiveMap() {
        Map map = GameObject.this.map;
        if(map == null) return false;
        GameObject cameraObject = Camera.getActive().getGameObject();
        return cameraObject != null && cameraObject.map == map;
    }



    /**
     * Returns the map the gameobject is on. May be {@code null}.
     *
     * @return The gameobject's map
     */
    public Map getMap() {
        return map;
    }

    /**
     * Returns the map the gameobject is on, cast to the given type.
     *
     * @param type The type to cast to
     * @return The gameobject's map as that type
     */
    public <M> M getMap(Class<M> type) {
        return type.cast(map);
    }

    /**
     * Sets the gameobject to be on the specified map. Setting {@code null}
     * will remove the gameobject from any map. Setting the map the gameobject
     * is already on has no effect and no events will be fired.
     *
     * @param map The map to set
     */
    public void setMap(@Nullable Map map) {
        if(this.map == map) return;
        if(this.map != null) {
            this.map.physicsWorld.destroyBody(body);
            body = null;
            for(var c : colliders) c.clearFixture();
            this.map.objects.remove(this);
            this.map.paintOrderObjects.remove(this);
            if(updateUses != 0)
                this.map.gameobjectUpdate.remove(updateAction);
            if(lateUpdateUses != 0)
                this.map.gameobjectLateUpdate.remove(lateUpdateAction);
        }
        Map old = this.map;
        this.map = map;
        if(map != null) {
            map.objects.add(this);
            map.paintOrderObjects.add(this);
            body = map.physicsWorld.createBody(bodyData);
            for(var c : colliders) c.generateFixture(body);
            if(updateUses != 0)
                this.map.gameobjectUpdate.add(updateAction);
            if(lateUpdateUses != 0)
                this.map.gameobjectLateUpdate.add(lateUpdateAction);
        }
        onMapChange.invoke(old, map);
    }

    /**
     * Removes this gameobject from any map, if it was on one. This is equivalent
     * to calling {@code setMap(null)}.
     *
     * @return {@code true} if the gameobject was on a map, {@code false} otherwise
     */
    public boolean remove() {
        boolean wasOnMap = map != null;
        setMap(null);
        return wasOnMap;
    }

    /**
     * Returns a new vector of length 1 in the direction the gameobject is currently facing.
     *
     * @return A vector facing the gameobjects direction
     */
    @NotNull
    public float2 direction() {
        return float2.angled(angle);
    }


    /**
     * Returns the gameobject's image. May be {@code null}.
     *
     * @return The gameobject's image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Sets the gameobject's image. A value of {@code null} will make the gameobject
     * invisible.
     *
     * @param image The image to set
     */
    public void setImage(@Nullable Image image) {
        this.image = image;
        if(useImageCollider) {
            if(image == null) {
                removeComponent(imageCollider);
                imageCollider = null;
            }
            else {
                if(imageCollider != null)
                    imageCollider.setSize(image.size.toF());
                else imageCollider = new BoxCollider(this, image.size.toF());
            }
        }
    }

    /**
     * Returns all components attached to the gameobject that are of the specified
     * type.
     *
     * @param type The type to search for
     * @return All attached components of the given type
     */
    public <C> Set<C> getComponents(Class<C> type) {
        return components.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    /**
     * Finds and returns a component of the specified type, or {@code null} if
     * no such component exists.
     *
     * @param type The type to search for
     * @return A component of that type
     */
    public <C> C getComponent(Class<C> type) {
        for(var c : components)
            if(type.isInstance(c)) return type.cast(c);
        return null;
    }

    /**
     * Removes the given component from this gameobject.
     *
     * @param component The component to remove
     * @return Whether the component was attached before
     */
    public boolean removeComponent(Component component) {
        if(component.gameObject != this)
            throw new IllegalArgumentException("The component is not related to this gameobject");
        boolean out = components.remove(component);
        if(out && component instanceof Collider)
            colliders.remove(component);
        return out;
    }

    /**
     * Removes a component of the given type of this gameobject, if any
     * is present.
     *
     * @param type The type of component to remove
     * @return The removed component, or {@code null} if none was present
     */
    public <C> C removeComponent(Class<C> type) {
        C out = getComponent(type);
        if(out != null) removeComponent((Component) out);
        return out;
    }

    /**
     * Returns whether this gameobject uses physics.
     *
     * @return Whether physics are enabled for this gameobject
     */
    public boolean usingPhysics() {
        return bodyData.type == BodyType.DYNAMIC;
    }

    /**
     * Enables or disables physics simulation for this gameobject. Physics
     * are disabled by default.
     *
     * @param usePhysics Whether to use physics
     */
    public void usePhysics(boolean usePhysics) {
        bodyData.type = usePhysics ? BodyType.DYNAMIC : BodyType.KINEMATIC;
        if(body != null)
            body.setType(bodyData.type);
    }

    /**
     * Whether this gameobject has a collider for its image.
     *
     * @return Whether the image is used as collider
     */
    public boolean usingImageCollider() {
        return useImageCollider;
    }

    /**
     * Sets whether the gameobject's image should be used as a collider.
     *
     * @param flag Whether to use the image as collider or not
     */
    public void useImageCollider(boolean flag) {
        if(useImageCollider == flag) return;
        useImageCollider = flag;
        if(flag && image != null)
            imageCollider = new BoxCollider(this, image.size.toF());
        else if(!flag && image != null) {
            removeComponent(imageCollider);
            imageCollider = null;
        }
    }

    /**
     * Returns the friction of the gameobject with the background.
     *
     * @return The current background friction
     */
    public float getBackgroundFriction() {
        return body.m_linearDamping;
    }

    /**
     * Sets the background friction of the gameobject.
     *
     * @param friction The friction to set
     */
    public void setBackgroundFriction(float friction) {
        // TODO
        bodyData.linearDamping = friction;
        bodyData.angularDamping = friction;
        if(body != null) {
            body.setLinearDamping(friction);
            body.setAngularDamping(friction);
        }
    }

    /**
     * Prepares the physics update by updating the physics body's parameters to
     * the currently set ones.
     */
    void preparePhysicsUpdate() {
        bodyData.position         = location.scaled(Convert.PIXELS_TO_UNITS);
        bodyData.linearVelocity   = velocity.scaled(Convert.PIXELS_TO_UNITS);
        bodyData.angle            = angle         * Convert.TO_RADIANS;
        bodyData.angularVelocity  = rotation      * Convert.TO_RADIANS;
//        bodyData.position.y       = -bodyData.position.y;
//        bodyData.linearVelocity.y = -bodyData.position.y;

        assert body != null;

        if(bodyData.angle != body.m_sweep.a || !bodyData.position.equals(body.m_xf.p))
            body.setTransform(bodyData.position, bodyData.angle);
        body.setLinearVelocity(bodyData.linearVelocity);
        body.setAngularVelocity(bodyData.angularVelocity);
    }

    /**
     * Evaluate the physics update by updating the gameobject's transform according
     * to the physics body's one.
     */
    void processPhysicsUpdate() {
        assert body != null;

        bodyData.position.set(body.m_xf.p);
        bodyData.linearVelocity.set(body.m_linearVelocity);
        bodyData.angle = body.m_sweep.a;
        bodyData.angularVelocity = body.m_angularVelocity;

        location.set(bodyData.position)      .scale(Convert.UNITS_TO_PIXELS);
        velocity.set(bodyData.linearVelocity).scale(Convert.UNITS_TO_PIXELS);
        angle =      bodyData.angle               * Convert.TO_DEGREES;
        rotation =   bodyData.angularVelocity     * Convert.TO_DEGREES;
//        location.y = -location.y;
//        velocity.y = -velocity.y;
    }


    /**
     * Called once per frame. Intended to be overridden. Default implementation does nothing.
     */
    protected void update() {
        // If this code is reached the method does nothing, so we may as well unregister it
        update.remove(localUpdateAction);
        localUpdateAction = null;
    }


    /**
     * Returns a stream containing only objects at the same location as the gameobject of
     * the given type.
     *
     * @param type The type to search for
     * @return A stream of objects on this position
     */
    public <T> Stream<T> findOnLoc(Class<T> type) {
        return getMap().objects(location, type).filter(o -> o != this);
    }

    /**
     * Returns a stream containing only objects in the given range from this gameobject's
     * center and of the given type.
     *
     * @param maxDist Maximum distance for objects to find (inclusive)
     * @param type Type of objects to find
     * @return A stream of objects found
     */
    public <T> Stream<T> findInRange(float maxDist, Class<T> type) {
        return getMap().objects(location, maxDist, type).filter(o -> o != this);
    }

    /**
     * Returns a stream containing only objects that are within the given range. If diagonal is
     * {@code false}, the distance will be calculated as manhattan norm (x dist + y dist).
     * If it is {@code true}, the distance will be computed as max-norm (max(x dist, y dist) ).
     *
     * @param maxDist The maximum distance for objects to find (inclusive)
     * @param diagonal Whether to consider diagonal steps as a single unit distance
     * @param type The type of objects to find
     * @return A stream of objects found
     */
    public <T> Stream<T> findAdjacent(float maxDist, boolean diagonal, Class<T> type) {
        return Utils.filterType(getMap().objects().filter(diagonal ?
                o -> o != this && float2.maxDist(location, o.location) <= maxDist :
                o -> o != this && float2.manhattanDist(location, o.location) <= maxDist), type);
    }


    /**
     * Returns the class name of this gameobject, and it's location, as string.
     *
     * @return A string representation of this object
     */
    @Override
    public String toString() {
        String className = "GameObject";
        try {
            Class<?> cls = getClass();
            do className = cls.getSimpleName();
            while(className.isEmpty());
        } catch(Exception ignored) { }
        return className + " at " + location;
    }
}
