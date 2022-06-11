package org.jbox2d.callbacks;

import com.github.rccookie.geometry.performance.float2;

/**
 * <p>ParticleRaycastCallback interface.</p>
 *
 */
public interface ParticleRaycastCallback {
  /**
   * Called for each particle found in the query. See
   * {@link org.jbox2d.callbacks.RaycastCallback#reportFixture(org.jbox2d.dynamics.Fixture, float2, float2, float)} for
   * argument info.
   *
   * @param index a int
   * @param point a {@link com.github.rccookie.geometry.performance.float2} object
   * @param normal a {@link com.github.rccookie.geometry.performance.float2} object
   * @param fraction a float
   * @return a float
   */
  float reportParticle(int index, float2 point, float2 normal, float fraction);

}
