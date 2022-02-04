package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.diogonunes.jcolor.Attribute;
import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.ui.Dimension;
import com.github.rccookie.engine2d.ui.Structure;
import com.github.rccookie.engine2d.ui.Theme;
import com.github.rccookie.engine2d.ui.util.Alignment;
import com.github.rccookie.engine2d.util.ModIterableArrayList;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.event.CaughtEvent;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public abstract class UIObject implements Iterable<UIObject> {

    @NotNull
    public final Vec2 relativeLoc = Vec2.ZERO.clone();
    @NotNull
    public final IVec2 offset = IVec2.ZERO.clone();

    private Image image;
    @NotNull
    private Alignment alignment = Alignment.AUTO;

    @NotNull
    private final IVec2 minSize = IVec2.ONE.clone();
    @NotNull
    private final IVec2 maxSize = new IVec2(Integer.MAX_VALUE, Integer.MAX_VALUE);

    @Nullable
    private Theme theme = null;

    @NotNull
    private String name;
    {
        name = getClass().toString();
        name = name.substring(name.lastIndexOf('.') + 1);
    }

    boolean enabled = true;
    private boolean visible = true;

    private boolean hovered = false, pressed = false;
    private boolean clickable = true;
    boolean clickThrough = false;

    private boolean modified = true;
    private boolean modifyLock = false;

    public final Event update = new NamedCaughtEvent(false, "UIObject.update on " + this) {
        @Override
        public boolean invoke() {
            // Invoked by parent so no global check needed
            if(!enabled) return false;

            super.invoke();
            // Invoke child updates after own update
            for(UIObject c : children)
                c.update.invoke();
            return false;
        }
    };
    public final ParamEvent<Boolean> onEnable      = new CaughtParamEvent<>(false);
    public final ParamEvent<Boolean> onHoverChange = new CaughtParamEvent<>();
    public final Event     onChange      = new CaughtEvent(false);
    public final Event     onHover       = new CaughtEvent();
    public final Event     onPress       = new CaughtEvent();
    public final Event     onRelease     = new CaughtEvent();
    public final Event     onClick       = new CaughtEvent();
    public final BiParamEvent<UIObject, ChangeType> onChildChange = new CaughtBiParamEvent<>(false);

    public final LocalInputManager input = new LocalInputManager.Impl(update, this::isEnabledAndOnScreen);
    public final LocalExecutionManager execute = new LocalExecutionManager(this::isEnabledAndOnScreen);

    private UIObject parent = null;
    @NotNull
    private final List<UIObject> children = new ModIterableArrayList<>();
    private boolean structureLocked = false;


    private boolean cacheCorrect = false;
    private final IVec2 cachedScreenPos = new IVec2();



    public UIObject(@Nullable UIObject parent) {
        // UI would throw an error if trying to set the parent, even when setting it to null
        if(parent != null)
            setParent(parent);

        update.add(this::updateHoverState);
        onHoverChange.add(s -> { hovered = s; });
        update.add(() -> { if(hovered) onHover.invoke(); });

        input.mousePressed.add(() -> {
            if(!clickable || !hovered) return;
            pressed = true;
            onPress.invoke();
        });
        input.mouseReleased.add(() -> {
            if(!clickable || !pressed) return;
            pressed = false;
            if(hovered) onClick.invoke();
            onRelease.invoke();
        });

        if(enabledByDefault()) {
            if(isEnabledGlobal())
                // No need to use invokeOnEnable() as there are no children at this point
                onEnable.invoke(true);
        }
        else enabled = false;
    }


    @Override
    @NotNull
    public String toString() {
        IVec2 pos = getScreenPos();
        if(pos == null) return name;
        return name + " at " + getScreenPos();
    }



    private boolean isEnabledAndOnScreen() {
        return isEnabledGlobal() && Camera.getActive() != null && Camera.getActive() == getCamera();
    }

    protected void modified() {
        if(modifyLock) return;

        modified = true;
        onChange.invoke();
    }

    protected void modifiedRecursively() {
        if(modifyLock) return;

        for(UIObject child : children)
            child.modifiedRecursively();
        modified();
    }

    void themeModified(boolean force) {
        if(modifyLock) return;

        if(!force && theme != null) return;

        for(UIObject child : children)
            child.themeModified(false);
        modified();
    }

    public Image getImage() {
        if(modified && !modifyLock) {
            try {
                modifyLock = true;
                modified = false;
                image = generateImage();
            } catch(Exception e) {
                System.err.println("Exception generating image of " + getName());
                e.printStackTrace();
            }
            modifyLock = false;
        }
        return image;
    }

    @Nullable
    protected abstract Image generateImage();

    @NotNull
    public Alignment getAlignment() {
        return alignment;
    }

    public void setAlignment(@NotNull Alignment alignment) {
        this.alignment = Arguments.checkNull(alignment);
    }

    @NotNull
    public IVec2 getMinSize() {
        return minSize.clone();
    }

    @NotNull
    public IVec2 getMaxSize() {
        return maxSize.clone();
    }

    public void setMinSize(@NotNull IVec2 minSize) {
        this.minSize.set(minSize);
        maxSize.set(IVec2.max(minSize, maxSize));
        modified();
    }

    public void setMaxSize(@NotNull IVec2 maxSize) {
        this.maxSize.set(maxSize);
        minSize.set(IVec2.min(minSize, maxSize));
        modified();
    }

    @NotNull
    protected IVec2 clampSize(@NotNull IVec2 size) {
        return IVec2.clamp(size, minSize, maxSize);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name == null ? "" : name;
    }

    public boolean isEnabledLocal() {
        return enabled;
    }

    public boolean isEnabledGlobal() {
        return enabled && (parent == null || parent.isEnabledGlobal());
    }

    public void setEnabled(boolean enabled) {
        if(this.enabled == enabled) return;
        this.enabled = enabled;
        invokeOnEnable(enabled, true);
    }

    private void invokeOnEnable(boolean enabled, boolean force) {
        if(!force && !this.enabled) return;
        onEnable.invoke(enabled);
        for(UIObject c : children)
            c.invokeOnEnable(enabled, false);
    }

    protected boolean enabledByDefault() {
        return true;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if(structureLocked)
            throw new UnsupportedOperationException(this + " is locked.");
        this.visible = visible;
    }


    public boolean isHovered() {
        return hovered;
    }

    public boolean isPressed() {
        return pressed;
    }

    protected boolean isClickable() {
        return clickable;
    }

    public boolean isClickThrough() {
        return clickThrough;
    }

    protected void setClickable(boolean clickable) {
        if(this.clickable == clickable) return;
        this.clickable = clickable;
        if(!clickable)
            pressed = false;
    }

    public void setClickThrough(boolean clickThrough) {
        if(this.clickThrough == clickThrough) return;
        this.clickThrough = clickThrough;
        if(!clickThrough)
            setClickable(false);
    }

    private void updateHoverState() {
        if(!clickable || hovered == containsMouse(true)) return;

        onHoverChange.invoke(!hovered);
    }

    public boolean contains(@NotNull IVec2 pixel, boolean pixelPerfect) {
        IVec2 pos = getScreenPos();
        Image image = getImage();
        if(image == null) return false;
        IVec2 size = image.size;

        if(pixel.x < pos.x - (size.x / 2) || pixel.x > pos.x + (size.x - size.x / 2)
                || pixel.y < pos.y - (size.y / 2) || pixel.y >= pos.y + (size.y - size.y / 2)) return false;

        return !pixelPerfect || image.getPixel(IVec2.min(image.size.subtracted(IVec2.ONE), pixel.subtracted(pos).add(image.center))).a != 0;
    }

    public boolean containsMouse(boolean onTop) {
        UI ui = getUI();
        if(ui == null || ui.mouseObjects.isEmpty()) return false;
        return onTop ? ui.mouseObjects.get(0) == this : ui.mouseObjects.contains(this);
    }


    public UIObject getParent() {
        return parent;
    }

    public void setParent(@Nullable UIObject parent) {
        if(structureLocked)
            throw new UnsupportedOperationException(this + " is locked.");

        if(this.parent == parent) return;
        if(getAllChildren().contains(parent))
            throw new IllegalStateException();
        if(this.parent != null) {
            this.parent.children.remove(this);
            this.parent.onChildChange.invoke(this, ChangeType.REMOVED);
        }
        this.parent = parent;
        if(parent != null) {
            parent.children.add(this);
            parent.onChildChange.invoke(this, ChangeType.ADDED);
        }
        else {
            hovered = pressed = false;
        }
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getIndex() {
        return parent.children.indexOf(this);
    }

    public void setIndex(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        if(parent.children.get(index) == this) return;
        parent.children.remove(this);
        parent.children.add(index, this);
        parent.onChildChange.invoke(this, ChangeType.MOVED);
    }

    public void moveToTop() {
        setIndex(parent.children.size()-1);
    }

    public void moveToBottom() {
        setIndex(0);
    }

    public void lockStructure() {
        structureLocked = true;
    }

    public void lockStructureRecursive() {
        lockStructure();
        for(UIObject o : children)
            o.lockStructureRecursive();
    }

    @NotNull
    public List<UIObject> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @NotNull
    public List<UIObject> getAllChildren() {
        List<UIObject> allChildren = new ArrayList<>(children);
        for(UIObject c : children) allChildren.addAll(c.getAllChildren());
        return allChildren;
    }

    @Override
    @NotNull
    public Iterator<UIObject> iterator() {
        return new Iterator<>() {

            int index = -1;
            Iterator<UIObject> childIt = null;

            @Override
            public boolean hasNext() {
                return index < children.size() && (childIt == null || childIt.hasNext());
            }

            @Override
            public UIObject next() {
                if(!hasNext())
                    throw new NoSuchElementException();

                UIObject out;
                if(index == -1) {
                    out = UIObject.this;
                    index++;
                    if(!children.isEmpty())
                        childIt = children.get(0).iterator();
                }
                else {
                    out = childIt.next();
                    if(!childIt.hasNext()) {
                        index++;
                        if(children.size() <= index) childIt = null;
                        else childIt = children.get(index).iterator();
                    }
                }
                return out;
            }
        };
    }

    @NotNull
    public Stream<UIObject> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @SuppressWarnings("unchecked")
    public <T> T find(@NotNull Class<T> type, String name) {
        return stream().filter(t -> type.isInstance(t) && t.name.equals(name))
                .map(t -> (T) t).findAny().orElse(null);
    }

    public UIObject find(String name) {
        return stream().filter(t -> t.name.equals(name)).findAny().orElse(null);
    }

    @SuppressWarnings("unchecked")
    public <T> T find(@NotNull Class<T> type) {
        return stream().filter(type::isInstance)
                .map(t -> (T) t).findAny().orElse(null);
    }

    void addAllRelevantInPaintOrder(@NotNull List<? super UIObject> list) {
        // Called by parent so no global check needed
        if(!enabled || !visible) return;
        for(int i=children.size()-1; i>=0; i--)
            children.get(i).addAllRelevantInPaintOrder(list);
        Image image = getImage();
        if(image != null && !image.definitelyBlank) list.add(this);
    }

    @NotNull
    public UIObject getChild(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return children.get(index);
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public int childCount() {
        return children.size();
    }

    public UI getUI() {
        return parent != null ? parent.getUI() : null;
    }

    @NotNull
    public UIObject getRoot() {
        return parent != null ? parent.getRoot() : this;
    }

    public Camera getCamera() {
        return parent != null ? parent.getCamera() : null;
    }

    @NotNull
    public Theme getTheme() {
        if(theme != null) return theme;
        return parent != null ? parent.getTheme() : Theme.DEFAULT;
    }

    public void setTheme(@Nullable Theme theme) {
        if(Objects.equals(this.theme, theme)) return;
        this.theme = theme;
        themeModified(true);
    }

    @NotNull
    public IVec2 getSize() {
        Image image = getImage();
        if(image != null) return image.size;
        return parent != null ? parent.getSize() : IVec2.ZERO;
    }

    public IVec2 getScreenPos() {
        return calcScreenPos(false);
    }

    @Nullable
    IVec2 calcScreenPos(boolean useCache) {
        if(parent == null) return null;

        IVec2 parentPos = useCache ? parent.getCachedScreenPos() : parent.calcScreenPos(false);
        if(parentPos == null) return null;

        if(this instanceof Structure && !(this instanceof Dimension))
            return parentPos;

        IVec2 parentSize = parent.getSize();
        return parentPos
                .added(new Vec2(parentSize.x * relativeLoc.x * 0.5f, parentSize.y * relativeLoc.y * 0.5f)
                .toI()
                .add(offset)
                .add(getAlignmentOffset()));
    }

    @NotNull
    private IVec2 getAlignmentOffset() {
        if(this instanceof Dimension)
            return alignment.getOffset(getSize(), this);
        Image image = getImage();
        return image != null ? alignment.getOffset(image.size, this) : IVec2.ZERO;
    }

    void resetCache() {
        cacheCorrect = false;
        for(UIObject c : children) c.resetCache();
    }

    @NotNull
    IVec2 getCachedScreenPos() {
        if(cacheCorrect) return cachedScreenPos;
        cacheCorrect = true;
        return cachedScreenPos.set(calcScreenPos(true));
    }


    private static final Attribute DISABLED_COLOR = Attribute.TEXT_COLOR(127, 127, 127);

    void printTree(@NotNull List<DepthInfo> depthInfo) {

        StringBuilder line = new StringBuilder();
        boolean enabled = this.enabled && (depthInfo.isEmpty() || depthInfo.get(depthInfo.size()-1).enabled);

        for(int i=0; i<depthInfo.size(); i++) {
            boolean last = i == depthInfo.size()-1;

            DepthInfo info = depthInfo.get(i);

            if(!(last || info.showLine)) line.append("  ");
            else {
                char first = last ? (info.showLine ? '\u251C' : '\u2514') : '\u2502';//info.showLine ? (last ? '>' : '|') : '+';
                String second = last ? "─" : " ";
                if(info.enabled) {
                    line.append(first);
                    if(!last || enabled)
                        line.append(second);
                    else line.append(Console.colored(second, DISABLED_COLOR));
                }
                else line.append(Console.colored(first + second, DISABLED_COLOR));
            }
        }

        if(enabled)
            line.append(this);
        else line.append(Console.colored(this.toString(), DISABLED_COLOR));

        Console.info(line);

        depthInfo.add(new DepthInfo(enabled, true));
        for(int i=0; i<children.size(); i++) {
            if(i == children.size()-1)
                depthInfo.set(depthInfo.size()-1, new DepthInfo(enabled, false));
            children.get(i).printTree(depthInfo);
        }
        depthInfo.remove(depthInfo.size()-1);
    }

    private static class DepthInfo {
        final boolean enabled, showLine;

        private DepthInfo(boolean enabled, boolean showLine) {
            this.enabled = enabled;
            this.showLine = showLine;
        }
    }



    public enum ChangeType {
        ADDED,
        REMOVED,
        MOVED
    }
}
