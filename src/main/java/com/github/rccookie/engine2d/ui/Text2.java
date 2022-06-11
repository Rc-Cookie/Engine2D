package com.github.rccookie.engine2d.ui;

import java.util.Map;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.ui.util.FormattedString;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.event.action.ParamAction;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Text2 extends UIObject {

    public final ColorProperty color = new ColorProperty(this, ThemeColor.FIRST);
    public final ColorProperty background = new ColorProperty(this, ThemeColor.CLEAR);

    private FormattedString text;
    private Font font = Font.DEFAULT;
    private boolean softWrap = false;
    @Nullable
    private ParamAction<int2> parentSizeChangeListener = null;

    @Nullable
    private Map<FormattedString.Segment, int2> posLookup = null;

    /**
     * Creates a new text with the given content.
     *
     * @param parent The parent for the text
     * @param text   The content of the text
     */
    public Text2(UIObject parent, @NotNull FormattedString text) {
        super(parent);
        this.text = Arguments.checkNull(text, "text");
    }

    public Text2(UIObject parent, @NotNull String formattedText) {
        this(parent, FormattedString.parse(formattedText));
    }

    @Override
    protected @Nullable Image generateImage() {
        int2 pos = int2.zero();

        for(FormattedString.Segment segment : text) {

        }
        return null;
    }
}
