package org.jbox2d.particle;

import org.jbox2d.common.Color3f;

/**
 * Small color object for each particle
 *
 * @author dmurph
 * @version $Id: $Id
 */
public class ParticleColor {
  public byte r, g, b, a;

  /**
   * <p>Constructor for ParticleColor.</p>
   */
  public ParticleColor() {
    r = (byte) 127;
    g = (byte) 127;
    b = (byte) 127;
    a = (byte) 50;
  }

  /**
   * <p>Constructor for ParticleColor.</p>
   *
   * @param r a byte
   * @param g a byte
   * @param b a byte
   * @param a a byte
   */
  public ParticleColor(byte r, byte g, byte b, byte a) {
    set(r, g, b, a);
  }

  /**
   * <p>Constructor for ParticleColor.</p>
   *
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public ParticleColor(Color3f color) {
    set(color);
  }

  /**
   * <p>set.</p>
   *
   * @param color a {@link org.jbox2d.common.Color3f} object
   */
  public void set(Color3f color) {
    r = (byte) (255 * color.x);
    g = (byte) (255 * color.y);
    b = (byte) (255 * color.z);
    a = (byte) 255;
  }
  
  /**
   * <p>set.</p>
   *
   * @param color a {@link org.jbox2d.particle.ParticleColor} object
   */
  public void set(ParticleColor color) {
    r = color.r;
    g = color.g;
    b = color.b;
    a = color.a;
  }
  
  /**
   * <p>isZero.</p>
   *
   * @return a boolean
   */
  public boolean isZero() {
    return r == 0 && g == 0 && b == 0 && a == 0;
  }

  /**
   * <p>set.</p>
   *
   * @param r a byte
   * @param g a byte
   * @param b a byte
   * @param a a byte
   */
  public void set(byte r, byte g, byte b, byte a) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }
}
