package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.Interpolation;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A color panel that can fade in and out. The fade always has the
 * size of its parent.
 */
public class Fade extends ColorPanel {

    /**
     * Progress in the fade in / out.
     */
    @Range(from = 0, to = 1)
    private float progress = 0;
    /**
     * Animation duration, in seconds.
     */
    private float duration = 1;
    /**
     * Speed, in % per second.
     */
    private float speed = 1 / duration;
    /**
     * {@code true} means fading in, {@code false} means fading
     * out, {@code null} means no animation.
     */
    private Boolean direction = null;

    /**
     * The interpolation used.
     */
    @NotNull
    private Interpolation interpolation = Interpolation.LINEAR;


    /**
     * Invoked whenever the fade animation completes, with the direction
     * as parameter. {@code true} means fade in, {@code false} means fade out.
     */
    public final ParamEvent<Boolean> onComplete = new CaughtParamEvent<>(false);


    /**
     * Creates a new fade object in the main theme color.
     *
     * @param parent The parent for the fade object
     */
    public Fade(UIObject parent) {
        this(parent, ThemeColor.FIRST);
    }

    /**
     * Creates a new fade object.
     *
     * @param parent The parent for the fade object
     * @param color The color of the fade object
     */
    public Fade(UIObject parent, Color color) {
        this(parent, ThemeColor.of(color));
    }

    /**
     * Creates a new fade object.
     *
     * @param parent The parent for the fade object
     * @param color The theme color of the fade object
     */
    public Fade(UIObject parent, ThemeColor color) {
        super(parent, int2.one, ThemeColor.FIRST);
        this.color.set(t -> color.get(t).setAlpha(interpolation.get(progress)));
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

    /**
     * Returns the size of the parent, or 0 of none is present.
     *
     * @return The size of this fade which is identical to the
     *         size of its parent
     */
    @NotNull
    @Override
    public int2 getSize() {
        UIObject parent = getParent();
        return parent == null ? int2.zero : parent.getSize();
    }

    /**
     * Not supported.
     */
    @Override
    public void setSize(@NotNull int2 size) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the animation duration.
     *
     * @return The fade duration
     */
    public float getDuration() {
        return duration;
    }

    /**
     * Sets the animation duration.
     *
     * @param duration The duration to use
     */
    public void setDuration(float duration) {
        Arguments.checkRange(duration, 0f, null);
        this.duration = duration;
        this.speed = 1 / duration;
    }

    /**
     * Returns the interpolation used.
     *
     * @return The current interpolation
     */
    @NotNull
    public Interpolation getInterpolation() {
        return interpolation;
    }

    /**
     * Sets the interpolation to use. Setting this while the
     * animation is running may cause a stutter effect.
     *
     * @param interpolation The interpolation to use
     */
    public void setInterpolation(@NotNull Interpolation interpolation) {
        if(this.interpolation == Arguments.checkNull(interpolation, "interpolation")) return;
        Interpolation old = this.interpolation;
        this.interpolation = interpolation;
        if((int) (255 * (old.get(progress) + 0.5f)) != (int) (255 * (interpolation.get(progress) + 0.5f)))
            modified();
    }

    /**
     * Returns the current progress of the animation.
     *
     * @return The animation progress
     */
    @Range(from = 0, to = 1)
    public float getProgress() {
        return progress;
    }

    /**
     * Sets the animation progress.
     *
     * @param progress The progress to set
     */
    public void setProgress(@Range(from = 0, to = 1) float progress) {
        Arguments.checkInclusive(progress, 0f, 1f);
        if((int) (255 * (interpolation.get(this.progress) + 0.5f)) != (int) (255 * (interpolation.get(progress) + 0.5f)))
            modified();
        this.progress = progress;
    }

    /**
     * Returns the current animation direction. {@code true} means
     * fade-in, {@code false} means fade-out. {@code null} means
     * no animation is currently running.
     *
     * @return The current animation direction
     */
    public Boolean getDirection() {
        return direction;
    }

    /**
     * Returns whether the animation is currently running.
     *
     * @return Whether the fade animation is running
     */
    public boolean isRunning() {
        return direction != null;
    }

    /**
     * Sets whether the animation to run or not, and if yes, in which
     * direction.
     *
     * @param running Whether to run the animation
     * @param direction {@code true} means fade-in, {@code false} means
     *                  fade-out. Ignored if running is {@code false}.
     * @see #stop()
     */
    public void setRunning(boolean running, boolean direction) {
        this.direction = running ? direction : null;
    }

    /**
     * Pauses the animation if it is running.
     */
    public void stop() {
        direction = null;
    }

    /**
     * Starts a fade-in.
     */
    public void fadeIn() {
        setProgress(0);
        direction = true;
    }

    /**
     * Starts a fade-out.
     */
    public void fadeOut() {
        setProgress(1);
        direction = false;
    }
}
