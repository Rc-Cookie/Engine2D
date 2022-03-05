package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A structure is an abstract definition of types of ui objects
 * that don't have images themselves. There purpose is to change
 * the behaviour of child elements instead. Examples:
 * {@link Dimension}, {@link SimpleList}
 */
public abstract class Structure extends UIObject {

    /**
     * Creates a new structure.
     *
     * @param parent The parent for this structure
     */
    public Structure(@Nullable UIObject parent) {
        super(parent);
    }

    /**
     * Updates this structure and returns {@code null} (structures
     * don't have images themselves).
     *
     * @return {@code null}
     */
    @Override
    protected final Image generateImage() {
        updateStructure();
        return null;
    }

    /**
     * Returns the size of this structure, which is identical to the size
     * of its parent. If no parent is present 0 will be returned.
     *
     * @return The parent's size
     */
    @NotNull
    @Override
    public int2 getSize() {
        UIObject parent = getParent();
        return parent == null ? int2.ZERO : parent.getSize();
    }

    /**
     * Called whenever the structure has been modified and is requested.
     * Updates whatever makes up this structure.
     */
    protected abstract void updateStructure();
}
