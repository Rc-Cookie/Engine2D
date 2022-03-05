package org.jbox2d.particle;

import com.github.rccookie.geometry.performance.float2;

public class ParticleContact {
  /** Indices of the respective particles making contact. */
  public int indexA, indexB;
  /** The logical sum of the particle behaviors that have been set. */
  public int flags;
  /** Weight of the contact. A value between 0.0f and 1.0f. */
  public float weight;
  /** The normalized direction from A to B. */
  public final float2 normal = new float2();
}
