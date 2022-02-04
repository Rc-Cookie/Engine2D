package com.github.rccookie.engine2d.ui;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.UIObject;
import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.util.Arguments;
import org.jetbrains.annotations.NotNull;

public class ValueInputField<T> extends TextInputField {

    @NotNull
    private Function<String, T> interpreter;
    @NotNull
    private UnaryOperator<T> submitValidator = t -> t;
    @NotNull
    private Function<T, String> toString = T::toString;

    private T lastResult = null;
    private boolean hasLast = false;

    public final ParamEvent<T> onValue = new CaughtParamEvent<>(false);

    public ValueInputField(UIObject parent, @NotNull String title, @NotNull UnaryOperator<String> writeValidator, @NotNull Function<String, T> interpreter) {
        super(parent, title, false, writeValidator);

        this.interpreter = Arguments.checkNull(interpreter, "interpreter");

        onSubmit.add(s -> { onValue.invoke(setValue0(s)); });
        onWrite.add(() -> hasLast = false);
    }

    public T getValue() {
        if(!hasLast)
            setValue(writer.toString());
        return lastResult;
    }

    public void setValue(T value) {
        setValue(toString.apply(value));
    }

    public void setValue(String string) {
        writer.setString(string);
        // hasLast will be modified during onWrite event
    }

    private T setValue0(String string) {
        hasLast = false;
        lastResult = submitValidator.apply(interpreter.apply(string));
        writer.setString(toString.apply(lastResult));
        // In case of an exception hasLast is false
        hasLast = true;
        return lastResult;
    }

    @NotNull
    public UnaryOperator<T> getSubmitValidator() {
        return submitValidator;
    }

    @NotNull
    public Function<T, String> getToString() {
        return toString;
    }

    @NotNull
    public Function<String, T> getInterpreter() {
        return interpreter;
    }

    public void setSubmitValidator(@NotNull UnaryOperator<T> submitValidator) {
        this.submitValidator = Arguments.checkNull(submitValidator);
    }

    public void setToString(@NotNull Function<T, String> toString) {
        this.toString = Arguments.checkNull(toString);
    }

    public void setInterpreter(@NotNull Function<String, T> interpreter) {
        this.interpreter = Arguments.checkNull(interpreter);
    }
}
