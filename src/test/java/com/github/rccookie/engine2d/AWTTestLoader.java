package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.util.awt.AWTApplicationLoader;
import com.github.rccookie.engine2d.util.awt.AWTStartupPrefs;

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
