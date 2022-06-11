package com.github.rccookie.engine2d.input;

import java.util.HashMap;
import java.util.Map;

import com.github.rccookie.event.CaughtParamEvent;
import com.github.rccookie.event.ParamEvent;
import com.github.rccookie.event.action.IAction;

public class Control {

    public final String name;
    public final InputRange range;

    public final ParamEvent<Float> onChange = new CaughtParamEvent<>();
    private final InputHandler handler = $ -> onBindingValueChange();

    private final Map<InputBinding, IAction> bindings = new HashMap<>();

    private float value;
    private boolean ready = false;


    public Control(String name, boolean allowNegative) {
        this.name = name;
        this.range = allowNegative ? InputRange.MINUS_ONE_TO_ONE : InputRange.ZERO_TO_ONE;
    }



    public void addBinding(InputBinding binding) {
        if(range == InputRange.ZERO_TO_ONE && binding.source.range == InputRange.MINUS_ONE_TO_ONE)
            throw new IllegalArgumentException("Binding with negative range not allowed");
        if(bindings.containsKey(binding)) return;
        bindings.put(binding, binding.bind(handler));
        ready = false;
    }

    public void removeBinding(InputBinding binding) {
        if(!bindings.containsKey(binding)) return;
        binding.unbind((bindings.remove(binding)));
        ready = false;
    }


    public float get() {
        if(ready) return value;

        float pos = 0, neg = 0;

        for(InputBinding binding : bindings.keySet()) {
            float v = binding.get();
            pos = Math.max(pos, v);
            neg = Math.min(neg, v);
        }

        ready = true;
        return value = pos + neg;
    }



    private void onBindingValueChange() {
        ready = false;
        onChange.invoke(get());
    }
}
