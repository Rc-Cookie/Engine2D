package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;

public abstract class Toggle extends Button {

    private Image plainImageOff, hoveredImageOff, pressedImageOff, disabledImageOff;
    private Image plainImageOn, hoveredImageOn, pressedImageOn, disabledImageOn;
    private boolean plainReadyOff = false, hoveredReadyOff = false, pressedReadyOff = false, disabledReadyOff = false;
    private boolean plainReadyOn = false, hoveredReadyOn = false, pressedReadyOn = false, disabledReadyOn = false;

    boolean on = false;

    public final ParamEvent<Boolean> onToggle = new CaughtParamEvent<>(false);

    public Toggle(UIObject parent) {
        super(parent);
        onToggle.add(this::onToggle);
        onClick.add(this::toggle);
    }

    @Override
    protected void modified() {
        plainImageOff = hoveredImageOff = pressedImageOff = disabledImageOff = null;
        plainImageOn = hoveredImageOn = pressedImageOn = disabledImageOn = null;
        plainReadyOff = hoveredReadyOff = pressedReadyOff = disabledReadyOff = false;
        plainReadyOn = hoveredReadyOn = pressedReadyOn = disabledReadyOn = false;
        super.modified();
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        if(this.on == on) return;
        onToggle.invoke(on);
    }

    public void silentSetOn(boolean on) {
        onToggle(on);
    }

    public void toggle() {
        setOn(!on);
    }

    private void onToggle(boolean on) {
        if(this.on == on) return;
        this.on = on;
        super.modified();
    }

    @Override
    Image getPlainImage() {
        if(on) {
            if(!plainReadyOn) {
                plainImageOn = generatePlainImage(true);
                plainReadyOn = true;
            }
            return plainImageOn;
        }
        else {
            if(!plainReadyOff) {
                plainImageOff = generatePlainImage(false);
                plainReadyOff = true;
            }
            return plainImageOff;
        }
    }

    @Override
    Image getHoveredImage() {
        if(on) {
            if(!hoveredReadyOn) {
                hoveredImageOn = generateHoveredImage(getPlainImage());
                hoveredReadyOn = true;
            }
            return hoveredImageOn;
        }
        else {
            if(!hoveredReadyOff) {
                hoveredImageOff = generateHoveredImage(getPlainImage());
                hoveredReadyOff = true;
            }
            return hoveredImageOff;
        }
    }

    @Override
    Image getPressedImage() {
        if(on) {
            if(!pressedReadyOn) {
                pressedImageOn = generatePressedImage(getPlainImage());
                pressedReadyOn = true;
            }
            return pressedImageOn;
        }
        else {
            if(!pressedReadyOff) {
                pressedImageOff = generatePressedImage(getPlainImage());
                pressedReadyOff = true;
            }
            return pressedImageOff;
        }
    }

    @Override
    Image getDisabledImage() {
        if(on) {
            if(!disabledReadyOn) {
                disabledImageOn = generateDisabledImage(getPlainImage());
                disabledReadyOn = true;
            }
            return disabledImageOn;
        }
        else {
            if(!disabledReadyOff) {
                disabledImageOff = generateDisabledImage(getPlainImage());
                disabledReadyOff = true;
            }
            return disabledImageOff;
        }
    }

    @Override
    protected Image generatePlainImage() {
        return generatePlainImage(on);
    }

    protected abstract Image generatePlainImage(boolean on);

    protected Image generateHoveredImage(Image plain, boolean on) {
        return generateHoveredImage(plain);
    }

    protected Image generatePressedImage(Image plain, boolean on) {
        return generatePressedImage(plain);
    }

    protected Image generateDisabledImage(Image plain, boolean on) {
        return generateDisabledImage(plain);
    }
}
