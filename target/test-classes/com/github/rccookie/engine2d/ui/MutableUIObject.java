package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.Image;
import com.github.rccookie.engine2d.UIObject;

import java.util.HashMap;
import java.util.Objects;

public abstract class MutableUIObject extends UIObject {

    private boolean modified = true;
    private boolean modifyLock = false;

    private final HashMap<String, Object> properties = new HashMap<>();

    protected void modified() {
        if(!modifyLock)
            modified = true;
    }

    @Override
    public Image getImage() {
        if(modified && !modifyLock) {
            modifyLock = true;
            setImage(generateImage());
            modifyLock = false;
        }
        return super.getImage();
    }

    protected abstract Image generateImage();


    @SuppressWarnings("unchecked")
    protected <T> T getProperty(String name) {
        return (T) properties.get(name);
    }

    protected void setProperty(String name, Object value) {
        if(!Objects.equals(properties.put(name, value), value))
            modified();
    }
}
