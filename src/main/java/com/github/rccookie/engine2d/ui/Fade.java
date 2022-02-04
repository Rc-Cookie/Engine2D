package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class Fade extends ColorPanel {

    private float progress = 0;
    private float duration = 1;
    private float speed = 1 / duration;
    private Boolean direction = null;

    public final ParamEvent<Boolean> onComplete = new CaughtParamEvent<>(false);

    public Fade(UIObject parent) {
        this(parent, ThemeColor.FIRST);
    }

    public Fade(UIObject parent, Color color) {
        this(parent, ThemeColor.of(color));
    }

    public Fade(UIObject parent, ThemeColor color) {
        super(parent, IVec2.ONE, ThemeColor.FIRST);
        setColor(t -> color.get(t).setAlpha(progress));
        setClickThrough(true);
        update.add(() -> {
            if(direction == null) return;
            float newProgress = progress + speed * Time.realDelta() * (direction ? 1 : -1);
            if(newProgress <= 0 || newProgress >= 1) {
                direction = null;
                setProgress(newProgress < 1 ? 0 : 1);
                onComplete.invoke(progress == 1);
            }
            else setProgress(newProgress);
        });
    }

    @NotNull
    @Override
    public IVec2 getSize() {
        UIObject parent = getParent();
        return parent == null ? IVec2.ZERO : parent.getSize();
    }

    @Override
    public void setSize(@NotNull IVec2 size) {
        throw new UnsupportedOperationException();
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        Arguments.checkRange(duration, 0f, null);
        this.duration = duration;
        this.speed = 1 / duration;
    }

    public void setProgress(float progress) {
        Arguments.checkInclusive(progress, 0f, 1f);
        if((int) (255 * (this.progress + 0.5f)) != (int) (255 * (progress + 0.5f)))
            modified();
        this.progress = progress;
    }

    public Boolean getDirection() {
        return direction;
    }
    
    public boolean isRunning() {
        return direction != null;
    }

    public void setRunning(boolean running, boolean direction) {
        this.direction = running ? direction : null;
    }

    public void fadeIn() {
        setProgress(0);
        direction = true;
    }

    public void fadeOut() {
        setProgress(1);
        direction = false;
    }
}
