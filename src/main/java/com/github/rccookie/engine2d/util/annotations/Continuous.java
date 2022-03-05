package com.github.rccookie.engine2d.util.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method must be called when overridden.
 * Thus, this annotation may not be used on final methods.
 * <p>This annotation has no runtime effect but is exclusively designed
 * for code clarity.</p>
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Continuous {
}
