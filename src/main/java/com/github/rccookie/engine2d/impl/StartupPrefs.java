package com.github.rccookie.engine2d.impl;

import com.github.rccookie.util.Cloneable;

/**
 * Generic definition of preferences for starting an application
 * loader. An application loader should take a subclass of these
 * as parameter in the constructor.
 */
public interface StartupPrefs<T> extends Cloneable<T> {
}
