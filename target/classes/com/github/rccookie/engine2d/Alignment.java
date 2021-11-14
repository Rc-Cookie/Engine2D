package com.github.rccookie.engine2d;

import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.geometry.performance.Vec2;

public interface Alignment {

    Alignment CENTER       = o -> IVec2.ZERO;
    Alignment TOP          = o -> new IVec2(0, o.getImage().size.y / 2 - 1);
    Alignment BOTTOM       = o -> new IVec2(0, o.getImage().size.y / -2 - 1);
    Alignment LEFT         = o -> new IVec2(o.getImage().size.x / 2 - 1, 0);
    Alignment RIGHT        = o -> new IVec2(o.getImage().size.x / -2 - 1, 0);
    Alignment TOP_LEFT     = o -> new IVec2(o.getImage().size.x / 2 - 1, o.getImage().size.y / 2 - 1);
    Alignment TOP_RIGHT    = o -> new IVec2(o.getImage().size.x / -2 - 1, o.getImage().size.y / 2 - 1);
    Alignment BOTTOM_LEFT  = o -> new IVec2(o.getImage().size.x / 2 - 1, o.getImage().size.y / -2 - 1);
    Alignment BOTTOM_RIGHT = o -> new IVec2(o.getImage().size.x / -2 - 1, o.getImage().size.y / -2 - 1);
    Alignment AUTO         = o -> new Vec2(o.getImage().size.x * -0.5f * o.relativeLoc.x - 1, o.getImage().size.y * -0.5f * o.relativeLoc.y - 1).toI();

    IVec2 getOffset(UIObject uiObject);
}
