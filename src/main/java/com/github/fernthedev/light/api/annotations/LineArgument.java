package com.github.fernthedev.light.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LineArgument {

    String name() default "";

    boolean required() default true;

    /**
     * Requires the field to be an object.
     * Allows the reflection initiator
     * to know what are the allowed class types
     * it can use for instantiating
     *
     * Example:
     * ```
     * @ LineArgument(classTypes = {boolean.class, int.class}
     * Object var; // The reflection initiator will only allow the object to be a boolean or an integer. If it's neither it will throw an exception
     * ```
     *
     * @return the allowed class types
     */
    Class[] classTypes() default {};
}


