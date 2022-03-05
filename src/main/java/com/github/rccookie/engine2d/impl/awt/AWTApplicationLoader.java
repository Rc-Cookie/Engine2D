package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.impl.ApplicationLoader;
import com.github.rccookie.engine2d.ILoader;

import org.jetbrains.annotations.NotNull;

/**
 * AWT implementation of {@link ApplicationLoader}. Example usage:
 * <pre>
 *     public static void main(String[] args) {
 *         new AWTApplicationLoader(new MyLoader(), new AWTStartupPrefs());
 *     }
 * </pre>
 */
public class AWTApplicationLoader implements ApplicationLoader {

    /**
     * Creates a new AWTApplication loader and launches the application.
     * If async starting is disabled in the prefs this method will block
     * until the application is closed.
     *
     * @param loader The loader used to load the application
     * @param prefs Startup preferences
     */
    public AWTApplicationLoader(@NotNull ILoader loader, @NotNull AWTStartupPrefs prefs) {

        loader.initialize();

        Application.setup(new AWTImplementation(prefs), prefs.parallel);

        loader.load();

        if(prefs.async)
            Application.startAsync();
        else Application.start();
    }
}
