package org.jbox2d.callbacks;

import org.jbox2d.particle.ParticleGroup;

/**
 * <p>ParticleDestructionListener interface.</p>
 *
 */
public interface ParticleDestructionListener {
  /**
   * Called when any particle group is about to be destroyed.
   *
   * @param group a {@link org.jbox2d.particle.ParticleGroup} object
   */
  void sayGoodbye(ParticleGroup group);

  /**
   * Called when a particle is about to be destroyed. The index can be used in conjunction with
   * {@link org.jbox2d.dynamics.World#getParticleUserDataBuffer} to determine which particle has been destroyed.
   *
   * @param index a int
   */
  void sayGoodbye(int index);
}
