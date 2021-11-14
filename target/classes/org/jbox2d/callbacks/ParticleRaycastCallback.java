package org.jbox2d.callbacks;

import com.github.rccookie.geometry.performance.Vec2;

public interface ParticleRaycastCallback {
  /**
   * Called for each particle found in the query. See
   * {@link RaycastCallback#reportFixture(org.jbox2d.dynamics.Fixture, Vec2, Vec2, float)} for
   * argument info.
   * 
   * @param index
   * @param point
   * @param normal
   * @param fraction
   * @return
   */
  float reportParticle(int index, Vec2 point, Vec2 normal, float fraction);

}
