package com.github.rccookie.engine2d.ui.util;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Objects;
import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Input;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.geometry.performance.IVec2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class TypeWriter {

    private final StringBuilder string = new StringBuilder();
    @Range(from = 0, to = Integer.MAX_VALUE)
    private int cursor = 0;
    private Integer selectionStart = null;

    private final boolean allowNewline;
    @NotNull
    private UnaryOperator<String> validator;

    public final ParamEvent<String> onChange = new CaughtParamEvent<>(false);
    public final ParamEvent<String> onSubmit = new CaughtParamEvent<>(false);

    public TypeWriter() {
        this(true, s->s);
    }

    public TypeWriter(boolean allowNewline, @NotNull UnaryOperator<String> validator) {
        this.allowNewline = allowNewline;
        this.validator = Arguments.checkNull(validator);
    }

    @Override
    @NotNull
    public String toString() {
        return string.toString();
    }

    public boolean isNewlineAllowed() {
        return allowNewline;
    }

    public @NotNull UnaryOperator<String> getValidator() {
        return validator;
    }

    public boolean hasSelection() {
        return selectionStart != null && selectionStart != cursor;
    }

    public IVec2 getSelection() {
        if(selectionStart == null) return null;
        return new IVec2(Math.min(cursor, selectionStart), Math.max(cursor, selectionStart));
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getCursor() {
        return cursor;
    }

    public void setValidator(@NotNull UnaryOperator<String> validator) {
        this.validator = Arguments.checkNull(validator);
        String string = toString();
        String newS = validator.apply(string);
        if(string.equals(newS)) return;
        this.string.delete(0, this.string.length());
        this.string.append(newS);
        cursor = Math.min(cursor, newS.length());
        selectionStart = null;
        onChange.invoke(newS);
    }

    public void setCursor(@Range(from = 0, to = Integer.MAX_VALUE) int cursor) {
        if(this.cursor == cursor) return;
        Arguments.checkRange(cursor, 0, string.length()+1);
        this.cursor = cursor;
        selectionStart = null;
        onChange.invoke(toString());
    }

    public void setSelection(int oneEnd, int cursorEnd) {
        if(cursor == cursorEnd && selectionStart != null && selectionStart == oneEnd) return;
        Arguments.checkRange(cursorEnd, 0, string.length()+1);
        Arguments.checkRange(oneEnd, 0, string.length()+1);
        cursor = cursorEnd;
        selectionStart = oneEnd;
        onChange.invoke(toString());
    }

    public void setString(@NotNull String string) {
        if(Arguments.checkNull(string).equals(toString())) return;
        this.string.delete(0, this.string.length()).append(string);
        cursor = Math.min(cursor, string.length());
        onChange.invoke(string);
    }

    public boolean keyTyped(@NotNull String key) {
        String before = toString();
        int beforeCursor = cursor;
        Integer beforeSelectionStart = selectionStart;
        boolean isSubmit = false;

        if(selectionStart == null && Input.getKeyState("shift"))
            selectionStart = cursor;

        if(key.length() == 1) {
            char k = key.charAt(0);
            boolean ctrl = Input.getKeyState("ctrl"), alt = Input.getKeyState("alt");
            if(!ctrl && !alt) {
                if(Input.getKeyState("shift"))
                    k = Character.toUpperCase(k);
                write(k);
            } else {
                if(k >= 1 && k <= 26) k += 96;
                //noinspection StatementWithEmptyBody
                if(alt) {
                    // TODO: More commands?
                }
                else {
                    if(k == 'a') {
                        selectionStart = 0;
                        cursor = string.length();
                    } else if(!Application.getImplementation().supportsAWT()) {
                        return false;
                    } else if(k == 'c') {
                        if(selectionStart == null || selectionStart == cursor) return true;
                        StringSelection selection;
                        if(selectionStart < cursor) selection = new StringSelection(string.substring(selectionStart, cursor));
                        else selection = new StringSelection(string.substring(cursor, selectionStart));
                        try {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }else if(k == 'x') {
                        if(selectionStart == null || selectionStart == cursor) return true;
                        StringSelection selection;
                        if(selectionStart < cursor) {
                            selection = new StringSelection(string.substring(selectionStart, cursor));
                            string.delete(selectionStart, cursor);
                        }
                        else {
                            selection = new StringSelection(string.substring(cursor, selectionStart));
                            string.delete(cursor, selectionStart);
                        }
                        selectionStart = null;
                        try {
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else if(k == 'v') {
                        try {
                            Transferable data = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                            if(!data.isDataFlavorSupported(DataFlavor.stringFlavor)) return true;
                            String s = (String) data.getTransferData(DataFlavor.stringFlavor);
                            write(s);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else return false;
                }
            }
        } else if(key.equals("tab")) {
            write("    ");
            cursor += 4;
            // TODO: Different behavior on selection
        } else if(key.equals("backspace")) {
            if(selectionStart != null)
                write();
            else if(cursor > 0) {
                string.deleteCharAt(--cursor);
                if(Input.getKeyState("ctrl"))
                    while(cursor > 0 && continueBlock(string.charAt(cursor-1)))
                        string.deleteCharAt(--cursor);
            }
        } else if(key.equals("delete")) {
            if(selectionStart != null)
                write();
            else if(cursor < string.length()) {
                string.deleteCharAt(cursor);
                if(Input.getKeyState("ctrl"))
                    while(cursor < string.length() && continueBlock(string.charAt(cursor)))
                        string.deleteCharAt(cursor);
            }
        } else if(key.equals("enter")) {
            isSubmit = !Input.getKeyState("shift");
            if(Input.getKeyState("ctrl") && string.length() > 0) {
                selectionStart = null;
                if(Input.getKeyState("shift")) {
                    moveToStartOfLine();
                    if(cursor != 0) cursor--;
                }
                else moveToEndOfLine();
            }
            if(allowNewline)
                write('\n');
        } else if(key.equals("left")) {
            if(!Input.getKeyState("shift"))
                selectionStart = null;
            if(cursor > 0) {
                cursor--;
                if(Input.getKeyState("ctrl"))
                    while(cursor > 0 && continueBlock(string.charAt(cursor-1)))
                        cursor--;
            }
        } else if(key.equals("right")) {
            if(!Input.getKeyState("shift"))
                selectionStart = null;
            if(cursor < string.length()) {
                cursor++;
                if(Input.getKeyState("ctrl"))
                    while(cursor < string.length() && continueBlock(string.charAt(cursor)))
                        cursor++;
            }
        } else if(key.equals("up")) {
            if(!Input.getKeyState("shift"))
                selectionStart = null;
            moveToStartOfLine();
            if(cursor != 0) {
                int dist = beforeCursor - cursor;
                cursor--;
                moveToStartOfLine();
                for(int i=0; i<dist && string.charAt(cursor) != '\n'; i++)
                    cursor++;
            }
        } else if(key.equals("down")) {
            if(!Input.getKeyState("shift"))
                selectionStart = null;
            moveToStartOfLine();
            int dist = beforeCursor - cursor;
            moveToEndOfLine();
            if(cursor != string.length()) {
                cursor++;
                for(int i=0; i<dist && string.charAt(cursor) != '\n'; i++)
                    cursor++;
            }
        } else if(key.equals("home")) {
            if(!Input.getKeyState("shift"))
                selectionStart = null;
            if(Input.getKeyState("ctrl")) cursor = 0;
            else moveToStartOfLine();
        } else if(key.equals("end")) {
            if(!Input.getKeyState("shift"))
                selectionStart = null;
            if(Input.getKeyState("ctrl")) cursor = string.length();
            else moveToEndOfLine();
        } else return false;

        String now = toString();
        if(now.equals(before)) {
            // Invoke onChange before onSubmit
            if(cursor != beforeCursor || !Objects.equals(selectionStart, beforeSelectionStart))
                onChange.invoke(now);

            if(isSubmit) onSubmit.invoke(now);
            return true;
        }
        now = validator.apply(now);
        string.delete(0, string.length());
        string.append(now);
        if(now.equals(before)) cursor = beforeCursor;
        else cursor = Math.min(cursor, string.length());

        // Invoke onChange before onSubmit
        onChange.invoke(now);
        if(isSubmit) onSubmit.invoke(now);
        return true;
    }

    private boolean continueBlock(char c) {
        c = Character.toLowerCase(c);
        return (c >= 'a' && c <= 'z') || (c >= '1' && c <= '9')
                || c == '\u00DF' || c == '\u00E4' || c == '\u00F6' || c == '\u00FC'
                || c == '%' || c == '&';
    }

    private void moveToEndOfLine() {
        cursor = string.indexOf("\n", cursor);
        if(cursor == -1) cursor = string.length();
    }

    private void moveToStartOfLine() {
        while(cursor > 0 && string.charAt(cursor-1) != '\n')
            cursor--;
    }

    private void write(String s) {
        write();
        if(!allowNewline) s = s.replace('\n', ' ');
        string.insert(cursor, s);
        cursor += s.length();
    }

    private void write(char c) {
        write();
        string.insert(cursor++, c);
    }

    private void write() {
        if(selectionStart != null) {
            if(selectionStart != cursor) {
                if(selectionStart < cursor) {
                    string.delete(selectionStart, cursor);
                    cursor = selectionStart;
                }
                else string.delete(cursor, selectionStart);
            }
            selectionStart = null;
        }
    }
}
