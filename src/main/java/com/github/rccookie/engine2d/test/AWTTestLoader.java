package com.github.rccookie.engine2d.test;

import com.github.rccookie.engine2d.impl.awt.AWTApplicationLoader;
import com.github.rccookie.engine2d.impl.awt.AWTStartupPrefs;
import org.jetbrains.annotations.TestOnly;

@TestOnly
class AWTTestLoader extends AWTApplicationLoader {

    public AWTTestLoader() {
        super(new TestLoader(), new AWTStartupPrefs());
    }

    public static void main(String... args) {
        new AWTTestLoader();
    }
}
