package com.github.rccookie.engine2d.ui;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.ui.util.Alignment;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A structure that stacks its children automatically in a specified direction.
 */
public class StackView extends Structure {

    /**
     * The stacking orientation.
     */
    @NotNull
    private Orientation orientation;

    /**
     * Gap size between entries.
     */
    private int gapSize = 0;

    /**
     * Gap size before the first entry.
     */
    private int startGapSize = 0;



    /**
     * Creates a new stack view.
     *
     * @param parent The parent for this structure
     * @param orientation The orientation of the stack
     */
    public StackView(@Nullable UIObject parent, @NotNull Orientation orientation) {
        super(parent);
        this.orientation = Arguments.checkNull(orientation, "orientation");
        setTraverseReverse(orientation == Orientation.BOTTOM_TO_TOP || orientation == Orientation.RIGHT_TO_LEFT);
        setRenderOrder(RenderOrder.AFTER_CHILDREN);
        onChildChange.add(this::modified);
    }

    /**
     * Returns the stack's orientation.
     *
     * @return The orientation
     */
    @NotNull
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * Returns the gap size between entries.
     *
     * @return The current gap size
     */
    public int getGapSize() {
        return gapSize;
    }

    /**
     * Returns the gap to the first object.
     *
     * @return The current gap size before the first entry
     */
    public int getStartGapSize() {
        return startGapSize;
    }

    /**
     * Sets the orientation of the stack.
     *
     * @param orientation The orientation to use
     */
    public void setOrientation(@NotNull Orientation orientation) {
        if(this.orientation == Arguments.checkNull(orientation, "orientation")) return;
        this.orientation = orientation;
        setTraverseReverse(orientation == Orientation.BOTTOM_TO_TOP || orientation == Orientation.RIGHT_TO_LEFT);
        modified();
    }

    /**
     * Sets the gap size between entries.
     *
     * @param gapSize The gap size to use
     */
    public void setGapSize(int gapSize) {
        if(this.gapSize == gapSize) return;
        this.gapSize = gapSize;
        modified();
    }

    /**
     * Sets the gap size to the first entry in the stack.
     *
     * @param startGapSize The gap size to use
     */
    public void setStartGapSize(int startGapSize) {
        if(this.startGapSize == startGapSize) return;
        this.startGapSize = startGapSize;
        modified();
    }

    @Override
    protected void updateStructure() {
        int offset = startGapSize;
        for(UIObject child : getChildren()) {
            child.setAlignment(orientation.alignment);
            orientation.setRelativeLoc.accept(child.relativeLoc);
            orientation.setOffset.accept(child.offset, offset);
            offset += orientation.sizeCalculator.apply(child.getSize()) + gapSize;
        }
    }

    /**
     * The orientation in which the stack "stacks up" its children.
     */
    public enum Orientation {
        /**
         * Stacks the entries starting at the top and appending the children at the bottom.
         */
        TOP_TO_BOTTOM(Alignment.TOP   , l -> l.y=-1, (o,v) -> o.y= v, s -> s.y),
        /**
         * Stacks the entries onto the floor upwards.
         */
        BOTTOM_TO_TOP(Alignment.BOTTOM, l -> l.y= 1, (o,v) -> o.y=-v, s -> s.y),
        /**
         * Stacks the entries from the left, appending at the right.
         */
        LEFT_TO_RIGHT(Alignment.LEFT  , l -> l.x=-1, (o,v) -> o.x= v, s -> s.x),
        /**
         * Stacks the entries from the right, appending at the left.
         */
        RIGHT_TO_LEFT(Alignment.RIGHT , l -> l.x= 1, (o,v) -> o.x=-v, s -> s.x);

        /**
         * The alignment used for this orientation.
         */
        final Alignment alignment;
        /**
         * A function that sets the relative location vector that is passed
         * as parameter to the one used for the orientation.
         */
        final Consumer<float2> setRelativeLoc;
        /**
         * Sets the offset in the orientation's direction to the specified
         * value.
         */
        final BiConsumer<int2, Integer> setOffset;
        /**
         * Calculates the size of the specified dimensions in the orientation's
         * direction.
         */
        final Function<int2, Integer> sizeCalculator;

        /**
         * Creates a new Orientation.
         *
         * @param alignment The value for {@link #alignment}
         * @param setRelativeLoc The value for {@link #setRelativeLoc}
         * @param setOffset The value for {@link #setOffset}
         * @param sizeCalculator The value for {@link #sizeCalculator}
         */
        Orientation(Alignment alignment, Consumer<float2> setRelativeLoc, BiConsumer<int2, Integer> setOffset, Function<int2, Integer> sizeCalculator) {
            this.alignment = alignment;
            this.setRelativeLoc = setRelativeLoc;
            this.setOffset = setOffset;
            this.sizeCalculator = sizeCalculator;
        }
    }
}
