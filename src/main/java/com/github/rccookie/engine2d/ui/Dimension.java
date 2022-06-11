package com.github.rccookie.engine2d.ui;

import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.util.Bounds;
import com.github.rccookie.geometry.performance.float2;
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
     * The size calculating function.
     */
    @NotNull
    private UnaryOperator<int2> sizeFunction;

    /**
     * Creates a new dimension that fits the size of its parent.
     *
     * @param parent The parent for the dimension
     */
    public Dimension(UIObject parent) {
        this(parent, 1f, 1f);
    }

    /**
     * Creates a new dimension with the specified width and height. Non-positive values
     * will be threatened as offset from the parent's size. Thus, {@code 0} means exactly
     * the parent's size. (If you want a width or height of 0 pixels, use
     * {@link Dimension#Dimension(UIObject, float, float)} or {@link Dimension#Dimension(UIObject, UnaryOperator)}).
     *
     * @param parent The parent for the dimension
     * @param width The width to use
     * @param height The height to uses
     */
    public Dimension(UIObject parent, int width, int height) {
        this(parent, s -> s.set(width <= 0 ? s.x + width : width, height <= 0 ? s.y + height : height));
    }

    /**
     * Creates a new dimension with the specified size. Non-positive values
     * will be threatened as offset from the parent's size. Thus, {@code 0} means exactly
     * the parent's size. (If you want a width or height of 0 pixels, use
     * {@link Dimension#Dimension(UIObject, float2)} or {@link Dimension#Dimension(UIObject, UnaryOperator)}).
     *
     * @param parent The parent for the dimension
     * @param size The maximum size of the dimension
     */
    public Dimension(UIObject parent, @NotNull int2 size) {
        this(parent, size.x, size.y);
    }

    /**
     * Creates a new dimension that has a size relative to the parent's size.
     * {@code 1} means exactly the parent's size.
     *
     * @param parent The parent for the dimension
     * @param widthScale The width of this dimension relative to its parent
     * @param heightScale The height of this dimension relative to its parent
     */
    public Dimension(UIObject parent, float widthScale, float heightScale) {
        this(parent, new float2(Arguments.checkRange(widthScale, 0f, null), Arguments.checkRange(heightScale, 0f, null)));
    }

    /**
     * Creates a new dimension that has a size relative to the parent's size.
     * {@code 1} means exactly the parent's size.
     *
     * @param parent The parent for the dimension
     * @param scale The size of this dimension relative to its parent
     */
    public Dimension(UIObject parent, @NotNull float2 scale) {
        this(parent, s -> s);
        setSize(scale); // Check range and make copy
    }

    /**
     * Creates a new dimension with the specified size calculating function. The
     * function takes the parent's size as input and calculates the preferred size
     * for the dimension. To do so, the supplied vector may be modified in-place.
     * <p>The function may be stateless, as it may not be called
     * regularly and the effect of a stateful function would be unpredictable.</p>
     *
     * @param parent The parent for the dimension
     * @param sizeFunction The size calculation function
     */
    public Dimension(UIObject parent, @NotNull UnaryOperator<int2> sizeFunction) {
        super(parent);
        this.sizeFunction = Arguments.checkNull(sizeFunction, "sizeFunction");
        setRenderOrder(RenderOrder.BEFORE_CHILDREN);
    }

    @NotNull
    @Override
    public int2 getSize() {
        return clampSize(sizeFunction.apply(super.getSize().clone()));
    }

    /**
     * Returns the size that this dimension would like to be, independent
     * of min and max size.
     *
     * @return The preferred size of the dimension
     */
    public int2 getPreferredSize() {
        return sizeFunction.apply(super.getSize().clone());
    }

    /**
     * Sets the size of this dimension. Non-positive values
     * will be threatened as offset from the parent's size. Thus, {@code 0} means exactly
     * the parent's size. (If you want a width or height of 0 pixels, use
     * {@link Dimension#setSize(float, float)} or {@link Dimension#setSize(UnaryOperator)}).
     *
     * @param width The width to use
     * @param height The height to uses
     */
    public void setSize(int width, int height) {
        setSize(s -> s.set(width < 0 ? s.x - width : width, height < 0 ? s.y - height : height));
    }

    /**
     * Sets the size of this dimension. Non-positive values
     * will be threatened as offset from the parent's size. Thus, {@code 0} means exactly
     * the parent's size. (If you want a width or height of 0 pixels, use
     * {@link Dimension#setSize(float2)} or {@link Dimension#setSize(UnaryOperator)}).
     *
     * @param size The maximum size of the dimension
     */
    public void setSize(int2 size) {
        setSize(size.x, size.y);
    }

    /**
     * Sets the size of the dimension relative to the parent's size.
     * {@code 1} means exactly the parent's size.
     *
     * @param widthScale The width of this dimension relative to its parent
     * @param heightScale The height of this dimension relative to its parent
     */
    public void setSize(float widthScale, float heightScale) {
        setSize(new float2(widthScale, heightScale));
    }

    /**
     * Sets the size of the dimension relative to the parent's size.
     * {@code 1} means exactly the parent's size.
     *
     * @param scale The size of this dimension relative to its parent
     */
    public void setSize(float2 scale) {
        float2 scale0 = scale.clone();
        Arguments.checkRange(scale.x, 0f, null);
        Arguments.checkRange(scale.y, 0f, null);
        setSize(s -> s.toF().mul(scale0).toI());
    }

    /**
     * Sets the dimension's size calculating function. The
     * function takes the parent's size as input and calculates the preferred size
     * for the dimension. To do so, the supplied vector may be modified in-place.
     * <p>The function may be stateless, as it may not be called
     * regularly and the effect of a stateful function would be unpredictable.</p>
     *
     * @param sizeFunction The size calculation function
     */
    public void setSize(UnaryOperator<int2> sizeFunction) {
        this.sizeFunction = Arguments.checkNull(sizeFunction, "sizeFunction");
        modified();
    }

    // Do nothing
    @Override
    protected void updateStructure() { }

    @Override
    public Bounds getBounds() {
        int2 pos = getScreenPos();
        if(pos == null) return null;

        int2 hSize = getSize().dived(2);
        return new Bounds(pos.subed(hSize), pos.added(hSize));
    }
}
