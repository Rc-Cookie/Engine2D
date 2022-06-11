package com.github.rccookie.engine2d.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.rccookie.engine2d.Time;
import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.image.Font;
import com.github.rccookie.engine2d.image.Image;
import com.github.rccookie.engine2d.image.RenderResult;
import com.github.rccookie.engine2d.image.ThemeColor;
import com.github.rccookie.engine2d.ui.util.TypeWriter;
import com.github.rccookie.engine2d.util.ColorProperty;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextEditor extends Interactable {

    final TypeWriter writer = new TypeWriter();
    private Font font = Font.MONOSPACE;

    private boolean focus = false;
    private float lastChanged = Time.realTime();

    private boolean editable = true;
    private boolean hardWrap = false;

    @NotNull
    private String[] renderedLines = { "" };

    public final ColorProperty backgroundColor = new ColorProperty(this, ThemeColor.FIRST);
    public final ColorProperty textColor = new ColorProperty(this, ThemeColor.TEXT_FIRST);
    public final ColorProperty cursorColor = new ColorProperty(this, ThemeColor.FIRST.complement());
    public final ColorProperty selectionColor = new ColorProperty(this, t -> t.accent.setAlpha(0.3f));

    private Integer mouseSelectionStart = null;

    /**
     * Called whenever the user types something or deletes something,
     * with the current content as parameter.
     */
    public final ParamEvent<String> onType;

    public TextEditor(@Nullable UIObject parent) {
        super(parent);
        onParentSizeChange.add(this::modified);

        writer.onChange.add(() -> lastChanged = Time.realTime());
        writer.onChange.add(this::modified);
        onType = writer.onChange;
        writer.setAllowSubmit(false);

        onEnable.add(e -> setFocus(e && focus));
        input.keyPressed.addConsuming(k -> {
            if(!focus || !"esc".equals(k)) return false;
            setFocus(false);
            return true;
        });
        input.addKeyPressListener(() -> setFocus(false), "esc");
        onClick.add(() -> setFocus(true)); // When selected from program
        onPress.add(() -> setFocus(true));
        onPress.add(() -> writer.setCursor(mouseSelectionStart = getMouseCursor()));
        onRelease.add(() -> mouseSelectionStart = null);
        input.mousePressed.add(m -> setFocus(getUI().getObjectAtMouse() == this));
        update.add(() -> {
                if(!focus || mouseSelectionStart == null || !input.getMouse().pressed) return;
                writer.setSelection(mouseSelectionStart, getMouseCursor());
        });

        execute.repeating(() -> { if(focus && !writer.hasSelection() && Time.realTime() - lastChanged >= 0.8f) modified(); }, 0.5f);

        input.keyPressed.addConsuming(k -> editable && focus && writer.keyTyped(k));
    }

    private int getMouseCursor() {
        int2 mouse = input.getMouse().pixel.subed(getScreenPos()).add(getSize().dived(2));
        getImage(); // Ensure rendered lines is correct

        int cursor = 0;
        int line = 0;
        for(; line<renderedLines.length && line<mouse.y/font.size; line++)
            cursor += renderedLines[line].length();
        if(line == renderedLines.length) {
            line--;
            cursor -= renderedLines[line].length();
        }

        for(int width=0,i=0; i<renderedLines[line].length() && renderedLines[line].charAt(i)!='\n' && mouse.x-font.charSize(renderedLines[line].charAt(i)).x/2>=width; i++, cursor++)
            width += font.charSize(renderedLines[line].charAt(i)).x;

        return cursor;
    }

    public String getText() {
        return writer.toString();
    }

    public Font getFont() {
        return font;
    }

    public void setText(String text) {
        writer.setString(text);
    }

    public void setFont(Font font) {
        if(this.font.equals(Arguments.checkNull(font, "font"))) return;
        this.font = font;
        modified();
    }

    public void setFocus(boolean focus) {
        if(this.focus == focus) return;
        this.focus = focus;
        modified();
    }

    public boolean hasFocus() {
        return focus;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isHardWrap() {
        return hardWrap;
    }

    public void setHardWrap(boolean hardWrap) {
        if(this.hardWrap == hardWrap) return;
        this.hardWrap = hardWrap;
        modified();
    }

    @Override
    protected @Nullable Image generateImage() {
        Image image = new Image(clampSize(getParent().getSize()), backgroundColor.get());
        // Draw text
        if(hardWrap) {
            int h = 0;
            List<String> wrappedLines = new ArrayList<>();
            outer: for(String line : writer.toString().split("\r?\n")) {
                if(line.isEmpty())
                    wrappedLines.add("\n");
                else {
                    do {
                        RenderResult result = font.tryRenderChars(line, textColor.get(), image.size.x);
                        image.drawImage(result.image, new int2(0, h));
                        wrappedLines.add(result.rendered);
                        line = result.remaining;
                        if((h += result.image.size.y) >= image.size.y) break outer;
                    } while(!line.isEmpty());
                    int last = wrappedLines.size()-1;
                    wrappedLines.set(last, wrappedLines.get(last) + "\n");
                }
            }
            this.renderedLines = wrappedLines.toArray(new String[0]);
        }
        else {
            String text = writer.toString();
            image.drawImage(font.render(text, textColor.get()), int2.zero);
            renderedLines = Arrays.stream(text.split("\r?\n", -1)).map(s -> s+'\n').toArray(String[]::new);
            int last = renderedLines.length-1;
            renderedLines[last] = renderedLines[last].substring(0, renderedLines[last].length()-1);
        }

        // Draw cursor and selection
        if(focus) {
            int2 cSize = font.charSize(' ');
            int2 cursor = writer.getCursorPos().mul(cSize);

            if(writer.hasSelection()) {
                int2 selection = writer.getSelection();
                int2 start = writer.getPos(selection.x), end = writer.getPos(selection.y);
                Color selectionColor = this.selectionColor.get();

                if(start.y == end.y) {
                    image.fillRect(start.multed(cSize), new int2(cSize.x * Num.max(1, end.x - start.x), font.size), selectionColor);
                }
                else {
                    image.fillRect(start.multed(cSize), new int2(Num.max(1, image.size.x), font.size), selectionColor);
                    for(int i=start.y+1; i<end.y; i++)
                        image.fillRect(new int2(0, i * cSize.y), new int2(image.size.x, font.size), selectionColor);
                    image.fillRect(new int2(0, end.y * cSize.y), new int2(end.x * cSize.x, font.size), selectionColor);
                }
            }

            if(writer.hasSelection() || (editable && Time.realTime() - lastChanged < 0.8f || Time.realTime() % 1f < 0.5f))
                image.fillRect(cursor, new int2(font.size / 10, font.size), cursorColor.get());
        }

        return image;
    }
}
