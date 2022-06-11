package org.jbox2d.callbacks;

import org.jbox2d.dynamics.World;

/**
 * Callback class for AABB queries. See
 * {@link org.jbox2d.dynamics.World#queryAABB(QueryCallback, org.jbox2d.collision.AABB)}.
 *
 * @author dmurph
 * @version $Id: $Id
 */
public interface ParticleQueryCallback {
  /**
   * Called for each particle found in the query AABB.
   *
   * @return false to terminate the query.
   * @param index a int
   */
  boolean reportParticle(int index);
}
