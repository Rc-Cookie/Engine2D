package com.github.rccookie.engine2d.impl.greenfoot;

import com.github.rccookie.engine2d.impl.StartupPrefs;
import com.github.rccookie.geometry.performance.int2;

public class GreenfootStartupPrefs implements StartupPrefs {

    public final int2 startupSize;
    public final Session sessionOverride;

    public GreenfootStartupPrefs() {
        this(null);
    }

    public GreenfootStartupPrefs(int2 startupSize) {
        this(startupSize, null);
    }

    public GreenfootStartupPrefs(int2 startupSize, Session sessionOverride) {
        this.startupSize = startupSize == null ? new int2(600, 400) : startupSize;
        this.sessionOverride = sessionOverride;
    }
}
