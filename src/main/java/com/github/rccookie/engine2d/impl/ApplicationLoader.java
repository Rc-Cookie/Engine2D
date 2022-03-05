package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.ILoader;

/**
 * Generic total, implementation-dependent entry point for an application loader.
 * An implementation should usually just consist of a single constructor, taking
 * an {@link ILoader} and a subclass of {@link StartupPrefs} as parameter. That
 * constructor should also launch the application.
 */
public interface ApplicationLoader {
}
