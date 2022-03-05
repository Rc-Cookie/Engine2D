package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.util.DoubleValidator;
import org.jetbrains.annotations.NotNull;

/**
 * An input field that accepts floating point values.
 */
public class FloatInputField extends ValueInputField<Double> {

    /**
     * Creates a new float input field.
     *
     * @param parent The parent for the input field
     * @param titleAndDefault The default value and title
     */
    public FloatInputField(UIObject parent, double titleAndDefault) {
        this(parent, ""+titleAndDefault, titleAndDefault);
        setGrayDefault(false);
    }

    /**
     * Creates a new float input field with the default value 0.
     *
     * @param parent The parent for the input field
     * @param title The title for the input field
     */
    public FloatInputField(UIObject parent, @NotNull String title) {
        this(parent, title, 0);
    }

    /**
     * Creates a new float input field.
     *
     * @param parent The parent for the input field
     * @param titleAndDefault The default value and title
     * @param validator The validator for the value typed
     */
    public FloatInputField(UIObject parent, double titleAndDefault, DoubleValidator validator) {
        this(parent, ""+titleAndDefault, titleAndDefault, validator);
        setGrayDefault(false);
    }

    /**
     * Creates a new float input field.
     *
     * @param parent The parent for the input field
     * @param title The title for the input field
     * @param defaultVal The default value
     */
    public FloatInputField(UIObject parent, @NotNull String title, double defaultVal) {
        this(parent, title, defaultVal, l->l);
    }

    /**
     * Creates a new float input field.
     *
     * @param parent The parent for the input field.
     * @param title The title for the input field.
     * @param defaultVal The default value
     * @param validator The validator for the value typed
     */
    public FloatInputField(UIObject parent, @NotNull String title, double defaultVal, DoubleValidator validator) {
        super(parent, title, FloatInputField::cutToDouble, i -> {
            if("-".equals(i)) return defaultVal;
            try { return Double.parseDouble(i); }
            catch (NumberFormatException e) { return defaultVal; }
        });
        setSubmitValidator(validator::validate);
        onSubmit.add(s -> { if("-".equals(s)) setValue(defaultVal); });
    }

    /**
     * Cuts the given string so that the result contains a partial valid
     * double string.
     *
     * @param string The string to cut
     * @return The cut string
     */
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
