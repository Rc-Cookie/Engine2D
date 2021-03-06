package org.jbox2d.particle;

/**
 * <p>ParticleGroupType class.</p>
 *
 */
public class ParticleGroupType {
  /** resists penetration */
  public static final int b2_solidParticleGroup = 1 << 0;
  /** keeps its shape */
  public static final int b2_rigidParticleGroup = 1 << 1;
}
