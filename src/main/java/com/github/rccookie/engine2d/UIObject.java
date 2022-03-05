package com.github.rccookie.engine2d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.rccookie.engine2d.core.LocalExecutionManager;
import com.github.rccookie.engine2d.core.LocalInputManager;
import com.github.rccookie.engine2d.ui.Dimension;
import com.github.rccookie.engine2d.ui.Structure;
import com.github.rccookie.engine2d.ui.Theme;
import com.github.rccookie.engine2d.ui.util.Alignment;
import com.github.rccookie.engine2d.util.NamedCaughtEvent;
import com.github.rccookie.event.BiParamEvent;
import com.github.rccookie.event.CaughtBiParamEvent;
import com.github.rccookie.event.CaughtEvent;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.Event;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Console;
import com.github.rccookie.util.ModIterableArrayList;

import com.diogonunes.jcolor.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Represents an ui element in the ui tree. An ui object has an image, which gets
 * generated automatically, whenever the properties of the ui object change. Therefore,
 * it is important that overriding classes call the {@link #modified()} method whenever
 * they change state.
 * <p>Because the ui object only generates its image on demand, changing multiple properties
 * of one object will usually not regenerate its image for every change, but only for the
 * next frame rendering.</p>
 * <p>UI objects have a parent and a list of children. Their position is always relative
 * to their parent's position on the screen. Visibility therefore also carries over from
 * the parent to its children; if the parent object gets disabled the children will not be
 * visible either.</p>
 */
public abstract class UIObject implements Iterable<UIObject> {

    /**
     * The ui objects location relative to its parent, and relative to the parent's size.
     * A value of -1 means this object is positioned on the left / top edge of the parent,
     * a value of 1 the opposite.
     * <p>Can be modified.</p>
     */
    @NotNull
    public final float2 relativeLoc = float2.ZERO.clone();
    /**
     * An absolute location offset of this object to its relative position, in pixels.
     */
    @NotNull
    public final int2 offset = int2.ZERO.clone();

    /**
     * The last generated image.
     */
    private Image image;
    /**
     * The ui objects alignment to the parent.
     */
    @NotNull
    private Alignment alignment = Alignment.AUTO;
    /**
     * The ui objects render order relative to its parent.
     */
    @NotNull
    private RenderOrder renderOrder = RenderOrder.BEFORE_AND_AFTER;


    /**
     * The ui objects minimum size.
     */
    @NotNull
    private final int2 minSize = int2.ONE.clone();
    /**
     * The ui objects maximum size.
     */
    @NotNull
    private final int2 maxSize = new int2(Integer.MAX_VALUE, Integer.MAX_VALUE);

    /**
     * The color theme of this ui object. {@code null} means the parent's
     * theme should be used.
     */
    @Nullable
    private Theme theme = null;

    /**
     * Whether this ui object is dependent on the theme and changing it should
     * cause this ui object to be set as modified.
     */
    private boolean themeDependent = true;

    /**
     * The ui objects name.
     */
    @NotNull
    private String name;
    {
        name = getClass().toString();
        name = name.substring(name.lastIndexOf('.') + 1);
    }


    /**
     * Whether this ui object is currently enabled locally.
     */
    boolean enabled = true;
    /**
     * Whether this ui object is currently visible.
     */
    private boolean visible = true;


    /**
     * Whether the mouse is currently hovering over this ui object.
     */
    private boolean hovered = false;
    /**
     * Whether the mouse is currently pressing down on this ui object.
     */
    private boolean pressed = false;

    /**
     * Whether this ui object should receive click events.
     */
    private boolean clickable = true;
    /**
     * Whether this ui elements allows click-through.
     */
    boolean clickThrough = false;


    /**
     * Whether this ui object has been modified since the last image
     * generation and the image must be regenerated when needed.
     */
    private boolean modified = true;
    /**
     * Whether this image is currently regenerating its image, which means
     * setting itself to be modified again is not allowed.
     */
    private boolean modifyLock = false;


    /**
     * Called once per frame after the map was updated. Parents get updated
     * before their children, and the children are updated in the order they
     * were added.
     */
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
    /**
     * Invoked when the enabled state of this ui object gets changed, with the new enabled
     * state as parameter.
     */
    public final ParamEvent<Boolean> onEnable = new CaughtParamEvent<>(false);
    /**
     * Invoked when the mouse starts or ends hovering over this ui object, with the new hover
     * state as parameter.
     */
    public final ParamEvent<Boolean> onHoverChange = new CaughtParamEvent<>();
    /**
     * Called when the {@link #modified()} method gets called and the object was not modified
     * before.
     */
    public final Event onChange = new CaughtEvent(false);
    /**
     * Called once per frame while the mouse is hovering over the ui object.
     */
    public final Event onHover = new CaughtEvent();
    /**
     * Called whenever the mouse presses down onto this ui object.
     */
    public final Event onPress = new CaughtEvent();
    /**
     * Called whenever the mouse releases after having pressed this ui object.
     */
    public final Event onRelease = new CaughtEvent();
    /**
     * Called whenever the mouse clicks onto this ui object, meaning it first pressed down on
     * it and then released the mouse button while still hovering over it.
     */
    public final Event onClick = new CaughtEvent();
    /**
     * Called whenever the children of this ui object get modified, with the affected child
     * and the change type as parameter. Modifications can be adding, removing or reordering a
     * child.
     */
    public final BiParamEvent<UIObject, ChangeType> onChildChange = new CaughtBiParamEvent<>(false);
    /**
     * Called whenever the ui objects parent changes, meaning it got set or removed. When the parent
     * gets replaced this event will be invoked twice.
     */
    public final BiParamEvent<UIObject, ChangeType> onParentChange = new CaughtBiParamEvent<>(false);
    /**
     * Called whenever the ui objects parent changes size.
     */
    public final ParamEvent<int2> onParentSizeChange = new CaughtParamEvent<>() {
        @Override
        public boolean invoke(int2 info) {
            boolean result = super.invoke(info);
            if(getImage() == null) // Means that this objects size is exclusively defined by the parent's size
                for(UIObject child : children)
                    child.onParentSizeChange.invoke(UIObject.this instanceof Structure ? getSize() : info);
            return result;
        }
    };


    /**
     * Local input manager only active when the ui object is enabled globally.
     */
    public final LocalInputManager input = new LocalInputManager.Impl(update, this::isEnabledAndOnScreen);
    /**
     * Local execution manager only active when the ui object is enabled globally.
     */
    public final LocalExecutionManager execute = new LocalExecutionManager(this::isEnabledAndOnScreen);


    /**
     * The ui objects parent. May be null.
     */
    private UIObject parent = null;
    /**
     * The ui objects children, in paint order from bottom to top.
     */
    @NotNull
    private final List<UIObject> children = new ModIterableArrayList<>();
    /**
     * A view of {@link #children}.
     */
    @NotNull
    private final List<UIObject> childrenView = Collections.unmodifiableList(children);
    /**
     * Whether this ui object is locked, meaning that its visible state and its parent
     * cannot be changed.
     */
    private boolean structureLocked = false;


    /**
     * Is {@link #cachedScreenPos} up to date?
     */
    private boolean cacheCorrect = false;
    /**
     * Cached position on screen.
     */
    private final int2 cachedScreenPos = new int2();


    /**
     * Creates a new ui object with the specified parent.
     *
     * @param parent The ui objects parent, or {@code null} if the ui object
     *               should not have any parent
     */
    public UIObject(@Nullable UIObject parent) {
        // UI would throw an error if trying to set the parent, even when setting it to null
        if(parent != null)
            setParent(parent);

        update.add(this::updateHoverState);
        onHoverChange.add(s -> hovered = s);
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


    /**
     * Returns the name of the ui object and its position on the screen, if it is
     * currently being displayed.
     *
     * @return A string representation of this object
     */
    @Override
    @NotNull
    public String toString() {
        int2 pos = getScreenPos();
        if(pos == null) return name;
        return name + " at " + getScreenPos();
    }



    /**
     * Is this ui object enabled globally and attached to the active camera?
     *
     * @return Whether this ui object is enabled and on the screen
     */
    private boolean isEnabledAndOnScreen() {
        return isEnabledGlobal() && Camera.getActive() == getCamera();
    }

    /**
     * Informs the ui object that a for rendering relevant property has been modified
     * and the image needs to be regenerated before being used again. Multiple calls
     * to this method without image regeneration in between will have no effect. Also,
     * calling this method while the ui object is currently regenerating its image will
     * be ignored.
     */
    public void modified() {
        if(modifyLock || modified) return;

        modified = true;
        onChange.invoke();
    }

    /**
     * Modifies this ui object and all its children that a for rendering relevant property
     * has been modified.
     *
     * @see #modified()
     */
    public void modifiedRecursively() {
        if(modifyLock) return;

        for(UIObject child : children)
            child.modifiedRecursively();
        modified();
    }

    /**
     * Called when the theme has been modified
     *
     * @param force Whether the modification started on this object
     */
    void themeModified(boolean force) {
        if(modifyLock) return;

        if(!force && theme != null) return;

        for(UIObject child : children)
            child.themeModified(false);
        if(themeDependent)
            modified();
    }



    /**
     * Returns the image of this ui object.
     * <p>If this ui object has been modified since the last image request, this
     * will first regenerate the image and then return the new image.</p>
     *
     * @return The current image of the ui object
     */
    public Image getImage() {
        if(modified && !modifyLock) {
            try {
                modifyLock = true;
                modified = false;
                int2 oldSize = getSize(); // Will NOT try to rerender because of modifyLock
                image = generateImage();
                int2 newSize = getSize();
                if(oldSize.equals(newSize))
                    for(UIObject child : children)
                        child.onParentSizeChange.invoke(newSize);
            } catch(Exception e) {
                System.err.println("Exception generating image of " + getName());
                e.printStackTrace();
            }
            modifyLock = false;
        }
        return image;
    }

    /**
     * Generates a new image of this ui object with the current properties.
     *
     * <p>The generated image may fit the current minimum and maximum size
     * which can be obtained using {@link #getMinSize()} and {@link #getMaxSize()},
     * or the preferred size can be clamped between them directly using
     * {@link #clampSize(int2)}.</p>
     *
     * @return An image for the current visual state
     */
    @Nullable
    protected abstract Image generateImage();

    /**
     * Returns the currently used alignment of this ui object relative to its parent.
     *
     * @return The current alignment
     */
    @NotNull
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Sets the alignment to use for the relative location relative to the parent.
     * The alignment will not affect the absolute offset.
     * <p>The default alignment is {@link Alignment#AUTO}</p>
     *
     * @param alignment The alignment to use
     */
    public void setAlignment(@NotNull Alignment alignment) {
        this.alignment = Arguments.checkNull(alignment);
    }

    /**
     * Returns the currently used render order.
     *
     * @return The current render order
     */
    @NotNull
    public RenderOrder getRenderOrder() {
        return renderOrder;
    }

    /**
     * Sets the order in which the image of this ui object and the image of its children
     * will be requested. The default value is {@link RenderOrder#BEFORE_CHILDREN}.
     *
     * @param renderOrder The render order to use
     */
    public void setRenderOrder(@NotNull RenderOrder renderOrder) {
        this.renderOrder = Arguments.checkNull(renderOrder, "renderOrder");
    }

    /**
     * Returns the currently set minimum size of the ui object.
     *
     * @return The currently used minimum size
     */
    @NotNull
    public int2 getMinSize() {
        return minSize.clone();
    }

    /**
     * Returns the currently set maximum size of the ui object.
     *
     * @return The currently used maximum size
     */
    @NotNull
    public int2 getMaxSize() {
        return maxSize.clone();
    }

    /**
     * Sets the minimum size of this ui object. The default value is
     * 0. If the minimum size is greater that the current maximum size
     * the maximum will be adjusted accordingly.
     *
     * @param minSize The minimum size to set
     */
    public void setMinSize(@NotNull int2 minSize) {
        this.minSize.set(minSize);
        maxSize.set(int2.max(minSize, maxSize));
        modified();
    }

    /**
     * Sets the minimum size of this ui object. The default value is
     * {@link Integer#MAX_VALUE}. If the minimum size is less that the current
     * minimum size the minimum will be adjusted accordingly.
     *
     * @param maxSize The maximum size to set
     */
    public void setMaxSize(@NotNull int2 maxSize) {
        this.maxSize.set(maxSize);
        minSize.set(int2.min(minSize, maxSize));
        modified();
    }

    /**
     * Utility method that clamps the given preferred size to fit the current
     * minimum and maximum size.
     *
     * @param size The preferred size
     * @return The clamped size
     */
    @NotNull
    protected int2 clampSize(@NotNull int2 size) {
        return int2.clamp(size, minSize, maxSize);
    }

    /**
     * Returns the ui objects name.
     *
     * @return The name
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * Sets the ui objects name. Passing {@code null} will set the name
     * to an empty string (but <i>not</i> to the value {@code null}).
     *
     * @param name The name to set
     */
    public void setName(@Nullable String name) {
        this.name = name == null ? "" : name;
    }

    /**
     * Returns whether this ui object is locally enabled. It may still
     * be disabled globally because a parent is disabled.
     *
     * @return Whether this ui object itself is enabled
     */
    public boolean isEnabledLocal() {
        return enabled;
    }

    /**
     * Returns whether this ui object is enabled in global context, meaning
     * that it is enabled itself and all of its parents are enabled.
     *
     * @return Whether this ui object is enabled globally
     */
    public boolean isEnabledGlobal() {
        return enabled && (parent == null || parent.isEnabledGlobal());
    }

    /**
     * Sets the local enabled state of this ui object. The ui object may
     * be disabled globally anyway because one of its parents is disabled.
     *
     * @param enabled The enabled state to set
     */
    public void setEnabled(boolean enabled) {
        if(this.enabled == enabled) return;
        this.enabled = enabled;
        if(parent == null || parent.isEnabledGlobal()) // If not the state did not change
            invokeOnEnable(enabled, true);
    }

    /**
     * Invokes {@link #onEnable} on this ui object and all of its children
     * that are enabled.
     *
     * @param enabled The new global enabled state
     * @param force Whether to force the event to fire even if nothing has changed
     */
    private void invokeOnEnable(boolean enabled, boolean force) {
        if(!(force || this.enabled)) return;
        onEnable.invoke(enabled);
        for(UIObject c : children)
            c.invokeOnEnable(enabled, false);
    }

    /**
     * Returns whether this ui object is enabled by default, which it is. Override
     * this method to have the object disabled by default.
     *
     * @return Whether this ui object should be disabled by default
     */
    protected boolean enabledByDefault() {
        return true;
    }

    /**
     * Returns whether this ui object is currently visible.
     *
     * @return Whether this ui object is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether this ui object should be visible or not. A non-visible enabled
     * ui object will still receive events and may still have visible children.
     *
     * @param visible The visible state to set
     */
    public void setVisible(boolean visible) {
        if(structureLocked)
            throw new UnsupportedOperationException(this + " is locked.");
        this.visible = visible;
    }


    /**
     * Returns whether the mouse is currently hovering over this ui object.
     *
     * @return Whether this ui object is hovered
     */
    public boolean isHovered() {
        return hovered;
    }

    /**
     * Returns whether the mouse is currently pressing this ui object.
     *
     * @return Whether this ui object is pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * Returns whether this ui object is currently clickable.
     *
     * @return Whether this ui object is clickable
     */
    protected boolean isClickable() {
        return clickable;
    }

    /**
     * Returns whether this ui object currently allows clicking through it.
     *
     * @return Whether this ui object is click-through
     */
    public boolean isClickThrough() {
        return clickThrough;
    }

    /**
     * Sets this ui object to be clickable or not. A non-clickable ui object
     * will not receive click events.
     *
     * @param clickable The clickable state to set
     */
    protected void setClickable(boolean clickable) {
        if(this.clickable == clickable) return;
        this.clickable = clickable;
        if(!clickable && pressed) {
            pressed = false;
            onRelease.invoke();
        }
    }

    /**
     * Sets this ui object to be click-through or not. Enabling this will
     * disable clickability (which can be enabled again manually).
     *
     * @param clickThrough Whether this ui object should be click-through
     */
    public void setClickThrough(boolean clickThrough) {
        if(this.clickThrough == clickThrough) return;
        this.clickThrough = clickThrough;
        if(clickThrough)
            setClickable(false);
    }

    /**
     * Updates the mouse hover state and invokes onHoverChange if needed.
     */
    private void updateHoverState() {
        if(!clickable || hovered == containsMouse(true)) return;

        onHoverChange.invoke(!hovered);
    }


    /**
     * Determines whether this ui object contains the given pixel. It does
     * not need to be on top at that point though.
     *
     * @param pixel The pixel to test at
     * @param pixelPerfect Whether full transparency at that pixel means
     *                     non-containment
     * @return Whether this ui object contains the given pixel
     * @see #containsMouse(boolean)
     * @see UI#getObjectsAt(int2)
     */
    public boolean contains(@NotNull int2 pixel, boolean pixelPerfect) {
        int2 pos = getScreenPos();
        Image image = getImage();
        if(image == null) return false;
        int2 size = image.size;

        if(pixel.x < pos.x - (size.x / 2) || pixel.x > pos.x + (size.x - size.x / 2)
                || pixel.y < pos.y - (size.y / 2) || pixel.y >= pos.y + (size.y - size.y / 2)) return false;

        return !pixelPerfect || image.getPixel(int2.min(image.size.subed(int2.ONE), pixel.subed(pos).add(image.center))).a != 0;
    }

    /**
     * Determines whether this ui object contains the pixel at which the mouse currently is.
     *
     * @param onTop Whether this object has to be on top
     * @return Whether this ui object is at the mouse pixel
     */
    public boolean containsMouse(boolean onTop) {
        UI ui = getUI();
        if(ui == null || ui.mouseObjects.isEmpty()) return false;
        return onTop ? ui.mouseObjects.get(0) == this : ui.mouseObjects.contains(this);
    }


    /**
     * Returns the ui objects parent. May be null.
     *
     * @return The ui objects parent
     */
    public UIObject getParent() {
        return parent;
    }

    /**
     * Sets the ui objects parent to the given object. Setting {@code null}
     * will detach this ui object from any parent.
     *
     * @param parent The parent to set
     * @throws IllegalStateException If the parent is also a child of this object
     */
    @SuppressWarnings("ConstantConditions")
    public void setParent(@Nullable UIObject parent) {
        if(structureLocked)
            throw new UnsupportedOperationException(this + " is locked.");

        if(this.parent == parent) return;
        if(getAllChildren().contains(parent))
            throw new IllegalStateException("Cannot set a parent that is also a child");
        if(this.parent != null) {
            this.parent.children.remove(this);
            this.parent.onChildChange.invoke(this, ChangeType.REMOVED);
            onParentChange.invoke(this.parent, ChangeType.REMOVED);
        }
        // Save now but run later. If null is set that would also change the size, but
        // as the object is not shown then there is no need to render it.
        boolean sizeChanged = this.parent == null || !this.parent.getSize().equals(parent.getSize());
        this.parent = parent;
        if(parent != null) {
            parent.children.add(this);
            parent.onChildChange.invoke(this, ChangeType.ADDED);
            onParentChange.invoke(parent, ChangeType.ADDED);
        }
        else {
            hovered = pressed = false;
        }
        if(sizeChanged && onParentSizeChange.getActions().size() != 0)
            onParentSizeChange.invoke(parent.getSize()); // parent is never null here
    }

    /**
     * Removes this object from its parent. This is equivalent to calling
     * {@code setParent(null)}.
     *
     * @return Whether the ui object had a parent previously
     */
    public boolean remove() {
        boolean changed = parent != null;
        setParent(null);
        return changed;
    }

    /**
     * Returns the index of this ui object as child of the parent.
     *
     * @return This objects child index
     * @throws NullPointerException If this object has no parent
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getIndex() {
        if(parent == null)
            throw new NullPointerException("No parent - ui object is not a child");
        return parent.children.indexOf(this);
    }

    /**
     * Sets the own index as child in the parent.
     *
     * @param index The index to set
     * @throws NullPointerException If this object has no parent
     */
    public void setIndex(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        if(parent == null)
            throw new NullPointerException("Cannot set index in parent because the ui object has no parent");
        if(parent.children.get(index) == this) return;
        parent.children.remove(this);
        parent.children.add(index, this);
        parent.onChildChange.invoke(this, ChangeType.MOVED);
    }

    /**
     * Sets the own index as child in the parent to the highest index available.
     *
     * @throws NullPointerException If this object has no parent
     */
    public void moveToTop() {
        setIndex(parent.children.size()-1);
    }

    /**
     * Sets the own index as child in the parent to 0.
     *
     * @throws NullPointerException If this object has no parent
     */
    public void moveToBottom() {
        setIndex(0);
    }

    /**
     * Locks this ui object once and for all.
     */
    public void lockStructure() {
        structureLocked = true;
    }

    /**
     * Recursively locks this object and all of its children once and for all.
     */
    public void lockStructureRecursive() {
        lockStructure();
        for(UIObject o : children)
            o.lockStructureRecursive();
    }

    /**
     * Returns a view onto the direct children of this ui object.
     *
     * @return This ui object's direct children
     */
    @NotNull
    public List<UIObject> getChildren() {
        return childrenView;
    }

    /**
     * Returns all direct and indirect children of this ui object.
     *
     * @return All children of this ui object
     */
    @NotNull
    public List<UIObject> getAllChildren() {
        List<UIObject> allChildren = new ArrayList<>(children);
        for(UIObject c : children) allChildren.addAll(c.getAllChildren());
        return allChildren;
    }

    /**
     * Returns an iterator iterating over this ui object and all of its
     * direct and indirect children.
     *
     * @return An iterator over this and all of its children
     */
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

    /**
     * Returns a stream over this ui object and all of its direct and indirect children.
     *
     * @return A stream over this and all its children
     */
    @NotNull
    public Stream<UIObject> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    /**
     * Finds an ui object of the given type with the given name in this ui subtree.
     *
     * @param type The type of ui object to find
     * @param name The name of the object to find
     * @return The ui object found, or {@code null} if none matched
     */
    public <T> T find(@NotNull Class<T> type, String name) {
        return stream().filter(t -> type.isInstance(t) && t.name.equals(name))
                .map(type::cast).findAny().orElse(null);
    }

    /**
     * Finds an ui object with the given name in this ui subtree.
     *
     * @param name The name of the object to find
     * @return The ui object found, or {@code null} if none matched
     */
    public UIObject find(String name) {
        return stream().filter(t -> t.name.equals(name)).findAny().orElse(null);
    }

    /**
     * Finds an ui object of the given type in this ui subtree
     *
     * @param type The type of ui object to find
     * @return The ui object found, or {@code null} if none matched
     */
    public <T> T find(@NotNull Class<T> type) {
        return stream().filter(type::isInstance)
                .map(type::cast).findAny().orElse(null);
    }

    /**
     * Adds all enabled, visible, non-transparent ui objects in this
     * subtree to the given list in paint order from top to bottom.
     *
     * @param list The list to add to
     */
    void addAllRelevantInPaintOrder(@NotNull List<UIObject> list) {
        // Called by parent so no global enabled check needed
        if(!enabled || !visible) return;

        Image image = null;
        RenderOrder renderOrder = this.renderOrder;
        if(renderOrder != RenderOrder.AFTER_CHILDREN) image = getImage();

        for(int i=children.size()-1; i>=0; i--)
            children.get(i).addAllRelevantInPaintOrder(list);

        if(renderOrder != RenderOrder.BEFORE_CHILDREN) image = getImage();
        if(image != null && !image.definitelyBlank) list.add(this);
    }

    /**
     * Returns the child at the given index.
     *
     * @param index The index to find the child at
     * @return The child at that index
     */
    @NotNull
    public UIObject getChild(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return children.get(index);
    }

    /**
     * Returns the number of direct children of this ui object.
     *
     * @return The number of direct children
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int childCount() {
        return children.size();
    }

    /**
     * Returns the ui root of this ui object, or {@code null} if none is
     * present.
     *
     * @return The root ui in this ui tree
     */
    public UI getUI() {
        return parent != null ? parent.getUI() : null;
    }

    /**
     * Returns the root object in this ui tree.
     *
     * @return The root ui object
     */
    @NotNull
    public UIObject getRoot() {
        return parent != null ? parent.getRoot() : this;
    }

    /**
     * Returns the camera that the ui root is attached to, if a ui is
     * present and is attached to any camera.
     *
     * @return The camera this ui object is attached to, or {@code null}
     */
    public Camera getCamera() {
        return parent != null ? parent.getCamera() : null;
    }

    /**
     * Returns the map that the camera this ui is attached to is itself
     * rendering, if any.
     *
     * @return The map this ui object is rendered above, or {@code null}
     */
    public Map getMap() {
        Camera camera = getCamera();
        return camera != null ? camera.getMap() : null;
    }

    /**
     * Returns the map that this ui is rendered above, cast to the given type.
     *
     * @param type The type to cast to
     * @return The map this ui object is rendered above as the given type, or
     *         {@code null} if no map is present
     */
    public <M> M getMap(Class<M> type) {
        return type.cast(getMap());
    }

    /**
     * Returns the theme currently used by this ui object. If the theme is not
     * explicitly specified the parent's theme will be returned. If no parent
     * is present the default theme {@link Theme#DEFAULT} will be returned.
     *
     * @return The currently used theme
     */
    @NotNull
    public Theme getTheme() {
        if(theme != null) return theme;
        return parent != null ? parent.getTheme() : Theme.DEFAULT;
    }

    /**
     * Returns whether this ui object is theme dependent.
     *
     * @return Whether this ui object is theme dependent
     */
    public boolean isThemeDependent() {
        return themeDependent;
    }

    /**
     * Sets the theme for this ui object and (indirectly) its children that
     * do not define their theme explicitly. Setting {@code null} will use
     * the parent's theme.
     *
     * @param theme The theme to set, or {@code null}
     */
    public void setTheme(@Nullable Theme theme) {
        if(Objects.equals(this.theme, theme)) return;
        this.theme = theme;
        themeModified(true);
    }

    /**
     * Sets whether this ui object should be theme dependent, which means that changing
     * the theme (directly or indirectly) will set it to be modified. This does not
     * affect children.
     *
     * @param themeDependent Whether this ui object should be theme dependent
     */
    public void setThemeDependent(boolean themeDependent) {
        if(this.themeDependent == themeDependent) return;
        this.themeDependent = themeDependent;
        if(themeDependent) modified();
    }



    /**
     * Returns the size of this ui object, which is defined by the size of its image. If
     * it does not have an image, the parent's size will be returned. If no parent is
     * present, 0 will be returned.
     *
     * @return The size of this ui object
     */
    @NotNull
    public int2 getSize() {
        Image image = getImage();
        if(image != null) return image.size;
        return parent != null ? parent.getSize() : int2.ZERO;
    }

    /**
     * Computes the ui objects absolute position on the screen. If this ui object is not
     * attached to a camera, {@code null} will be returned.
     *
     * @return The absolute screen position
     */
    public int2 getScreenPos() {
        return calcScreenPos(false); // Always recalculate because positions may have been changed
    }

    /**
     * Calculates the absolute screen position or reads in from cache, if allowed
     *
     * @param useCache Whether cache should be used, if available
     * @return The ui objects absolute screen position
     */
    @Nullable
    int2 calcScreenPos(boolean useCache) {
        if(parent == null) return null;

        int2 parentPos = useCache ? parent.getCachedScreenPos() : parent.calcScreenPos(false);
        if(parentPos == null) return null;

        if(this instanceof Structure && !(this instanceof Dimension))
            return parentPos;

        int2 parentSize = parent.getSize();
        return parentPos
                .added(new float2(parentSize.x * relativeLoc.x * 0.5f, parentSize.y * relativeLoc.y * 0.5f)
                .toI()
                .add(offset)
                .add(getAlignmentOffset()));
    }

    /**
     * Computes the offset caused by the alignment.
     *
     * @return The offset from the alignment
     */
    @NotNull
    private int2 getAlignmentOffset() {
        if(this instanceof Structure)
            return alignment.getOffset(getSize(), this);
        Image image = getImage();
        return image != null ? alignment.getOffset(image.size, this) : int2.ZERO;
    }

    /**
     * Clears screen location cache.
     */
    void resetCache() {
        cacheCorrect = false;
        for(UIObject c : children) c.resetCache();
    }

    /**
     * Returns the cached screen location, or calculates and saves it if not available.
     *
     * @return The absolute screen position
     */
    @NotNull
    int2 getCachedScreenPos() {
        if(cacheCorrect) return cachedScreenPos;
        cacheCorrect = true;
        return cachedScreenPos.set(calcScreenPos(true));
    }


    /**
     * Text color for disabled objects
     */
    private static final Attribute DISABLED_COLOR = Attribute.TEXT_COLOR(127, 127, 127);

    /**
     * Prints this ui object into the console with the specified depth offset.
     *
     * @param depthInfo The depth offset to prepend on any line
     */
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
        else if(Console.Config.coloredOutput) line.append(Console.colored(this.toString(), DISABLED_COLOR));
        else line.append(this).append(" (disabled)");

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



    /**
     * Describes a type of change of the parent or a child.
     */
    public enum ChangeType {
        /**
         * A child or the parent was added / set.
         */
        ADDED,
        /**
         * A child or the parent was removed.
         */
        REMOVED,
        /**
         * A child's order was changed.
         */
        MOVED
    }

    /**
     * Describes the order in which the object's itself and its children's
     * images are requested during rendering.
     */
    public enum RenderOrder {
        /**
         * The objects image gets requested first, then the children's images
         * get requested.
         */
        BEFORE_CHILDREN,
        /**
         * The children's images are requested first, first when they are all
         * done recursively the objects image gets requested.
         */
        AFTER_CHILDREN,
        /**
         * Requests the objects image before the children's images are requested,
         * and again after they were rendered. This does not necessarily mean that
         * the image is generated twice, but gives children the chance to modify
         * this object in the rendering process, and changes will be updated.
         */
        BEFORE_AND_AFTER
    }
}
