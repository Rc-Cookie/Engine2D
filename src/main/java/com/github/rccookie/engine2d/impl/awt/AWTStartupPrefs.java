package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.impl.StartupPrefs;

public class AWTStartupPrefs implements StartupPrefs {

    public final boolean parallel;
    public final boolean async;
    public final String applicationName;

    public AWTStartupPrefs() {
        this("Application", true);
    }

    public AWTStartupPrefs(String applicationName, boolean parallel) {
        this(applicationName, parallel, true);
    }

    public AWTStartupPrefs(String applicationName, boolean parallel, boolean async) {
        this.applicationName = applicationName;
        this.parallel = parallel;
        this.async = async;
    }
}
