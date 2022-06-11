package com.github.rccookie.engine2d.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Input {

    private Input() {
        throw new UnsupportedOperationException();
    }

    private static final Set<String> CONTROLS = new HashSet<>();
    private static final Map<String, InputBinding> BINDINGS = new HashMap<>();
}
