package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.physics.BoxCollider;
import com.github.rccookie.engine2d.physics.Convert;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

public class GameObject {

    public final Vec2 location = new Vec2();
    public float angle = 0;
    public final Vec2 velocity = new Vec2();
    public float rotation = 0;

    private Image image;

    Map map = null;
    Body body;
    private final BodyDef bodyData;


    public final Event update = new NamedCaughtEvent(false, () -> "Gameobject.update on " + this) {
        @Override
        public boolean invoke() {
            for(int i=0; i<components.size(); i++)
                components.get(i).earlyUpdate.invoke();
            super.invoke();
            for(int i=0; i<components.size(); i++)
                components.get(i).update.invoke();
            return false;
        }
    };
    public final Event lateUpdate = new NamedCaughtEvent(false, () -> "GameObject.lateUpdate on " + this) {
        @Override
        public boolean invoke() {
            super.invoke();
            for(int i=0; i<components.size(); i++) components.get(i).lateUpdate.invoke();
            return false;
        }
    };
    public final BiParamEvent<Map,Map> onMapChange = new CaughtBiParamEvent<>(false);

    public final LocalInputManager input = new LocalInputManager.Impl(update, this::isOnActiveMap);
    public final LocalExecutionManager execute = new LocalExecutionManager(this::isOnActiveMap);

    final List<Component> components = new ArrayList<>();
    final Set<Collider> colliders = new HashSet<>();

    private boolean useImageCollider = true;
    private BoxCollider imageCollider = null;


    public GameObject() {
        Application.checkSetup();
        update.add(this::update);
        bodyData = new BodyDef();
        bodyData.type = BodyType.KINEMATIC;
        bodyData.linearDamping = 0.2f;
        bodyData.angularDamping = 0.2f;
        bodyData.fixedRotation = false;
    }



    private boolean isOnActiveMap() {
        Map map = GameObject.this.map;
        if(map == null) return false;
        Camera camera = Camera.getActive();
        if(camera == null) return false;
        GameObject cameraObject = camera.getGameObject();
        return cameraObject != null && cameraObject.map == map;
    }


    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        if(this.map == map) return;
        if(this.map != null) {
            this.map.physicsWorld.destroyBody(body);
            body = null;
            for(var c : colliders) c.clearFixture();
            this.map.objects.remove(this);
            this.map.paintOrderObjects.remove(this);
        }
        Map old = this.map;
        this.map = map;
        if(map != null) {
            map.objects.add(this);
            map.paintOrderObjects.add(this);
            body = map.physicsWorld.createBody(bodyData);
            for(var c : colliders) c.generateFixture(body);
        }
        onMapChange.invoke(old, map);
    }

    public void remove() {
        setMap(null);
    }

    public Vec2 direction() {
        return Vec2.angled(angle);
    }


    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        if(useImageCollider) {
            if(imageCollider != null)
                imageCollider.setSize(image.size.toF());
            else imageCollider = new BoxCollider(this, image.size.toF());
        }
    }

    @SuppressWarnings("unchecked")
    public <C> Set<C> getComponents(Class<C> type) {
        return components.stream()
                .filter(type::isInstance)
                .map(c -> (C)c)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public <C> C getComponent(Class<C> type) {
        for(var c : components)
            if(type.isInstance(c)) return (C) c;
        return null;
    }

    public boolean removeComponent(Component component) {
        boolean out = components.remove(component);
        if(out && component instanceof Collider)
            colliders.remove(component);
        return out;
    }

    public <C> C removeComponent(Class<C> type) {
        C out = getComponent(type);
        if(out != null) removeComponent((Component) out);
        return out;
    }

    public boolean usingPhysics() {
        return bodyData.type == BodyType.DYNAMIC;
    }

    public void usePhysics(boolean usePhysics) {
        bodyData.type = usePhysics ? BodyType.DYNAMIC : BodyType.KINEMATIC;
        if(body != null)
            body.setType(bodyData.type);
    }

    public boolean usingImageCollider() {
        return useImageCollider;
    }

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

    public float getBackgroundFriction() {
        return body.m_linearDamping;
    }

    public void setBackgroundFriction(float friction) {
        // TODO
        bodyData.linearDamping = friction;
        bodyData.angularDamping = friction;
        if(body != null) {
            body.setLinearDamping(friction);
            body.setAngularDamping(friction);
        }
    }

    void preparePhysicsUpdate() {
        bodyData.position         = location.scaled(Convert.PIXELS_TO_UNITS);
        bodyData.linearVelocity   = velocity.scaled(Convert.PIXELS_TO_UNITS);
        bodyData.angle            = angle         * Convert.DEGREES_TO_RADIANS;
        bodyData.angularVelocity  = rotation      * Convert.DEGREES_TO_RADIANS;
//        bodyData.position.y       = -bodyData.position.y;
//        bodyData.linearVelocity.y = -bodyData.position.y;

        assert body != null;

        if(bodyData.angle != body.m_sweep.a || !bodyData.position.equals(body.m_xf.p))
            body.setTransform(bodyData.position, bodyData.angle);
        body.setLinearVelocity(bodyData.linearVelocity);
        body.setAngularVelocity(bodyData.angularVelocity);
    }

    void processPhysicsUpdate() {
        assert body != null;

        bodyData.position.set(body.m_xf.p);
        bodyData.linearVelocity.set(body.m_linearVelocity);
        bodyData.angle = body.m_sweep.a;
        bodyData.angularVelocity = body.m_angularVelocity;

        location.set(bodyData.position)      .scale(Convert.UNITS_TO_PIXELS);
        velocity.set(bodyData.linearVelocity).scale(Convert.UNITS_TO_PIXELS);
        angle =      bodyData.angle               * Convert.RADIANS_TO_DEGREES;
        rotation =   bodyData.angularVelocity     * Convert.RADIANS_TO_DEGREES;
//        location.y = -location.y;
//        velocity.y = -velocity.y;
    }

    protected void update() {

    }


    @Override
    public String toString() {
        return "GameObject at " + location;
    }
}
