package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.util.LongValidator;
import org.jetbrains.annotations.NotNull;

public class IntInputField extends ValueInputField<Long> {

    public IntInputField(UIObject parent, long titleAndDefault) {
        this(parent, ""+titleAndDefault, titleAndDefault);
        setGrayDefault(false);
    }

    public IntInputField(UIObject parent, @NotNull String title) {
        this(parent, title, 0);
    }

    public IntInputField(UIObject parent, long titleAndDefault, LongValidator validator) {
        this(parent, ""+titleAndDefault, titleAndDefault, validator);
        setGrayDefault(false);
    }

    public IntInputField(UIObject parent, @NotNull String title, long defaultVal) {
        this(parent, title, defaultVal, l->l);
    }

    public IntInputField(UIObject parent, @NotNull String title, long defaultVal, LongValidator validator) {
        super(parent, title, IntInputField::cutToLong, i -> {
            if("-".equals(i)) return defaultVal;
            try { return Long.parseLong(i); }
            catch (NumberFormatException e) { return defaultVal; }
        });
        setSubmitValidator(validator::validate);
        onSubmit.add(s -> { if("-".equals(s)) setValue(defaultVal); });
    }

    private static String cutToLong(String string) {
        if(string.isEmpty()) return ""; // Default value will be used
        StringBuilder cut = new StringBuilder();

        boolean minusPossible = true;
        for(char c : string.toCharArray()) {
            if((minusPossible && c == '-') || (c >= '0' && c <= '9')) {
                cut.append(c);
                minusPossible = false;
            }
        }

        return cut.toString();
    }
}
