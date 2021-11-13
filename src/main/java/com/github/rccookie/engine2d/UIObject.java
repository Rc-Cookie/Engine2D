package com.github.rccookie.engine2d;

import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.event.Event;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.Arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UIObject {

    public final Vec2 relativeLoc = Vec2.ZERO.clone();
    public final IVec2 offset = IVec2.ZERO.clone();

    private Image image;
    private Alignment alignment = Alignment.AUTO;

    public final Event update = new Event() {
        @Override
        public void invoke() {
            super.invoke();
            // Invoke child updates after own update
            for(UIObject c : children) c.update.invoke();
        }
    };
    public final LocalInputManager input = new LocalInputManager(update) {
        @Override
        protected boolean isInputAvailable() {
            return Camera.getActive() != null && Camera.getActive() == getCamera();
        }
    };

    private UIObject parent = null;
    private final List<UIObject> children = new ArrayList<>();


    private boolean cacheCorrect = false;
    private IVec2 cachedScreenPos;



    public UIObject() {
        this(null);
    }

    public UIObject(UIObject parent) {
        // UI would throw an error if trying to set the parent, even when setting it to null
        if(parent != null)
            setParent(parent);
    }



    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(Alignment alignment) {
        this.alignment = Arguments.checkNull(alignment);
    }

    public UIObject getParent() {
        return parent;
    }

    public void setParent(UIObject parent) {
        if(this.parent == parent) return;
        if(getAllChildren().contains(parent))
            throw new IllegalStateException();
        if(this.parent != null) this.parent.children.remove(this);
        this.parent = parent;
        if(parent != null) parent.children.add(this);
    }

    public List<UIObject> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<UIObject> getAllChildren() {
        List<UIObject> allChildren = new ArrayList<>(children);
        for(UIObject c : children) allChildren.addAll(c.getAllChildren());
        return allChildren;
    }

    void addAllRelevantInPaintOrder(List<UIObject> list) {
        for(UIObject c : children) c.addAllRelevantInPaintOrder(list);
        if(getImage() != null) list.add(this);
    }

    public UIObject getChild(int index) {
        return children.get(index);
    }

    public int childCount() {
        return children.size();
    }

    public UI getUI() {
        return parent != null ? parent.getUI() : null;
    }

    public UIObject getRoot() {
        return parent != null ? parent.getRoot() : this;
    }

    public Camera getCamera() {
        return parent != null ? parent.getCamera() : null;
    }

    public IVec2 getSize() {
        Image image = getImage();
        if(image != null) return image.size;
        return parent != null ? parent.getSize() : IVec2.ZERO;
    }

    public IVec2 getScreenPos() {
        return calcScreenPos(false);
    }

    IVec2 calcScreenPos(boolean useCache) {
        if(parent == null) return null;

        IVec2 parentPos = useCache ? parent.getCachedScreenPos() : parent.calcScreenPos(false);
        if(parentPos == null) return null;

        IVec2 parentSize = parent.getSize();
        return parentPos
                .added(new Vec2(parentSize.x * relativeLoc.x * 0.5f, parentSize.y * relativeLoc.y * 0.5f)
                .toI()
                .add(offset)
                .add(alignment.getOffset(this)));
    }

    void resetCache() {
        cacheCorrect = false;
        cachedScreenPos = null;
        for(UIObject c : children) c.resetCache();
    }

    IVec2 getCachedScreenPos() {
        if(cacheCorrect) return cachedScreenPos;
        cacheCorrect = true;
        return cachedScreenPos = calcScreenPos(true);
    }
}
