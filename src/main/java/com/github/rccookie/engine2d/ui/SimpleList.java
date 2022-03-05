package com.github.rccookie.engine2d.ui;

import java.util.List;

import com.github.rccookie.engine2d.UIObject;

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
    }


    @Override
    protected void updateStructure() {
        List<UIObject> objects = getChildren();
        if(objects.isEmpty()) return;
        if(objects.size() == 1) {
            objects.get(0).relativeLoc.setZero();
            return;
        }
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
