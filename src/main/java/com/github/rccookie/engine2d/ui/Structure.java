package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Structure extends UIObject {

    public Structure(@Nullable UIObject parent) {
        super(parent);
    }

    @Override
    protected final Image generateImage() {
        updateStructure();
        return null;
    }

    @NotNull
    @Override
    public IVec2 getSize() {
        UIObject parent = getParent();
        return parent == null ? IVec2.ZERO : parent.getSize();
    }

    protected abstract void updateStructure();
}
