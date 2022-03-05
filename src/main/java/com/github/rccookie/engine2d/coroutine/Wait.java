package com.github.rccookie.engine2d.coroutine;

import com.github.rccookie.engine2d.Time;

public interface Wait {

    Wait aMoment = () -> true;

    boolean isDone();

    static Wait forNextFrame() {
        long frame = Time.frame();
        return () -> Time.frame() != frame;
    }

    static Wait seconds(float seconds) {
        return seconds(seconds, false);
    }

    static Wait seconds(float seconds, boolean realTime) {
        if(realTime) {
            float target = Time.realTime() + seconds;
            return () -> Time.realTime() >= target;
        }
        float target = Time.time();
        return () -> Time.time() >= target;
    }

    static Wait until(float time) {
        return until(time, false);
    }

    static Wait until(float time, boolean realTime) {
        return realTime ?
                () -> Time.realTime() >= time :
                () -> Time.time() >= time;
    }
}
