package com.github.rccookie.engine2d.physics;

import com.github.rccookie.engine2d.Collider;
import com.github.rccookie.geometry.performance.float2;

import org.jetbrains.annotations.NotNull;

/**
 * A filter that filters out raycast hits that should be reported.
 */
public interface RaycastFilter {

    /**
     * Should the given raycast hit be reported?
     *
     * @param collider The collider that was hit
     * @param point The point of collision
     * @param normal The collision normal
     * @return Whether the collision should be reported
     */
    boolean isValid(@NotNull Collider collider, @NotNull float2 point, @NotNull float2 normal);
}
