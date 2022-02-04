package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.rccookie.engine2d.physics.Raycast;
import com.github.rccookie.engine2d.physics.RaycastFilter;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.Arguments;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.RaycastCallback;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

public class Map {

    final List<GameObject> objects = new ArrayList<>();
    final List<GameObject> paintOrderObjects = new ArrayList<>();

    final World physicsWorld = new World(new Vec2());

    long updateDuration = 0;
    long physicsDuration = 0;


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

    public void update() {
        long start = System.nanoTime();
        for(int i=0; i<objects.size(); i++)
            objects.get(i).update.invoke();
        long updateDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for(GameObject o : objects) o.preparePhysicsUpdate();
        physicsWorld.step(Time.delta(), 6, 2);
        for(GameObject o : objects) o.processPhysicsUpdate();
        physicsDuration = System.nanoTime() - start;

        start = System.nanoTime();
        for(int i=0; i<objects.size(); i++)
            objects.get(i).lateUpdate.invoke();
        updateDuration += System.nanoTime() - start;
        this.updateDuration = updateDuration;
    }



    public void setGravity(Vec2 gravity) {
        physicsWorld.setGravity(new Vec2(gravity));
    }

    public Vec2 getGravity() {
        return physicsWorld.getGravity();
    }

    public boolean isAllowPhysicsSleep() {
        return physicsWorld.isAllowSleep();
    }

    public void setAllowPhysicsSleep(boolean flag) {
        physicsWorld.setAllowSleep(flag);
    }



    public Collection<GameObject> getObjects() {
        return Collections.unmodifiableCollection(objects);
    }

    public int getObjectCount() {
        return objects.size();
    }



    public Raycast raycast(Vec2 p, Vec2 d, float maxDLength, RaycastFilter filter) {
        var callback = new ClosestHitRaycastCallback(Arguments.checkNull(filter), maxDLength);
        physicsWorld.raycast(callback, p, d.scaled(maxDLength).add(p));
        return callback.getResult();
    }

    public <T> Raycast raycast(Vec2 p, Vec2 d, float maxDLength, Class<T> type) {
        return raycast(p, d, maxDLength, (c, $0, $1) -> type.isInstance(c));
    }



    public Set<Raycast> raycastAll(Vec2 p, Vec2 d, float maxDLength, RaycastFilter filter) {
        var callback = new AllHitsRaycastCallback(Arguments.checkNull(filter));
        physicsWorld.raycast(callback, p, d.scaled(maxDLength).add(p));
        return callback.getResult();
    }

    public <T> Set<Raycast> raycastAll(Vec2 p, Vec2 d, float maxDLength, Class<T> type) {
        return raycastAll(p, d, maxDLength, (c, $0, $1) -> type.isInstance(c));
    }



    private static class ClosestHitRaycastCallback implements RaycastCallback {

        private final RaycastFilter filter;
        private float closest;
        private Collider collider = null;
        private Vec2 point = null;
        private Vec2 normal = null;

        ClosestHitRaycastCallback(RaycastFilter filter, float maxFraction) {
            this.filter = filter;
            closest = maxFraction + 1;
        }

        @Override
        public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
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
        public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
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
