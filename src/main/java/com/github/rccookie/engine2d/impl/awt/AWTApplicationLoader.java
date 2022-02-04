package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.impl.ApplicationLoader;
import com.github.rccookie.engine2d.impl.Initializer;

public abstract class AWTApplicationLoader implements ApplicationLoader {

    public AWTApplicationLoader(Initializer initializer, AWTStartupPrefs prefs) {

        initializer.initialize();

        Application.setup(new AWTImplementation(prefs), prefs.parallel);

        initializer.load();

        if(prefs.async)
            Application.startAsync();
        else Application.start();
    }
}
