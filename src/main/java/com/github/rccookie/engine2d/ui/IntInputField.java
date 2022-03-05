package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.engine2d.util.LongValidator;
import org.jetbrains.annotations.NotNull;

public class IntInputField extends ValueInputField<Long> {

    /**
     * Creates a new int input field.
     *
     * @param parent The parent for the input field.
     * @param titleAndDefault The title and default value
     */
    public IntInputField(UIObject parent, long titleAndDefault) {
        this(parent, ""+titleAndDefault, titleAndDefault);
        setGrayDefault(false);
    }

    /**
     * Creates a new int input field with the default value 0.
     *
     * @param parent The parent for the input field.
     * @param title The title for the input field.
     */
    public IntInputField(UIObject parent, @NotNull String title) {
        this(parent, title, 0);
    }

    /**
     * Creates a new int input field.
     *
     * @param parent The parent for the input field.
     * @param titleAndDefault The title and default value
     * @param validator The validator for the value typed
     */
    public IntInputField(UIObject parent, long titleAndDefault, LongValidator validator) {
        this(parent, ""+titleAndDefault, titleAndDefault, validator);
        setGrayDefault(false);
    }

    /**
     * Creates a new int input field.
     *
     * @param parent The parent for the input field.
     * @param title The title for the input field.
     * @param defaultVal The default value
     */
    public IntInputField(UIObject parent, @NotNull String title, long defaultVal) {
        this(parent, title, defaultVal, l->l);
    }

    /**
     * Creates a new int input field.
     *
     * @param parent The parent for the input field.
     * @param title The title for the input field.
     * @param defaultVal The default value
     * @param validator The validator for the value typed
     */
    public IntInputField(UIObject parent, @NotNull String title, long defaultVal, LongValidator validator) {
        super(parent, title, IntInputField::cutToLong, i -> {
            if("-".equals(i)) return defaultVal;
            try { return Long.parseLong(i); }
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
