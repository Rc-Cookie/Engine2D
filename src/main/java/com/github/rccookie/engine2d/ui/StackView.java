package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StackView extends Structure { // TODO: Implement

    /**
     * Creates a new structure.
     *
     * @param parent The parent for this structure
     */
    public StackView(@Nullable UIObject parent, @NotNull Orientation orientation) {
        super(parent);
        setRenderOrder(RenderOrder.AFTER_CHILDREN);
    }

    @Override
    protected void updateStructure() {

    }

    public enum Orientation {
        TOP_TO_BOTTOM,
        BOTTOM_TO_TOP,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }
}
