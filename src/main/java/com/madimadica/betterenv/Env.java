package com.madimadica.betterenv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     Define the requirements for binding an environment variable to a class field.
 * </p>
 * <p>
 *     Allows multiple keys to be specified in the {@link Env#value()}.
 *     The first key-value pair that is valid will be used.
 * </p>
 * <p>
 *     If {@link Env#required()} is {@code true}, then an exception
 *     will be thrown during loading when a valid value cannot be resolved.
 *     If {@link Env#required()} is {@code false}, then a {@code null} value
 *     will be bound instead. This is {@code true} by default.
 * </p>
 * <p>
 *     If {@link Env#allowBlank()} is {@code true}, then blank Strings
 *     will be allowed as values, otherwise when it is {@code false} and
 *     a value is blank, an exception will be thrown during loading.
 *     This is {@code false} by default.
 * </p>
 * <p>
 *     If {@link Env#required()} is {@code false} and {@link Env#allowBlank()} is {@code false}, and the value is blank,
 *     then the resulting value will be {@code null}.
 * </p>
 * @see BetterEnv#load(Class) 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Env {
    /**
     * The key, or array of keys, to find the environment variable
     * @return the environment variable names to scan
     */
    String[] value();

    /**
     * If the value can be missing/null. Defaults to {@code true}
     * @return if this is a required (non-null) field
     */
    boolean required() default true;

    /**
     * If the value can be a blank String. Defaults to {@code false}.
     * @return if this allows blank Strings as valid values
     */
    boolean allowBlank() default false;

    /**
     * An additional annotation to specify a hardcoded fallback value if no valid environment variables were found in {@link Env}
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Fallback {
        /**
         * Hardcoded fallback value to use when no valid variables were found in {@link Env}
         * @return hardcoded value to default to if nothing else is valid
         */
        String value();
    }
}
