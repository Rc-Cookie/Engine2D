package org.jbox2d.particle;

import com.github.rccookie.geometry.performance.float2;

/**
 * <p>ParticleDef class.</p>
 *
 */
public class ParticleDef {
  /**
   * Specifies the type of particle. A particle may be more than one type. Multiple types are
   * chained by logical sums, for example: pd.flags = ParticleType.b2_elasticParticle |
   * ParticleType.b2_viscousParticle.
   */
  int flags;

  /** The world position of the particle. */
  public final float2 position = new float2();

  /** The linear velocity of the particle in world co-ordinates. */
  public final float2 velocity = new float2();

  /** The color of the particle. */
  public ParticleColor color;

  /** Use this to store application-specific body data. */
  public Object userData;
}
