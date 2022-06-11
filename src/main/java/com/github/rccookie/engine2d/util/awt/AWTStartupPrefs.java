package com.github.rccookie.engine2d.util.awt;

import com.github.rccookie.engine2d.impl.StartupPrefs;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * Startup preferences for running a {@link AWTApplicationLoader}.
 */
public class AWTStartupPrefs implements StartupPrefs<AWTStartupPrefs> {

    /**
     * Is multithreading inside the application allowed?
     */
    public boolean parallel = true;
    /**
     * Should the application launch on a different thread?
     */
    public boolean async = true;
    /**
     * Title of the application window.
     */
    public String name = "Application";
    /**
     * Command line args to evaluate.
     */
    public String[] args = Utils.getArgs();


    /**
     * Creates new parallel, async startup prefs.
     */
    public AWTStartupPrefs() {
    }

    public AWTStartupPrefs parallel(boolean parallel) {
        this.parallel = parallel;
        return this;
    }

    public AWTStartupPrefs async(boolean async) {
        this.async = async;
        return this;
    }

    public AWTStartupPrefs name(String name) {
        this.name = Arguments.checkNull(name, "name");
        return this;
    }

    public AWTStartupPrefs args(String... args) {
        this.args = Arguments.checkNull(args, "args");
        return this;
    }

    @Override
    public @NotNull AWTStartupPrefs clone() {
        return new AWTStartupPrefs()
                .parallel(parallel)
                .async(async)
                .name(name)
                .args(args);
    }
}
