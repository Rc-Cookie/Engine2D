package com.github.rccookie.engine2d.ui;

import java.util.List;

import com.github.rccookie.engine2d.UIObject;

public class SimpleList extends Structure {

    private boolean vertical;
    private boolean outsideGap = true;


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

    public boolean isVertical() {
        return vertical;
    }

    public boolean isOutsideGap() {
        return outsideGap;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        modified();
    }

    public void setOutsideGap(boolean outsideGap) {
        this.outsideGap = outsideGap;
        modified();
    }
}
