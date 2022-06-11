package com.github.rccookie.engine2d.ui.debug;

import com.github.rccookie.engine2d.*;
import com.github.rccookie.engine2d.core.stats.PerformanceStats;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.ui.ColorPanel;
import com.github.rccookie.engine2d.ui.KeyValueText;
import com.github.rccookie.engine2d.ui.SimpleList;
import com.github.rccookie.geometry.performance.int2;

/**
 * A panel that shows debug information.
 */
public class DebugPanel extends ColorPanel {

    /**
     * Creates a new debug display.
     *
     * @param parent The parent for the debug panel
     */
    public DebugPanel(UIObject parent) {
        super(parent, new int2(180, 180), Color.BLACK.setAlpha(0.3f));

        if(parent != null)
            parent.onChildChange.add((o,t) -> {
                if(t != ChangeType.REMOVED && !(o instanceof DebugPanel) && getParent() != null)
                    moveToTop();
            });

        // Default to top-left corner
        relativeLoc.set(1, -1);

        input.addKeyPressListener((Runnable) Debug::printUI, "f12");

        SimpleList list = new SimpleList(this, true);
        list.setOutsideGap(false);
        list.relativeLoc.x = -1;

        new FpsDisplay(list);
        new AutoRefreshText(list, () -> {
            GameObject gameObject = getCamera().getGameObject();
            if(gameObject == null) return "Objects on map: -";
            Map map = gameObject.getMap();
            if(map == null) return "Objects on map: -";
            return "Objects on map: " + map.getObjectCount();
        }, 1/30f);
        KeyValueText drawCount =          new KeyValueText(list, "Draw objects", "-");
        KeyValueText poolSize =           new KeyValueText(list, "Render pool", "-");
        KeyValueText updateDuration =     new KeyValueText(list, "Update time", "-");
        KeyValueText physicsDuration =    new KeyValueText(list, "Physics time", "-");
        KeyValueText uiUpdateDuration =   new KeyValueText(list, "UI update time", "-");
        KeyValueText renderPrepDuration = new KeyValueText(list, "Render prep", "-");
        KeyValueText renderDuration =     new KeyValueText(list, "Render time", "-");
        KeyValueText otherDuration =      new KeyValueText(list, "Other time", "-");
        KeyValueText parallel =           new KeyValueText(list, "Parallel", "-");
        KeyValueText bottleneck =         new KeyValueText(list, "Bottleneck", "-");

        // Gets stats only once
        execute.repeating(() -> {
            PerformanceStats stats = Application.getPerformanceStats();
            drawCount         .setValue(stats.drawCount);
            poolSize          .setValue(stats.poolSize);
            updateDuration    .setValue(stats.updateDuration);
            physicsDuration   .setValue(stats.physicsDuration);
            uiUpdateDuration  .setValue(stats.uiUpdateDuration);
            renderPrepDuration.setValue(stats.renderPrepDuration);
            renderDuration    .setValue(stats.renderDuration);
            otherDuration     .setValue(stats.otherDuration);
            parallel          .setValue(stats.parallel);
            bottleneck        .setValue(stats.bottleneck);
        }, 1/30f);

        for(UIObject o : list.getChildren()) o.relativeLoc.x = -1;
        for(UIObject o : this)
            o.setClickThrough(true);
    }
}
