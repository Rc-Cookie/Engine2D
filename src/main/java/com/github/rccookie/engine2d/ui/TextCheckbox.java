package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.IVec2;

public class TextCheckbox extends Dimension {

    private static final int SPACING = 5;

    public final Text text;
    public final Checkbox checkbox;

    public final ParamEvent<Boolean> onToggle;

    public TextCheckbox(UIObject parent, String title) {
        super(parent);

        checkbox = new Checkbox(null);

        onToggle = checkbox.onToggle;
        checkbox.onClick.add(onClick::invoke);
        checkbox.onPress.add(onPress::invoke);
        checkbox.onHover.add(onHover::invoke);
        checkbox.onHoverChange.add(onHoverChange::invoke);

        text = new Text(null, title);
        text.onClick.add(checkbox::toggle);

        new LeftRightOrder(this, checkbox, text);
    }

    @Override
    protected void updateStructure() {
        Image textImage = text.getImage();
        Image checkboxImage = checkbox.getImage();

        setSize(new IVec2(
                textImage.size.x + checkboxImage.size.x + SPACING,
                Math.max(textImage.size.y, checkboxImage.size.y)));
    }

    public boolean isOn() {
        return checkbox.isOn();
    }

    public void setOn(boolean on) {
        checkbox.setOn(on);
    }

    public void silentSetOn(boolean on) {
        checkbox.silentSetOn(on);
    }
}
