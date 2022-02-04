package com.github.rccookie.engine2d.ui.debug;

import java.util.function.Supplier;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.Text;

public class AutoRefreshText extends Text {

    public AutoRefreshText(UIObject parent, Supplier<String> generator, float interval) {
        super(parent, "");
        execute.repeating(() -> setText(generator.get()), interval);
    }
}
