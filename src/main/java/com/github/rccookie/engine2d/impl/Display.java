package com.github.rccookie.engine2d.impl;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.geometry.performance.IVec2;

import java.util.List;

public interface Display {

    void draw(List<DrawObject> objects, Color background);

    void setResolution(IVec2 resolution);

    void allowResizingChanged(boolean allowed);
}
