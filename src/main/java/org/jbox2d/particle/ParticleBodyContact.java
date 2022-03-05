package org.jbox2d.particle;

import com.github.rccookie.geometry.performance.float2;
import org.jbox2d.dynamics.Body;

public class ParticleBodyContact {
  /** Index of the particle making contact. */
  public int index;
  /** The body making contact. */
  public Body body;
  /** Weight of the contact. A value between 0.0f and 1.0f. */
  float weight;
  /** The normalized direction from the particle to the body. */
  public final float2 normal = new float2();
  /** The effective mass used in calculating force. */
  float mass;
}
