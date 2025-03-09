package com.madimadica.betterenv;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;

class ClassUtils {

    private static final Class<?> JDK_RECORD_TYPE;
    public static final boolean RUNTIME_HAS_RECORDS;

    static {
        boolean success = true;
        Class<?> tempRecordClass = null;
        try {
            tempRecordClass = Class.forName("java.lang.Record");
        } catch (ClassNotFoundException e) {
            success = false;
        }
        RUNTIME_HAS_RECORDS = success;
        JDK_RECORD_TYPE = RUNTIME_HAS_RECORDS ? tempRecordClass : null;
    }

    public static boolean isRecord(Class<?> clazz) {
        return ClassUtils.RUNTIME_HAS_RECORDS && (JDK_RECORD_TYPE == clazz.getSuperclass());
    }

    public static Field[] getInstanceFields(Class<?> clazz) {
        Field[] allFields = clazz.getDeclaredFields();
        return Arrays.stream(allFields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
    }

    public static Field[] getAnnotatedInstanceFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        Field[] allFields = clazz.getDeclaredFields();
        return Arrays.stream(allFields)
                .filter(field -> !Modifier.isStatic(field.getModifiers()) && field.isAnnotationPresent(annotation))
                .toArray(Field[]::new);
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Field[] fields) {
        Class<?>[] types = new Class<?>[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            types[i] = fields[i].getType();
        }
        try {
            return clazz.getDeclaredConstructor(types);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> Constructor<T> getAllArgsConstructor(Class<T> clazz) {
        return getConstructor(clazz, getInstanceFields(clazz));
    }

    public static <T> Constructor<T> getNoArgsConstructor(Class<T> clazz) {
        Class<?>[] types = new Class<?>[0];
        try {
            return clazz.getDeclaredConstructor(types);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T> T instantiate(Constructor<T> constructor, Object... initargs) {
        try {
            return constructor.newInstance(initargs);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Unable to instantiate type \"" + constructor.getParameterTypes()[0].getName() + "\"", e);
        }
    }

    public static <T> Object coerceType(String input, Class<T> type) {
        if (type.isPrimitive() && input == null) {
            throw new IllegalArgumentException("Cannot coerce null input to primitive type");
        } else if (input == null) {
            return null;
        } else if (type == String.class) {
            return input;
        } else if (type == boolean.class || type == Boolean.class) {
            String lower = input.toLowerCase();
            if ("true".equals(lower)) {
                return Boolean.TRUE;
            } else if ("false".equals(lower)) {
                return Boolean.FALSE;
            } else {
                throw new IllegalArgumentException("Expected 'true' or 'false' (case-insensitive)");
            }
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(input);
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(input);
        } else if (type == char.class || type == Character.class) {
            if (input.length() != 1) {
                throw new IllegalArgumentException("Character/char input must be length 1, instead found length " + input.length());
            }
            return input.charAt(0);
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(input);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(input);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(input);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(input);
        } else if (type == BigInteger.class) {
            return new BigInteger(input);
        } else if (type == BigDecimal.class) {
            return new BigDecimal(input);
        } else {
            throw new IllegalArgumentException("Unsupported type \"" + type.getName() + "\"");
        }
    }

}
