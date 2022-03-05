package com.github.rccookie.engine2d.ui.debug;

import java.util.function.Supplier;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.Text;

/**
 * A text that regularly updates its content.
 */
public class AutoRefreshText extends Text {

    /**
     * Creates a new auto refreshing text.
     *
     * @param parent The parent for the text
     * @param generator Generator for the content string to use
     * @param interval The interval in which the text should be updated
     */
    public AutoRefreshText(UIObject parent, Supplier<String> generator, float interval) {
        super(parent, "");
        execute.repeating(() -> setText(generator.get()), interval);
    }
}
