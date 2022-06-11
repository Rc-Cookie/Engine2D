package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.github.rccookie.engine2d.physics.Raycast;
import com.github.rccookie.engine2d.physics.RaycastFilter;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.SimpleEvent;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.ModIterableArrayList;
import com.github.rccookie.util.Utils;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.RaycastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jetbrains.annotations.NotNull;

/**
 * The map represents a container for gameobjects. It also controls
 * global physics settings.
 */
public class Map {

    /**
     * Objects on this map.
     */
    final List<GameObject> objects = new ModIterableArrayList<>();

    /**
     * View of {@link #objects}.
     */
    private final List<GameObject> objectsView = Collections.unmodifiableList(objects);

    /**
     * Objects in paint order, from back to top.
     */
    final List<GameObject> paintOrderObjects = new ArrayList<>();


    /**
     * Physics world of Box2D.
     */
    final World physicsWorld = new World(new float2());


    /**
     * Performance stats.
     */
    long updateDuration = 0, physicsDuration = 0;


    /**
     * Called once per frame before any gameobject gets updated.
     */
    public final Event earlyUpdate = new NamedCaughtEvent(false, "Map.earlyUpdate");

    /**
     * Called once per frame after all gameobjects have been updated, but
     * before the physics update and late update.
     */
    public final Event update = new NamedCaughtEvent(false, "Map.update");

    /**
     * Called once per frame after all other late updates of gameobjects.
     */
    public final Event lateUpdate = new NamedCaughtEvent(false, "Map.lateUpdate");

    final Event gameobjectUpdate = new SimpleEvent();
    final Event gameobjectLateUpdate = new SimpleEvent();



    /**
     * Creates a new map.
     */
    public Map() {
        Application.checkSetup();
        physicsWorld.setAutoClearForces(true);
        physicsWorld.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Collider a = (Collider) contact.m_fixtureA.m_userData;
                Collider b = (Collider) contact.m_fixtureB.m_userData;
                Execute.later(() -> {
                    a.onCollisionEnter.invoke(b);
                    b.onCollisionEnter.invoke(a);
                });
            }

            @Override
            public void endContact(Contact contact) {
                Collider a = (Collider) contact.m_fixtureA.m_userData;
                Collider b = (Collider) contact.m_fixtureB.m_userData;
                Execute.later(() -> {
                    a.onCollisionExit.invoke(b);
                    b.onCollisionExit.invoke(a);
                });
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) { }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) { }
        });
    }


    /**
     * Invokes update events for the gameobjects and the map itself.
     */
    void update() {
        long start = System.nanoTime();
        this.earlyUpdate.invoke();
//        for(int i=0; i<objects.size(); i++)
//            objects.get(i).update.invoke();
        this.gameobjectUpdate.invoke();
        this.update.invoke();
        long updateDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for(GameObject o : objects) o.preparePhysicsUpdate();
        physicsWorld.step(Time.delta(), 6, 2);
        for(GameObject o : objects) o.processPhysicsUpdate();
        physicsDuration = System.nanoTime() - start;

        start = System.nanoTime();
//        for(int i=0; i<objects.size(); i++)
//            objects.get(i).lateUpdate.invoke();
        this.gameobjectLateUpdate.invoke();
        this.lateUpdate.invoke();
        updateDuration += System.nanoTime() - start;
        this.updateDuration = updateDuration;

//        Console.log(gameobjectUpdate.getActions().size(), "/", gameobjectLateUpdate.getActions().size(), "of", objects.size(), "update events connected");
    }



    /**
     * Sets the strength and direction of gravity on gameobject with physics enabled.
     *
     * @param gravity The gravity to set
     */
    public void setGravity(@NotNull float2 gravity) {
        physicsWorld.setGravity(gravity.clone());
    }

    /**
     * Returns the gravity used on this map.
     *
     * @return The map's gravity
     */
    @NotNull
    public float2 getGravity() {
        return physicsWorld.getGravity();
    }

    /**
     * Returns whether physics sleep is allowed for performance reasons.
     *
     * @return Whether physics sleep is allowed
     */
    public boolean isAllowPhysicsSleep() {
        return physicsWorld.isAllowSleep();
    }

    /**
     * Sets whether physics sleep is allowed for performance reasons. This may cause
     * some unexpected behavior on still standing objects.
     *
     * @param flag Whether to allow physics sleep or not
     */
    public void setAllowPhysicsSleep(boolean flag) {
        physicsWorld.setAllowSleep(flag);
    }


    /**
     * Returns a view on the objects on this map.
     *
     * @return The objects on this map
     */
    public Collection<GameObject> getObjects() {
        return objectsView;
    }

    /**
     * Returns the number of objects on this map.
     *
     * @return The number of objects
     */
    public int getObjectCount() {
        return objects.size();
    }



    /**
     * Returns a stream over the objects on this map
     *
     * @return The objects on this map
     */
    public Stream<GameObject> objects() {
        return objects.stream();
    }

    /**
     * Returns a stream over the objects of the specified type on this map.
     *
     * @param type The type of objects to find
     * @return A stream over those objects
     */
    public <T> Stream<T> objects(Class<T> type) {
        return Utils.filterType(objects(), type);
    }

    /**
     * Returns a stream of objects on the specified location.
     *
     * @param pos The location to find objects at
     * @param type The type of objects to find
     * @return A stream over those objects
     */
    public <T> Stream<T> objects(float2 pos, Class<T> type) {
        return Utils.filterType(objects().filter(o -> o.location.equals(pos)), type);
    }

    /**
     * Returns a stream over objects in range of the specified location.
     *
     * @param pos The location for the range to start in
     * @param maxDist The maximum distance from pos to find objects in (inclusive)
     * @param type The type of objects to find
     * @return A stream over those objects
     */
    public <T> Stream<T> objects(float2 pos, float maxDist, Class<T> type) {
        float maxSqrDist = maxDist * maxDist;
        return Utils.filterType(objects().filter(o -> float2.sqrDist(o.location, pos) <= maxSqrDist), type);
    }




    /**
     * Calculates and returns a raycast over all objects that match the filter.
     *
     * @param p The ray origin
     * @param d The ray direction
     * @param maxDLength The maximum length of the ray, in multiples of d
     * @param filter Filter for objects to find
     * @return The raycast result
     * @deprecated Does not seem to work. Use {@link com.github.rccookie.geometry.performance.Raycast}
     *             instead.
     */
    @Deprecated
    public Raycast raycast(float2 p, float2 d, float maxDLength, RaycastFilter filter) {
        var callback = new ClosestHitRaycastCallback(Arguments.checkNull(filter), maxDLength);
        physicsWorld.raycast(callback, p, d.scaled(maxDLength).add(p));
        return callback.getResult();
    }

    /**
     * Calculates and returns a raycast over all objects of the given type.
     *
     * @param p The ray origin
     * @param d The ray direction
     * @param maxDLength The maximum length of the ray, in multiples of d
     * @param type The type of objects to raycast
     * @return The raycast result
     * @deprecated Does not seem to work. Use {@link com.github.rccookie.geometry.performance.Raycast}
     *             instead.
     */
    public <T> Raycast raycast(float2 p, float2 d, float maxDLength, Class<T> type) {
        return raycast(p, d, maxDLength, (c, $0, $1) -> type.isInstance(c));
    }



    @Deprecated
    public Set<Raycast> raycastAll(float2 p, float2 d, float maxDLength, RaycastFilter filter) {
        var callback = new AllHitsRaycastCallback(Arguments.checkNull(filter));
        physicsWorld.raycast(callback, p, d.scaled(maxDLength).add(p));
        return callback.getResult();
    }

    @Deprecated
    public <T> Set<Raycast> raycastAll(float2 p, float2 d, float maxDLength, Class<T> type) {
        return raycastAll(p, d, maxDLength, (c, $0, $1) -> type.isInstance(c));
    }



    private static class ClosestHitRaycastCallback implements RaycastCallback {

        private final RaycastFilter filter;
        private float closest;
        private Collider collider = null;
        private float2 point = null;
        private float2 normal = null;

        ClosestHitRaycastCallback(RaycastFilter filter, float maxFraction) {
            this.filter = filter;
            closest = maxFraction + 1;
        }

        @Override
        public float reportFixture(Fixture fixture, float2 point, float2 normal, float fraction) {
            if(fraction > closest) return -1;
            Collider collider = (Collider) fixture.m_userData;
            if(!filter.isValid(collider, point, normal)) return -1;
            this.collider = collider;
            this.point = point;
            this.normal = normal;
            closest = fraction;
            return 1;
        }

        Raycast getResult() {
            return new Raycast(collider != null, point, normal, collider);
        }
    }

    private static class AllHitsRaycastCallback implements RaycastCallback {

        private final RaycastFilter filter;
        private final Set<Raycast> result = new HashSet<>();

        AllHitsRaycastCallback(RaycastFilter filter) {
            this.filter = filter;
        }

        @Override
        public float reportFixture(Fixture fixture, float2 point, float2 normal, float fraction) {
            Collider collider = (Collider) fixture.m_userData;
            if(!filter.isValid(collider, point, normal)) return -1;
            result.add(new Raycast(true, point, normal, collider));
            return 1;
        }

        Set<Raycast> getResult() {
            return result;
        }
    }
}
