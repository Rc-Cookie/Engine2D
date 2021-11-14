package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Collider;
import com.github.rccookie.geometry.performance.Vec2;

public interface RaycastFilter {

    boolean isValid(Collider collider, Vec2 point, Vec2 normal);
}
