package com.github.rccookie.engine2d.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is expected to always return the same value.
 * <p>This annotation has no runtime effect but is exclusively designed
 * for code clarity.</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Constant {

    /**
     * Whether the method is allowed to return an equivalent, but not
     * identical value on different method calls. This is {@code false}
     * by default.
     *
     * @return Whether the method is allowed to return an equal but not
     *         identical value
     */
    boolean equalsAllowed() default false;
}
