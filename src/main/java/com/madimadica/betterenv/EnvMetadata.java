package com.madimadica.betterenv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Internal type wrapping environment variable binding results for a single {@link Env}
 */
class EnvMetadata {

    /**
     * Key used for logging issues associated with {@link Env.Fallback} bindings.
     */
    public static final String FALLBACK_KEY = "@Env.Fallback";

    /**
     * Data about a single entry for each {@link Env#value()}.
     */
    static class Entry {
        /**
         * Environment variable name/key
         */
        private final String key;
        /**
         * Environment variable original value, which is nullable, returned by {@link System#getenv(String)}
         */
        private final String value;
        /**
         * The error message associated with a value requirement error, null if no error.
         */
        private String valueError;
        /**
         * The strongly typed coered value from {@link ClassUtils#coerceType(String, Class)}, nullable.
         */
        private Object coercedValue;
        /**
         * The error message associated with a bad type coercion, null if no error.
         */
        private String coercionError;

        /**
         * Construct an entry with a given environment variable name and raw value.
         * @param key name of the environment variable
         * @param value value of the environment variable, nullable
         */
        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * Check if this entry is valid, without considering the {@link Env#required()} attribute.
         * @return {@code true} if this value has no errors.
         */
        public boolean isValid() {
            return valueError == null && coercionError == null;
        }

        /**
         * Get the environment variable name/key associated with this entry.
         * @return String environment key used to resolve this value
         */
        public String getKey() {
            return key;
        }

        /**
         * Get the error message associated with this binding result, or null if there is no error;
         * @return String simple error message
         */
        public String getErrorMessage() {
            if (valueError != null) {
                return valueError;
            } else if (coercionError != null) {
                return coercionError;
            } else {
                return null;
            }
        }
    }

    /**
     * The field this environment data is mapped to
     */
    private final Field field;

    /**
     * The {@link Env} annotation on {@code this} {@link EnvMetadata#field}
     */
    private final Env annotation;

    /**
     * List of environment entries, one for each value in {@link Env#value()}
     */
    private final List<Entry> entries = new ArrayList<>();

    public EnvMetadata(Field field, Env annotation) {
        this.field = field;
        this.annotation = annotation;
    }

    /**
     * Check if the annotation is {@link Env#required()}
     * @return {@code true} if the annotation requires a non-null value.
     */
    public boolean isRequired() {
        return annotation.required();
    }

    /**
     * Add an entry to the list and attempt to coerce its type, storing any error messages on the {@link Entry} instance.
     * @param key Environment variable name
     * @param value Environment variable original value
     */
    public void addEntry(String key, String value) {
        Entry entry = new Entry(key, value);
        try {
            EnvMetadata.validateEnvVal(annotation, value);
            try {
                entry.coercedValue = ClassUtils.coerceType(value, field.getType());
            } catch (IllegalArgumentException e) {
                String error =  "Failed to coerce type to \"" + field.getType().getName() + "\": ";
                if (e instanceof NumberFormatException) {
                    error += "NumberFormatException"; // Redact NFE to prevent exposing env-value in logs
                } else {
                    error += e.getMessage();
                }
                entry.coercionError = error;
            }
        } catch (IllegalArgumentException e) {
            entry.valueError = e.getMessage();
        }
        entries.add(entry);
    }

    /**
     * Get the underlying field associated with {@code this}
     * @return the {@link Field} {@code this} metadata refers to.
     */
    public Field getField() {
        return field;
    }

    /**
     * Add a fallback value from {@link Env.Fallback}
     * @param fallback value from {@link Env.Fallback#value()} to use as a last resort
     */
    public void addFallback(String fallback) {
        this.addEntry(FALLBACK_KEY, fallback);
    }

    /**
     * Check if at least one entry is valid, no considering {@link Env#required()}
     * @return {@code true} if at least one entry is valid (no errors)
     */
    public boolean hasValidEntry() {
        for (Entry entry : entries) {
            if (entry.isValid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if this metadata has a valid binding, with consideration for {@link Env#required()}
     * @return {@code true} if there is a valid entry or invalid entries are allowed
     */
    public boolean isValid() {
        return !isRequired() || hasValidEntry();
    }

    /**
     * Get the first valid entry value. If no entries are valid and {@link Env#required()} is {@code true}, a {@link RuntimeException} is thrown. Otherwise {@code null} is returned.
     * @return The first strongly typed valid entry, or {@code null} if none could be resolved and {@link Env#required()} is {@code false}.
     * @throws RuntimeException If a valid entry is required and there are no valid entries.
     */
    public Object getFirstValue() {
        for (Entry entry : entries) {
            if (entry.isValid()) {
                return entry.coercedValue;
            }
        }
        if (isRequired()) {
            throw new RuntimeException("No valid entries found");
        }
        return null;
    }

    /**
     * Return a list of the invalid entries
     * @return list of the invalid entries.
     * @see Entry#isValid()
     */
    public List<Entry> getInvalidEntries() {
        return entries.stream().filter(e -> !e.isValid()).collect(Collectors.toList());
    }

    /**
     * Validate an environment value for errors
     * @param env {@link Env} configuration
     * @param value value to validate
     * @throws IllegalArgumentException if invalid, with a custom error message.
     */
    public static void validateEnvVal(Env env, String value) {
        if (value == null) {
            throw new IllegalArgumentException("Missing environment variable");
        }
        if (env.allowBlank()) {
            return;
        }
        // Cannot be blank, manual check since String#isBlank is JDK11
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (!Character.isWhitespace(ch)) {
                return;
            }
        }
        throw new IllegalArgumentException("Cannot be blank");
    }

}
