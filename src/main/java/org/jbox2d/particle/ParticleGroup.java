package org.jbox2d.particle;

import com.github.rccookie.geometry.performance.float2;

import org.jbox2d.common.Transform;

/**
 * <p>ParticleGroup class.</p>
 *
 */
public class ParticleGroup {

  ParticleSystem m_system;
  int m_firstIndex;
  int m_lastIndex;
  int m_groupFlags;
  float m_strength;
  ParticleGroup m_prev;
  ParticleGroup m_next;

  int m_timestamp;
  float m_mass;
  float m_inertia;
  final float2 m_center = new float2();
  final float2 m_linearVelocity = new float2();
  float m_angularVelocity;
  final Transform m_transform = new Transform();

  boolean m_destroyAutomatically;
  boolean m_toBeDestroyed;
  boolean m_toBeSplit;

  Object m_userData;

  /**
   * <p>Constructor for ParticleGroup.</p>
   */
  public ParticleGroup() {
    // m_system = null;
    m_firstIndex = 0;
    m_lastIndex = 0;
    m_groupFlags = 0;
    m_strength = 1.0f;

    m_timestamp = -1;
    m_mass = 0;
    m_inertia = 0;
    m_angularVelocity = 0;
    m_transform.setIdentity();

    m_destroyAutomatically = true;
    m_toBeDestroyed = false;
    m_toBeSplit = false;
  }

  /**
   * <p>getNext.</p>
   *
   * @return a {@link org.jbox2d.particle.ParticleGroup} object
   */
  public ParticleGroup getNext() {
    return m_next;
  }

  /**
   * <p>getParticleCount.</p>
   *
   * @return a int
   */
  public int getParticleCount() {
    return m_lastIndex - m_firstIndex;
  }

  /**
   * <p>getBufferIndex.</p>
   *
   * @return a int
   */
  public int getBufferIndex() {
    return m_firstIndex;
  }

  /**
   * <p>getGroupFlags.</p>
   *
   * @return a int
   */
  public int getGroupFlags() {
    return m_groupFlags;
  }

  /**
   * <p>setGroupFlags.</p>
   *
   * @param flags a int
   */
  public void setGroupFlags(int flags) {
    m_groupFlags = flags;
  }

  /**
   * <p>getMass.</p>
   *
   * @return a float
   */
  public float getMass() {
    updateStatistics();
    return m_mass;
  }

  /**
   * <p>getInertia.</p>
   *
   * @return a float
   */
  public float getInertia() {
    updateStatistics();
    return m_inertia;
  }

  /**
   * <p>getCenter.</p>
   *
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getCenter() {
    updateStatistics();
    return m_center;
  }

  /**
   * <p>getLinearVelocity.</p>
   *
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getLinearVelocity() {
    updateStatistics();
    return m_linearVelocity;
  }

  /**
   * <p>getAngularVelocity.</p>
   *
   * @return a float
   */
  public float getAngularVelocity() {
    updateStatistics();
    return m_angularVelocity;
  }

  /**
   * <p>getTransform.</p>
   *
   * @return a {@link org.jbox2d.common.Transform} object
   */
  public Transform getTransform() {
    return m_transform;
  }

  /**
   * <p>getPosition.</p>
   *
   * @return a {@link com.github.rccookie.geometry.performance.float2} object
   */
  public float2 getPosition() {
    return m_transform.p;
  }

  /**
   * <p>getAngle.</p>
   *
   * @return a float
   */
  public float getAngle() {
    return m_transform.q.getAngle();
  }

  /**
   * <p>getUserData.</p>
   *
   * @return a {@link java.lang.Object} object
   */
  public Object getUserData() {
    return m_userData;
  }

  /**
   * <p>setUserData.</p>
   *
   * @param data a {@link java.lang.Object} object
   */
  public void setUserData(Object data) {
    m_userData = data;
  }
  
  

  /**
   * <p>updateStatistics.</p>
   */
  public void updateStatistics() {
    if (m_timestamp != m_system.m_timestamp) {
      float m = m_system.getParticleMass();
      m_mass = 0;
      m_center.setZero();
      m_linearVelocity.setZero();
      for (int i = m_firstIndex; i < m_lastIndex; i++) {
        m_mass += m;
        float2 pos = m_system.m_positionBuffer.data[i];
        m_center.x += m * pos.x;
        m_center.y += m * pos.y;
        float2 vel = m_system.m_velocityBuffer.data[i];
        m_linearVelocity.x += m * vel.x;
        m_linearVelocity.y += m * vel.y;
      }
      if (m_mass > 0) {
        m_center.x *= 1 / m_mass;
        m_center.y *= 1 / m_mass;
        m_linearVelocity.x *= 1 / m_mass;
        m_linearVelocity.y *= 1 / m_mass;
      }
      m_inertia = 0;
      m_angularVelocity = 0;
      for (int i = m_firstIndex; i < m_lastIndex; i++) {
        float2 pos = m_system.m_positionBuffer.data[i];
        float2 vel = m_system.m_velocityBuffer.data[i];
        float px = pos.x - m_center.x;
        float py = pos.y - m_center.y;
        float vx = vel.x - m_linearVelocity.x;
        float vy = vel.y - m_linearVelocity.y;
        m_inertia += m * (px * px + py * py);
        m_angularVelocity += m * (px * vy - py * vx);
      }
      if (m_inertia > 0) {
        m_angularVelocity *= 1 / m_inertia;
      }
      m_timestamp = m_system.m_timestamp;
    }
  }
}
