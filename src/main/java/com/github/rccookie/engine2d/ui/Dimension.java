package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

/**
 * A dimension is a structure that takes a dynamic size depending
 * on the size of the parent, intended for limiting the size of
 * children.
 */
public class Dimension extends Structure {

    /**
     * The size of this dimension.
     */
    private final int2 size;

    /**
     * Creates a new dimension with no size limitation.
     *
     * @param parent The parent for the dimension
     */
    public Dimension(UIObject parent) {
        this(parent, new int2(Integer.MAX_VALUE, Integer.MAX_VALUE));
    }

    /**
     * Creates a new dimension.
     *
     * @param parent The parent for the dimension
     * @param width The maximum width of the dimension
     * @param height The maximum height of the dimension
     */
    public Dimension(UIObject parent, int width, int height) {
        this(parent, new int2(width, height));
    }

    /**
     * Creates a new dimension.
     *
     * @param parent The parent for the dimension
     * @param size The maximum size of the dimension
     */
    public Dimension(UIObject parent, @NotNull int2 size) {
        super(parent);
        this.size = Arguments.checkNull(size, "size").clone();
        setRenderOrder(RenderOrder.BEFORE_CHILDREN);
    }

    @NotNull
    @Override
    public int2 getSize() {
        return clampSize(int2.min(size, super.getSize()));
    }

    /**
     * Returns the maximum size this dimension allows, as set using
     * {@link #setSize(int2)} or in a constructor.
     *
     * @return The maximum size of the dimension
     */
    public int2 getDimensionSize() {
        return size.clone();
    }

    /**
     * Sets the maximum size for the dimension.
     *
     * @param size The maximum size to use
     */
    public void setSize(int2 size) {
        this.size.set(size);
        modified();
    }

    // Do nothing
    @Override
    protected void updateStructure() { }
}
