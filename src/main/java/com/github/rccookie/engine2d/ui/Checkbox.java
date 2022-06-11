package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;

/**
 * A checkbox styled toggle.
 */
public class Checkbox extends Toggle {

    /**
     * Size of the checkbox.
     */
    private final int2 size = int2.one.scaled(20);

    public final ColorProperty backgroundColor = new ColorProperty(this, ThemeColor.SECOND);
    public final ColorProperty borderColor = new ColorProperty(this, ThemeColor.of(new Color(0.85f)));
    public final ColorProperty activeColor = new ColorProperty(this, ThemeColor.ACCENT);


    /**
     * Creates a new rectangular checkbox.
     *
     * @param parent The parent for the checkbox
     */
    public Checkbox(UIObject parent) {
        super(parent);
    }


    @NotNull
    @Override
    public int2 getSize() {
        return size;
    }

    /**
     * Sets the size of the checkbox.
     *
     * @param size The size to set
     */
    public void setSize(int2 size) {
        if(this.size.equals(Arguments.checkNull(size, "size"))) return;
        this.size.set(size);
        modified();
    }

    @Override
    protected Image generatePlainImage(boolean on) {
        Image image = new Image(size);

        image.drawRect(int2.zero, size, borderColor.get());
        image.fillRect(int2.one, size.added(-2, -2), backgroundColor.get());
        if(on) image.fillRect(new int2(4, 4), size.added(-8, -8), activeColor.get());

        image.round(2);

        return image;
    }
}
