package com.github.rccookie.engine2d.ui.util;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;

import org.jetbrains.annotations.NotNull;

/**
 * Alignment allows to offset the actual position of the ui object so that
 * the ui object does not align with the center at the specified location,
 * but for example with the top left corner.
 */
public interface Alignment {

    /**
     * Aligns the ui object with its center.
     */
    Alignment CENTER       = (s,o) -> int2.zero;
    /**
     * Aligns the ui object with its top center.
     */
    Alignment TOP          = (s,o) -> new int2(0, s.y / 2 - 1);
    /**
     * Aligns the ui objects with its bottom center.
     */
    Alignment BOTTOM       = (s,o) -> new int2(0, s.y / -2 - 1);
    /**
     * Aligns the ui objects with its left center.
     */
    Alignment LEFT         = (s,o) -> new int2(s.x / 2 - 1, 0);
    /**
     * Aligns the ui objects with its right center.
     */
    Alignment RIGHT        = (s,o) -> new int2(s.x / -2 - 1, 0);
    /**
     * Aligns the ui object with its top left corner.
     */
    Alignment TOP_LEFT     = (s,o) -> new int2(s.x / 2 - 1, s.y / 2 - 1);
    /**
     * Aligns the ui object with its top right corner.
     */
    Alignment TOP_RIGHT    = (s,o) -> new int2(s.x / -2 - 1, s.y / 2 - 1);
    /**
     * Aligns the ui object with its bottom left corner.
     */
    Alignment BOTTOM_LEFT  = (s,o) -> new int2(s.x / 2 - 1, s.y / -2 - 1);
    /**
     * Aligns the ui object with its bottom right corner.
     */
    Alignment BOTTOM_RIGHT = (s,o) -> new int2(s.x / -2 - 1, s.y / -2 - 1);
    /**
     * Automatically aligns the ui object with the sides that are currently at
     * the outsides. At the same time, it will line up with the center when
     * located at the center. The values in between are a linear transition from
     * center alignment to side alignment. This alignment is usually the expected
     * one.
     */
    Alignment AUTO         = (s,o) -> new float2(s.x * -0.5f * o.relativeLoc.x - 1, s.y * -0.5f * o.relativeLoc.y - 1).toI();

    /**
     * Returns the offset for an ui object with the specified image size.
     *
     * @param size The image size
     * @param object The object to get the offset for. This information is
     *               usually not needed
     * @return The offset caused by the alignment
     */
    @NotNull
    int2 getOffset(int2 size, UIObject object);
}
