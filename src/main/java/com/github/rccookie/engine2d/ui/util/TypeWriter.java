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
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * A typewriter writes a string dynamically based on keys typed. It
 * supports multiple lines, advanced cursor navigation, selections
 * and a handful of shortcuts like ctrl+c / ctrl+v.
 */
public class TypeWriter {

    /**
     * The content that was typed.
     */
    private final StringBuilder string = new StringBuilder();

    /**
     * Position of the cursor in the string. The cursor
     * is in front of the character in the string with that index.
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    private int cursor = 0;
    /**
     * The other end of the selection (the cursor is one end).
     */
    private Integer selectionStart = null;


    /**
     * Whether newlines are allowed.
     */
    private final boolean allowNewline;
    /**
     * Whether submitting using enter is allowed.
     */
    private boolean allowSubmit = true;
    /**
     * Validator for converting a typed string into an allowed string.
     */
    @NotNull
    private UnaryOperator<String> validator;


    /**
     * Called whenever the content changes, with the content as
     * parameter.
     */
    public final ParamEvent<String> onChange = new CaughtParamEvent<>(false);
    /**
     * Called whenever the user presses enter without pressing shift or
     * control at the same time. Will not be called when submit is not
     * allowed.
     * <p>As parameter the current content will be passed.</p>
     */
    public final ParamEvent<String> onSubmit = new CaughtParamEvent<>(false);


    /**
     * Creates a new typewriter allowing newlines and with no validator.
     */
    public TypeWriter() {
        this(true, s->s);
    }

    /**
     * Creates a new typewriter.
     *
     * @param allowNewline Whether newlines are allowed
     * @param validator The validator to validate typed text
     */
    public TypeWriter(boolean allowNewline, @NotNull UnaryOperator<String> validator) {
        this.allowNewline = allowNewline;
        this.validator = Arguments.checkNull(validator);
    }

    /**
     * Returns the content of the typewriter.
     *
     * @return The current convent
     */
    @Override
    @NotNull
    public String toString() {
        return string.toString();
    }

    /**
     * Returns whether newlines are allowed.
     *
     * @return Whether newlines are allowed
     */
    public boolean isNewlineAllowed() {
        return allowNewline;
    }

    /**
     * Returns whether submitting using enter is allowed.
     *
     * @return Whether submit is allowed
     */
    public boolean isAllowSubmit() {
        return allowSubmit;
    }

    /**
     * Sets whether submitting using enter is allowed. This may be disabled to
     * avoid having to hold shift to write a newline.
     *
     * @param allowSubmit Whether to allow submit or not
     */
    public void setAllowSubmit(boolean allowSubmit) {
        this.allowSubmit = allowSubmit;
    }

    /**
     * Returns the current validator.
     *
     * @return The validator
     */
    @NotNull
    public UnaryOperator<String> getValidator() {
        return validator;
    }

    /**
     * Sets the validator to the given validator.
     *
     * @param validator The validator to use to validate typed text
     */
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

    /**
     * Returns whether text is currently selected.
     *
     * @return Whether any text is currently selected
     */
    public boolean hasSelection() {
        return selectionStart != null && selectionStart != cursor;
    }

    /**
     * Returns the current selection, where x is the lower bound of
     * the selection and y is the upper bound. If nothing is selected
     * this will return {@code null}.
     *
     * @return The current selection
     */
    public int2 getSelection() {
        if(selectionStart == null) return null;
        return new int2(Math.min(cursor, selectionStart), Math.max(cursor, selectionStart));
    }

    /**
     * Returns the cursors current position. The cursor is in front of
     * the character at that index.
     *
     * @return The position of the cursor
     */
    @Range(from = 0, to = Integer.MAX_VALUE)
    public int getCursor() {
        return cursor;
    }

    /**
     * Sets the position of the cursor to the given one.
     *
     * @param cursor The position to set
     */
    public void setCursor(@Range(from = 0, to = Integer.MAX_VALUE) int cursor) {
        if(this.cursor == cursor) return;
        Arguments.checkRange(cursor, 0, string.length()+1);
        this.cursor = cursor;
        selectionStart = null;
        onChange.invoke(toString());
    }

    /**
     * Sets the selection to the given selection.
     *
     * @param oneEnd One end of the selection
     * @param cursorEnd The end of the selection where the cursor is
     */
    public void setSelection(int oneEnd, int cursorEnd) {
        if(cursor == cursorEnd && selectionStart != null && selectionStart == oneEnd) return;
        Arguments.checkRange(cursorEnd, 0, string.length()+1);
        Arguments.checkRange(oneEnd, 0, string.length()+1);
        cursor = cursorEnd;
        selectionStart = oneEnd;
        onChange.invoke(toString());
    }

    /**
     * Sets the content of this typewriter. The string will still be validated.
     *
     * @param string The content to set
     */
    public void setString(@NotNull String string) {
        if(Arguments.checkNull(string).equals(toString())) return;
        this.string.delete(0, this.string.length()).append(validator.apply(string));
        cursor = Math.min(cursor, string.length());
        onChange.invoke(string);
    }

    /**
     * Informs the typewriter that the specified key was pressed.
     *
     * @param key The key that was pressed
     * @return Whether the typewriter "consumed" that key press or
     *         ignored it
     */
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
            isSubmit = allowSubmit && !Input.getKeyState("shift");
            if(Input.getKeyState("ctrl") && string.length() > 0) {
                selectionStart = null;
                if(Input.getKeyState("shift")) {
                    moveToStartOfLine();
                    if(cursor != 0) cursor--;
                }
                else moveToEndOfLine();
            }
            if(!isSubmit && allowNewline)
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

    /**
     * Returns whether the given character continues a text block that the cursor skips
     * at once when control is held down.
     *
     * @param c The character to test
     * @return Whether the character continues a block
     */
    private boolean continueBlock(char c) {
        c = Character.toLowerCase(c);
        return (c >= 'a' && c <= 'z') || (c >= '1' && c <= '9')
                || c == '\u00DF' || c == '\u00E4' || c == '\u00F6' || c == '\u00FC'
                || c == '%' || c == '&';
    }

    /**
     * Moves the cursor to the end of the current line.
     */
    private void moveToEndOfLine() {
        cursor = string.indexOf("\n", cursor);
        if(cursor == -1) cursor = string.length();
    }

    /**
     * Moves the cursor to the start of the current line.
     */
    private void moveToStartOfLine() {
        while(cursor > 0 && string.charAt(cursor-1) != '\n')
            cursor--;
    }

    /**
     * Writes the given string at the current cursor location and
     * moves the cursor forward accordingly.
     *
     * @param s The string to write.
     */
    private void write(String s) {
        write();
        if(!allowNewline) s = s.replace('\n', ' ');
        string.insert(cursor, s);
        cursor += s.length();
    }

    /**
     * Writes the given character at the current cursor location and
     * moves the cursor forward by 1.
     *
     * @param c The character to write
     */
    private void write(char c) {
        write();
        string.insert(cursor++, c);
    }

    /**
     * Writes "nothing", no characters are added but if characters are
     * selected they are overridden with nothing, i.e. removed.
     */
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
