package org.jbox2d.callbacks;

import com.github.rccookie.geometry.performance.float2;

public interface ParticleRaycastCallback {
  /**
   * Called for each particle found in the query. See
   * {@link RaycastCallback#reportFixture(org.jbox2d.dynamics.Fixture, float2, float2, float)} for
   * argument info.
   * 
   * @param index
   * @param point
   * @param normal
   * @param fraction
   * @return
   */
  float reportParticle(int index, float2 point, float2 normal, float fraction);

}
