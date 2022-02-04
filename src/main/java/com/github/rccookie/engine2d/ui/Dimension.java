package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class Dimension extends Structure {

    private final IVec2 size;

    public Dimension(UIObject parent) {
        this(parent, new IVec2(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    public Dimension(UIObject parent, int width, int height) {
        this(parent, new IVec2(width, height));
    }

    public Dimension(UIObject parent, @NotNull IVec2 size) {
        super(parent);
        this.size = Arguments.checkNull(size, "size").clone();
    }

    @NotNull
    @Override
    public IVec2 getSize() {
        return clampSize(IVec2.min(size, super.getSize()));
    }

    public IVec2 getDimensionSize() {
        return size.clone();
    }

    public void setSize(IVec2 size) {
        this.size.set(size);
        modified();
    }

    @Override
    protected void updateStructure() { }
}
