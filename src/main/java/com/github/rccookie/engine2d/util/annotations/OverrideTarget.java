package com.github.rccookie.engine2d.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method is indented to be overridden, but cannot be abstract or
 * overriding is not necessary in every situation.
 * <p>May be used together with {@link Continuous} to indicate that while a method is
 * intended to be overridden, it does still contain important code that has to be
 * called when overriding.</p>
 * <p>This annotation has no runtime effect but is exclusively designed for code
 * clarity.</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface OverrideTarget {
}
