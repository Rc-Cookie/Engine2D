package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.util.DoubleValidator;
import org.jetbrains.annotations.NotNull;

public class FloatInputField extends ValueInputField<Double> {

    public FloatInputField(UIObject parent, double titleAndDefault) {
        this(parent, ""+titleAndDefault, titleAndDefault);
        setGrayDefault(false);
    }

    public FloatInputField(UIObject parent, @NotNull String title) {
        this(parent, title, 0);
    }

    public FloatInputField(UIObject parent, double titleAndDefault, DoubleValidator validator) {
        this(parent, ""+titleAndDefault, titleAndDefault, validator);
        setGrayDefault(false);
    }

    public FloatInputField(UIObject parent, @NotNull String title, double defaultVal) {
        this(parent, title, defaultVal, l->l);
    }

    public FloatInputField(UIObject parent, @NotNull String title, double defaultVal, DoubleValidator validator) {
        super(parent, title, FloatInputField::cutToDouble, i -> {
            if("-".equals(i)) return defaultVal;
            try { return Double.parseDouble(i); }
            catch (NumberFormatException e) { return defaultVal; }
        });
        setSubmitValidator(validator::validate);
        onSubmit.add(s -> { if("-".equals(s)) setValue(defaultVal); });
    }

    private static String cutToDouble(String string) {
        if(string.isEmpty()) return ""; // Default value will be used
        StringBuilder cut = new StringBuilder();

        boolean minusPossible = true, pointIsPossible = true;
        for(char c : string.toCharArray()) {
            if(pointIsPossible && c == '.') {
                cut.append('.');
                pointIsPossible = minusPossible = false;
            }
            if((minusPossible && c == '-') || (c >= '0' && c <= '9')) {
                cut.append(c);
                minusPossible = false;
            }
        }

        return cut.toString();
    }
}
