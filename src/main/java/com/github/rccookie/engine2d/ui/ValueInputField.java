package com.github.rccookie.engine2d.ui;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

/**
 * A special kind of text input field which parses its content into a
 * value of a certain type.
 *
 * @param <T> The value type
 */
public class ValueInputField<T> extends TextInputField {

    /**
     * The parsing function.
     */
    @NotNull
    private Function<String, T> interpreter;
    /**
     * A validator used to validate parsed values.
     */
    @NotNull
    private UnaryOperator<T> submitValidator = t -> t;
    /**
     * Converter function used to convert a value back into a string.
     */
    @NotNull
    private Function<T, String> toString = T::toString;


    /**
     * The last parsed result.
     */
    private T lastResult = null;
    /**
     * Is {@link #lastResult} available?
     */
    private boolean hasLast = false;

    /**
     * Invoked whenever {@link #onSubmit} gets invoked, but with the
     * parsed value as parameter.
     */
    public final ParamEvent<T> onValue = new CaughtParamEvent<>(false);

    /**
     * Creates a new value input field.
     *
     * @param parent The parent for the input field
     * @param title The default value for the input field
     * @param writeValidator A validator that validates constantly while typing
     * @param interpreter A function that parses a validated string into a value
     */
    public ValueInputField(UIObject parent, @NotNull String title, @NotNull UnaryOperator<String> writeValidator, @NotNull Function<String, T> interpreter) {
        super(parent, title, false);
        setValidator(writeValidator);

        this.interpreter = Arguments.checkNull(interpreter, "interpreter");

        onSubmit.add(s -> onValue.invoke(setValue0(s)));
        onType.add(() -> hasLast = false);
    }

    /**
     * Returns the last submitted value. If no value was ever submitted,
     * the current value will be parsed.
     *
     * @return The current value
     */
    public T getValue() {
        if(!hasLast)
            setValue0(writer.toString());//setText(writer.toString());
        return lastResult;
    }

    /**
     * Sets the value of this input field to the given value.
     *
     * @param value The value to set
     */
    public void setValue(T value) {
        setText(toString.apply(value));
    }

    /**
     * Internally sets the given value.
     *
     * @param string The value to set
     * @return The parsed value
     */
    private T setValue0(String string) {
        hasLast = false;
        lastResult = submitValidator.apply(interpreter.apply(string));
        writer.setString(toString.apply(lastResult));
        // In case of an exception hasLast is false
        hasLast = true;
        return lastResult;
    }

    /**
     * Returns the submit validator which validates parsed values.
     *
     * @return The current submit validator
     */
    @NotNull
    public UnaryOperator<T> getSubmitValidator() {
        return submitValidator;
    }

    /**
     * Returns the current toString function to convert values back to text.
     *
     * @return The current toString function
     */
    @NotNull
    public Function<T, String> getToString() {
        return toString;
    }

    /**
     * Returns the current parsing function that parses validates strings into
     * values.
     *
     * @return The current interpreter
     */
    @NotNull
    public Function<String, T> getInterpreter() {
        return interpreter;
    }

    /**
     * Sets the submit validator which validates parsed values.
     *
     * @param submitValidator The submit validator to use
     */
    public void setSubmitValidator(@NotNull UnaryOperator<T> submitValidator) {
        this.submitValidator = Arguments.checkNull(submitValidator);
    }

    /**
     * Sets the toString function which converts values back to text to
     * the given function. The toString function should be effectively
     * the inverse of the interpreter function, meaning that
     * {@code value.equals(interpreter.apply(toString.apply(value))}
     * should be true for any non-null value.
     *
     * @param toString The toString function to use
     */
    public void setToString(@NotNull Function<T, String> toString) {
        this.toString = Arguments.checkNull(toString);
    }

    /**
     * Sets the parsing function that parses validated strings into
     * values.
     *
     * @param interpreter The function to set
     */
    public void setInterpreter(@NotNull Function<String, T> interpreter) {
        this.interpreter = Arguments.checkNull(interpreter);
    }
}
