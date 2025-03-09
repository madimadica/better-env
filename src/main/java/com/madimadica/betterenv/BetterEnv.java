package com.madimadica.betterenv;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * <p>
 *     Static entry point to load environment bindings to POJO types.
 * </p>
 * <p>
 *     Also provides simple Optional return values and number parsing with
 *     {@link BetterEnv#get(String)}, {@link BetterEnv#getInt(String)}, and {@link BetterEnv#getLong(String)}.
 * </p>
 *
 * @see BetterEnv#load(Class)
 * @see Env
 * @see Env.Fallback
 */
public class BetterEnv {

    /**
     * Hide the constructor
     */
    private BetterEnv() {}


    /**
     * <p>
     *     Load environment variables and attempt to bind them to a new instance of type {@code T}. Supports classes and records, even in older runtimes.
     *     This only binds variables to instance-variable types, and will bind to static variables.
     * </p>
     * <p>
     *     Invoke this with {@code MyType myType = BetterEnv.load(MyType.class)}
     * </p>
     * <p>
     *     For records, all fields/components must have an {@link Env} annotation provided. The canonical constructor will be used.
     * </p>
     * <p>
     *     For classes, any number of fields can be annotated with {@link Env}. There must be at least 1 valid constructor to use for these types, and you have 2 options.
     * </p>
     * <p>
     *     First, you can have a no-args constructor, which <em>does not</em> support {@code final} modifiers on {@link Env} fields. The valus are directly set and do not
     *     use any defined setters/mutators.
     * </p>
     * <p>
     *     Next, you can use an overloaded constructor, which must have each {@link Env} annotated field in the same declared order. This overloaded constructor
     *     will allow {@code final} modifiers on annotated fields, and does not update set values with reflection.
     * </p>
     * <p>
     *     If both the overloaded and no-args constructors are provided, the overloaded constructor is used. If neither are provided, an {@link InvalidEnvTypeException} is thrown.
     * </p>
     * <p>
     *     Fields may be automatically coerced into the following supported types: all primitives, all primitive wrappers, {@link String}, {@link java.math.BigDecimal}, {@link java.math.BigInteger}
     * </p>
     *
     * @param pojoType Type to load
     * @return an instance of type {@code T}
     * @param <T> type to bind/return
     * @throws InvalidEnvironmentException if the runtime environment has invalid environment variables required to bind to type {@code T}.
     * @throws InvalidEnvTypeException if the given type {@code T} has problems with the declared fields or constructors.
     */
    public static <T> T load(Class<T> pojoType) {
        boolean isRecord = ClassUtils.isRecord(pojoType);
        Field[] envFields = ClassUtils.getAnnotatedInstanceFields(pojoType, Env.class);
        return isRecord
                ? loadRecord(pojoType, envFields)
                : loadClass(pojoType, envFields);
    }

    /**
     * Load/bind a record type
     * @param pojoType Type to load
     * @param envFields fields annotated with {@link Env}
     * @return an instance of type {@code T}
     * @param <T> type to bind/return
     * @throws InvalidEnvironmentException if the runtime environment has invalid environment variables required to bind to type {@code T}.
     * @throws InvalidEnvTypeException if the given type {@code T} has problems with the declared fields or constructors.
     */
    private static <T> T loadRecord(Class<T> pojoType, Field[] envFields) {
        // All records must have a canonical constructor (all args)
        Constructor<T> constructor = ClassUtils.getAllArgsConstructor(pojoType);

        // All record fields should have @Env
        if (constructor.getParameterCount() != envFields.length) {
            throw new InvalidEnvTypeException("All fields on record type \"" + pojoType.getName() + "\" must be annotated with @Env");
        }

        Object[] args = getValuesForEnvFields(pojoType, envFields);
        return ClassUtils.instantiate(constructor, args);
    }

    /**
     * Load/bind a class type
     * @param pojoType Type to load
     * @param envFields fields annotated with {@link Env}
     * @return an instance of type {@code T}
     * @param <T> type to bind/return
     * @throws InvalidEnvironmentException if the runtime environment has invalid environment variables required to bind to type {@code T}.
     * @throws InvalidEnvTypeException if the given type {@code T} has problems with the declared fields or constructors.
     */
    private static <T> T loadClass(Class<T> pojoType, Field[] envFields) {
        // Prioritize using all-args constructor, otherwise no-args
        Constructor<T> constructor = ClassUtils.getConstructor(pojoType, envFields);
        boolean usingDefaultConstructor = constructor == null;
        if (usingDefaultConstructor) {
            constructor = ClassUtils.getNoArgsConstructor(pojoType);
        }
        if (constructor == null) {
            throw new InvalidEnvTypeException("No suitable constructor found for type \"" + pojoType.getName() + "\". Expected an all-env-args or no-args constructor to be defined.");
        }

        Object[] args = getValuesForEnvFields(pojoType, envFields);
        if (!usingDefaultConstructor) {
            return ClassUtils.instantiate(constructor, args);
        }

        T obj = ClassUtils.instantiate(constructor);

        // Bind values
        for (int i = 0; i < envFields.length; ++i) {
            Field field = envFields[i];
            Object value = args[i];
            if (Modifier.isFinal(field.getModifiers())) {
                throw new InvalidEnvTypeException("Cannot bind final field \"" + field.getName() + "\"");
            }
            field.setAccessible(true);
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                throw new InvalidEnvTypeException("Unable to bind field \"" + field.getName() + "\"", e);
            }
        }
        return obj;
    }

    /**
     * Process the {@link Env} and {@link Env.Fallback} annotations for the given fields into resolved types/values.
     * @param type Type to load
     * @param envFields fields annotated with {@link Env}
     * @return an {@code Object[]} of strongly typed values
     * @throws InvalidEnvironmentException if any environment variables fail to resolve to a valid type
     */
    static Object[] getValuesForEnvFields(Class<?> type, Field[] envFields) throws InvalidEnvironmentException {
        List<EnvMetadata> envMetadata = getEnvMetadata(envFields);

        if (envMetadata.stream().allMatch(EnvMetadata::isValid)) {
            return envMetadata.stream().map(EnvMetadata::getFirstValue).toArray();
        }

        // Unhappy path, at least one value is invalid
        StringBuilder sb = new StringBuilder();
        sb.append("Failed to load env data for type \"").append(type.getName()).append("\":");
        for (EnvMetadata metadata : envMetadata) {
            if (metadata.isValid()) {
                continue;
            }
            sb.append("\n\tField \"").append(metadata.getField().getName()).append("\":");
            for (EnvMetadata.Entry entry : metadata.getInvalidEntries()) {
                sb.append("\n\t\t\"").append(entry.getKey()).append("\": ").append(entry.getErrorMessage());
            }
        }
        throw new InvalidEnvironmentException(sb.toString());
    }

    /**
     * Process the {@link Env} and {@link Env.Fallback} annotations for the given fields into resolved types/values.
     * @param envFields fields annotated with {@link Env}
     * @return a list of {@link EnvMetadata} with the binding results for each field
     */
    static List<EnvMetadata> getEnvMetadata(Field[] envFields) {
        List<EnvMetadata> envMetadata = new ArrayList<>();
        for (Field field : envFields) {
            envMetadata.add(getEnvMetadata(field));
        }
        return envMetadata;
    }

    /**
     * Process the {@link Env} and {@link Env.Fallback} annotations for the given field.
     * @param field field annotated with {@link Env}
     * @return an {@link EnvMetadata} of binding results
     */
    static EnvMetadata getEnvMetadata(Field field) {
        Env env = field.getAnnotation(Env.class);
        EnvMetadata metadata = new EnvMetadata(field, env);

        for (String key : env.value()) {
            String value = System.getenv(key);
            metadata.addEntry(key, value);
        }

        if (metadata.hasValidEntry()) {
            return metadata;
        }

        Env.Fallback envFallback = field.getAnnotation(Env.Fallback.class);
        if (envFallback != null) {
            metadata.addFallback(envFallback.value());
        }

        return metadata;
    }

    /**
     * Try to find the environment variable value with the given name,
     * returning an empty optional if it doesn't exist, or a present
     * optional with the value.
     * @param name name of the environment variable
     * @return Optional String value of the environment variable
     */
    public static Optional<String> get(String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    /**
     * Try to find the environment variable value with the given name,
     * and try to call {@link Integer#parseInt(String)} on it. If the
     * value doesn't exist or cannot be parsed, an empty optional is returned.
     * @param name name of the environment variable
     * @return Optional Integer value of the environment variable
     */
    public static Optional<Integer> getInt(String name) {
        String value = System.getenv(name);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Try to find the environment variable value with the given name,
     * and try to call {@link Long#parseLong(String)} on it. If the
     * value doesn't exist or cannot be parsed, an empty optional is returned.
     * @param name name of the environment variable
     * @return Optional Long value of the environment variable
     */
    public static Optional<Long> getLong(String name) {
        String value = System.getenv(name);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

}
