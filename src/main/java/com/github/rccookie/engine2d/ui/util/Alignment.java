package com.github.rccookie.engine2d.ui.util;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;
import org.jetbrains.annotations.NotNull;

public interface Alignment {

    Alignment CENTER       = s -> IVec2.ZERO;
    Alignment TOP          = s -> new IVec2(0, s.y / 2 - 1);
    Alignment BOTTOM       = s -> new IVec2(0, s.y / -2 - 1);
    Alignment LEFT         = s -> new IVec2(s.x / 2 - 1, 0);
    Alignment RIGHT        = s -> new IVec2(s.x / -2 - 1, 0);
    Alignment TOP_LEFT     = s -> new IVec2(s.x / 2 - 1, s.y / 2 - 1);
    Alignment TOP_RIGHT    = s -> new IVec2(s.x / -2 - 1, s.y / 2 - 1);
    Alignment BOTTOM_LEFT  = s -> new IVec2(s.x / 2 - 1, s.y / -2 - 1);
    Alignment BOTTOM_RIGHT = s -> new IVec2(s.x / -2 - 1, s.y / -2 - 1);
    Alignment AUTO         = new Alignment() {
        @NotNull
        @Override
        public IVec2 getOffset(IVec2 size) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public IVec2 getOffset(IVec2 s, UIObject o) {
            return new Vec2(s.x * -0.5f * o.relativeLoc.x - 1, s.y * -0.5f * o.relativeLoc.y - 1).toI();
        }
    };

    @NotNull
    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    IVec2 getOffset(IVec2 size);

    @NotNull
    default IVec2 getOffset(IVec2 size, UIObject object) {
        return getOffset(size);
    }
}
