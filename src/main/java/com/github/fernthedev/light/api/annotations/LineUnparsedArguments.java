package com.github.fernthedev.light.api.annotations;

import com.github.fernthedev.light.api.lines.ILightLine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the Field which must be String[]
 * the values that could not be parsed at the end
 * of the LightLine construction
 *
 * {@link ILightLine#validateUnparsedArguments()} is called if this annotation is used.
 * Use the method to parse the arguments that are used in runtime. Example usage
 * is {@link com.github.fernthedev.light.api.lines.LightAnimationLine}
 *
 * Requires {@link LineArgument} annotated
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LineUnparsedArguments {

    String name() default "";

    boolean required() default true;

}


