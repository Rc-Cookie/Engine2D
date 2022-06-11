package com.github.rccookie.engine2d.ui;

import java.util.List;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * A structure that automatically spaces out its children in a
 * regular interval.
 */
public class SimpleList extends Structure {

    /**
     * Whether this list is vertical.
     */
    private boolean vertical;
    /**
     * Whether to have a gap at the ends.
     */
    private boolean outsideGap = true;


    /**
     * Creates a new list.
     *
     * @param parent The parent for the list
     * @param vertical Whether the list should be vertically or horizontally
     */
    public SimpleList(UIObject parent, boolean vertical) {
        super(parent);
        this.vertical = vertical;
        onChildChange.add(this::modified);
        onParentSizeChange.add(this::modified);
        setRenderOrder(RenderOrder.AFTER_CHILDREN);
    }

    private boolean calculatingSize = false;

    private int2 lastSize = null;

    @Override
    public @NotNull int2 getSize() {
        if(lastSize != null) return lastSize;
        if(calculatingSize) return super.getSize();

        List<UIObject> children = getChildren();
        calculatingSize = true;

        int max = 0;
        for(UIObject child : children)
            max = Num.max(max, vertical ? child.getSize().x : child.getSize().y);
        calculatingSize = false;

        // It's important to use the full size in the list direction because otherwise
        // the relative location will not be correct no more that is used for positioning
        // the elements.
        return lastSize = vertical ? new int2(max, super.getSize().y) : new int2(super.getSize().x, max);
    }

    @Override
    protected void updateStructure() {
        lastSize = null;

        List<UIObject> objects = getChildren();
        if(objects.isEmpty()) return;
        if(objects.size() == 1) {
            objects.get(0).relativeLoc.setZero();
            return;
        }
        // TODO: Use offset instead of relativeLoc, then calculate the size properly
        if(outsideGap) {
            float spacing = 2f / objects.size();
            for (int i = 0; i < objects.size(); i++) {
                if(vertical)
                     objects.get(i).relativeLoc.y = spacing * (i + 0.5f) - 1;
                else objects.get(i).relativeLoc.x = spacing * (i + 0.5f) - 1;
            }
        }
        else {
            float spacing = 2f / (objects.size() - 1);
            for (int i = 0; i < objects.size(); i++) {
                if(vertical)
                     objects.get(i).relativeLoc.y = spacing * i - 1;
                else objects.get(i).relativeLoc.x = spacing * i - 1;
            }
        }
    }

    /**
     * Returns whether this list is currently ordering vertically or horizontally.
     *
     * @return Whether this list is vertical
     */
    public boolean isVertical() {
        return vertical;
    }

    /**
     * Returns whether a gap is left at the outsides of the list.
     *
     * @return Whether a gap is left that the outsides
     */
    public boolean isOutsideGap() {
        return outsideGap;
    }

    /**
     * Sets this list to be ordering vertically or horizontally.
     *
     * @param vertical Whether this list should be vertical or not
     */
    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        modified();
    }

    /**
     * Sets whether this list should have gaps at the outside or align the outermost
     * items directly with the edge.
     *
     * @param outsideGap Whether the list should have outside gaps or not
     */
    public void setOutsideGap(boolean outsideGap) {
        this.outsideGap = outsideGap;
        modified();
    }
}
