package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.impl.StartupPrefs;

/**
 * Startup preferences for running a {@link AWTApplicationLoader}.
 */
public class AWTStartupPrefs implements StartupPrefs {

    /**
     * Is multithreading inside the application allowed?
     */
    public final boolean parallel;
    /**
     * Should the application launch on a different thread?
     */
    public final boolean async;
    /**
     * Title of the application window.
     */
    public final String applicationName;


    /**
     * Creates new parallel, async startup prefs.
     */
    public AWTStartupPrefs() {
        this("Application");
    }

    /**
     * Creates new parallel, async startup prefs.
     *
     * @param applicationName The name of the application
     */
    public AWTStartupPrefs(String applicationName) {
        this(applicationName, true);
    }

    /**
     * Creates new async startup prefs.
     *
     * @param applicationName The name of the application
     * @param parallel Whether multithreading should be allowed for
     *                 the application
     */
    public AWTStartupPrefs(String applicationName, boolean parallel) {
        this(applicationName, parallel, true);
    }

    /**
     * Creates new startup prefs.
     *
     * @param applicationName The name of the application
     * @param parallel Whether multithreading should be allowed for
     *                 the application
     * @param async Whether the application should start on a different thread
     */
    public AWTStartupPrefs(String applicationName, boolean parallel, boolean async) {
        this.applicationName = applicationName;
        this.parallel = parallel;
        this.async = async;
    }
}
