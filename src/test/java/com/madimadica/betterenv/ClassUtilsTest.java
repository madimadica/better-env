package com.madimadica.betterenv;

import com.madimadica.betterenv.classes.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClassUtilsTest {

    @Test
    @EnabledForJreRange(max=JRE.JAVA_15)
    void givenOldJdk_thenIsRecordsArentSupported() {
        assertFalse(ClassUtils.RUNTIME_HAS_RECORDS);
    }

    @Test
    @EnabledForJreRange(min=JRE.JAVA_16)
    void givenNewJdk_thenRecordsAreSupported() {
        assertTrue(ClassUtils.RUNTIME_HAS_RECORDS);
    }

    @Test
    void givenOnlyStaticFields_whenGetInstanceFields_thenReturnEmptyArray() {
        Field[] fields = ClassUtils.getInstanceFields(OnlyStaticFields.class);
        assertEquals(0, fields.length);
    }

    @Test
    void givenStaticAndInstanceFields_whenGetInstanceFields_thenReturnInstanceFields() throws NoSuchFieldException {
        Field[] fields = ClassUtils.getInstanceFields(StaticAndInstanceFields.class);
        assertEquals(4, fields.length);
        assertEquals(fields[0], StaticAndInstanceFields.class.getDeclaredField("fPublic"));
        assertEquals(fields[1], StaticAndInstanceFields.class.getDeclaredField("fPrivate"));
        assertEquals(fields[2], StaticAndInstanceFields.class.getDeclaredField("fProtected"));
        assertEquals(fields[3], StaticAndInstanceFields.class.getDeclaredField("fPackagePrivate"));
    }

    @Test
    void givenOnlyInstanceFields_whenGetInstanceFields_thenReturnInstanceFields() throws NoSuchFieldException {
        Field[] fields = ClassUtils.getInstanceFields(OnlyInstanceFields.class);
        assertEquals(4, fields.length);
        assertEquals(fields[0], OnlyInstanceFields.class.getDeclaredField("fPublic"));
        assertEquals(fields[1], OnlyInstanceFields.class.getDeclaredField("fPrivate"));
        assertEquals(fields[2], OnlyInstanceFields.class.getDeclaredField("fProtected"));
        assertEquals(fields[3], OnlyInstanceFields.class.getDeclaredField("fPackagePrivate"));
    }

    @Test
    void givenOnlyFinalInstanceFields_whenGetInstanceFields_thenReturnInstanceFields() throws NoSuchFieldException {
        Field[] fields = ClassUtils.getInstanceFields(OnlyFinalInstanceFields.class);
        assertEquals(4, fields.length);
        assertEquals(fields[0], OnlyFinalInstanceFields.class.getDeclaredField("fPublic"));
        assertEquals(fields[1], OnlyFinalInstanceFields.class.getDeclaredField("fPrivate"));
        assertEquals(fields[2], OnlyFinalInstanceFields.class.getDeclaredField("fProtected"));
        assertEquals(fields[3], OnlyFinalInstanceFields.class.getDeclaredField("fPackagePrivate"));
    }

    @Test
    void givenNoAnnotatedFields_whenGetAnnotatedFields_thenReturnEmptyArray() {
        Field[] fields = ClassUtils.getAnnotatedInstanceFields(OnlyInstanceFields.class, FieldAnnotation.class);
        assertEquals(0, fields.length);
    }

    @Test
    void givenSomeAnnotatedFields_whenGetAnnotatedFields_thenReturnThoseFields() throws NoSuchFieldException {
        Field[] fields = ClassUtils.getAnnotatedInstanceFields(AnnotatedSomeFields.class, FieldAnnotation.class);
        assertEquals(2, fields.length);
        assertEquals(fields[0], AnnotatedSomeFields.class.getDeclaredField("fPublic"));
        assertEquals(fields[1], AnnotatedSomeFields.class.getDeclaredField("fPrivate"));
    }

    @Test
    void givenAllAnnotatedFields_whenGetAnnotatedFields_thenReturnAllFields() throws NoSuchFieldException {
        Field[] fields = ClassUtils.getAnnotatedInstanceFields(AnnotatedAllFields.class, FieldAnnotation.class);
        assertEquals(4, fields.length);
        assertEquals(fields[0], AnnotatedAllFields.class.getDeclaredField("fPublic"));
        assertEquals(fields[1], AnnotatedAllFields.class.getDeclaredField("fPrivate"));
        assertEquals(fields[2], AnnotatedAllFields.class.getDeclaredField("fProtected"));
        assertEquals(fields[3], AnnotatedAllFields.class.getDeclaredField("fPackagePrivate"));
    }

    @Test
    void getAllArgsConstructor() {
        Constructor<WithAllArgsConstructor> constructor = ClassUtils.getAllArgsConstructor(WithAllArgsConstructor.class);
        assertNotNull(constructor);
    }

    @Test
    void getNoArgsConstructor() {
        Constructor<OnlyStaticFields> constructor = ClassUtils.getNoArgsConstructor(OnlyStaticFields.class);
        assertNotNull(constructor);
    }

    @Test
    void givenSomeArgsConstructor_thenGetConstructor() {
        Field[] fields = ClassUtils.getInstanceFields(WithSomeArgsConstructor.class);
        Field[] someFields = new Field[] { fields[0], fields[1] };
        Constructor<WithSomeArgsConstructor> constructor = ClassUtils.getConstructor(WithSomeArgsConstructor.class, someFields);
        assertNotNull(constructor);
    }

    @Test
    void givenWrongFields_whenGetConstructor_thenReturnNull() {
        Field[] fields = ClassUtils.getInstanceFields(WithSomeArgsConstructor.class);
        Field[] someFields = new Field[] { fields[0], fields[0], fields[0], fields[0], fields[0] };
        Constructor<WithSomeArgsConstructor> constructor = ClassUtils.getConstructor(WithSomeArgsConstructor.class, someFields);
        assertNull(constructor);

    }

    @Test
    void givenAllArgsConstructor_thenInstantiate() {
        Constructor<WithAllArgsConstructor> constructor = ClassUtils.getAllArgsConstructor(WithAllArgsConstructor.class);
        assertNotNull(constructor);
        WithAllArgsConstructor instance = ClassUtils.instantiate(constructor, "Hi", 1L);
        assertNotNull(instance);
    }

    @Test
    void givenNoArgsConstructor_whenGetAllArgsConstructor_thenReturnNull() {
        Constructor<OnlyInstanceFields> constructor = ClassUtils.getAllArgsConstructor(OnlyInstanceFields.class);
        assertNull(constructor);
    }

    @Test
    void givenAllArgsConstructor_whenGetNoArgsConstructor_thenReturnNull() {
        Constructor<WithAllArgsConstructor> constructor = ClassUtils.getNoArgsConstructor(WithAllArgsConstructor.class);
        assertNull(constructor);
    }

    @Test
    void givenNoArgsConstructor_thenInstantiate() {
        Constructor<OnlyStaticFields> constructor = ClassUtils.getNoArgsConstructor(OnlyStaticFields.class);
        assertNotNull(constructor);
        OnlyStaticFields instance = ClassUtils.instantiate(constructor);
        assertNotNull(instance);
    }

    @Test
    void givenSomeArgsConstructor_thenInstantiate() {
        Field[] fields = ClassUtils.getInstanceFields(WithSomeArgsConstructor.class);
        Field[] someFields = new Field[] { fields[0], fields[1] };
        Constructor<WithSomeArgsConstructor> constructor = ClassUtils.getConstructor(WithSomeArgsConstructor.class, someFields);
        assertNotNull(constructor);
        WithSomeArgsConstructor instance = ClassUtils.instantiate(constructor, "Hi", 1L);
        assertNotNull(instance);
    }

    @Test
    void givenWrongArgs_whenInstantiate_thenThrow() {
        Constructor<OnlyInstanceFields> constructor = ClassUtils.getNoArgsConstructor(OnlyInstanceFields.class);
        assertNotNull(constructor);
        assertThrows(RuntimeException.class, () -> ClassUtils.instantiate(constructor, "foo", "bar", "baz"));
    }

    @Test
    void coerceType_nullPrimitives() {
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, boolean.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, char.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, byte.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, short.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, int.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, long.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, float.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType(null, double.class));
    }

    @Test
    void givenNull_whenCoerceReferenceType_thenReturnNull() {
        assertNull(ClassUtils.coerceType(null, Boolean.class));
        assertNull(ClassUtils.coerceType(null, Character.class));
        assertNull(ClassUtils.coerceType(null, Byte.class));
        assertNull(ClassUtils.coerceType(null, Short.class));
        assertNull(ClassUtils.coerceType(null, Integer.class));
        assertNull(ClassUtils.coerceType(null, Long.class));
        assertNull(ClassUtils.coerceType(null, Float.class));
        assertNull(ClassUtils.coerceType(null, Double.class));
        assertNull(ClassUtils.coerceType(null, String.class));
        assertNull(ClassUtils.coerceType(null, BigInteger.class));
        assertNull(ClassUtils.coerceType(null, BigDecimal.class));
    }

    @Test
    void coerceType_String() {
        assertEquals("foo", ClassUtils.coerceType("foo", String.class));
    }

    @Test
    void coerceType_Boolean() {
        assertEquals(true, ClassUtils.coerceType("true", boolean.class));
        assertEquals(true, ClassUtils.coerceType("TRUE", boolean.class));
        assertEquals(false, ClassUtils.coerceType("false", boolean.class));
        assertEquals(false, ClassUtils.coerceType("FALSE", boolean.class));
        assertEquals(true, ClassUtils.coerceType("true", Boolean.class));
        assertEquals(true, ClassUtils.coerceType("TRUE", Boolean.class));
        assertEquals(false, ClassUtils.coerceType("false", Boolean.class));
        assertEquals(false, ClassUtils.coerceType("FALSE", Boolean.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("", Boolean.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("", boolean.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("1", boolean.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("1", Boolean.class));
    }

    @Test
    void coerceType_Byte() {
        assertEquals((byte) 5, ClassUtils.coerceType("5", byte.class));
        assertEquals((byte) 5, ClassUtils.coerceType("5", Byte.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", byte.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", Byte.class));
    }

    @Test
    void coerceType_Short() {
        assertEquals((short) 5, ClassUtils.coerceType("5", short.class));
        assertEquals((short) 5, ClassUtils.coerceType("5", Short.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", short.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", Short.class));
    }

    @Test
    void coerceType_Char() {
        assertEquals('a', ClassUtils.coerceType("a", char.class));
        assertEquals('a', ClassUtils.coerceType("a", Character.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("", char.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("too long", char.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("", Character.class));
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("too long", Character.class));
    }

    @Test
    void coerceType_Int() {
        assertEquals(5, ClassUtils.coerceType("5", int.class));
        assertEquals(5, ClassUtils.coerceType("5", Integer.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", int.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", Integer.class));
    }

    @Test
    void coerceType_Long() {
        assertEquals(5L, ClassUtils.coerceType("5", long.class));
        assertEquals(5L, ClassUtils.coerceType("5", Long.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", long.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", Long.class));
    }

    @Test
    void coerceType_Float() {
        assertEquals(2.25f, ClassUtils.coerceType("2.25", float.class));
        assertEquals(2.25f, ClassUtils.coerceType("2.25", Float.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", float.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", Float.class));
    }

    @Test
    void coerceType_Double() {
        assertEquals(2.25, ClassUtils.coerceType("2.25", double.class));
        assertEquals(2.25, ClassUtils.coerceType("2.25", Double.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", double.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("", Double.class));
    }

    @Test
    void coerceType_BigInteger() {
        String input = "12345678901234567890123456789012345678901234567890";
        assertEquals(new BigInteger(input), ClassUtils.coerceType(input, BigInteger.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("foo", BigInteger.class));
    }

    @Test
    void coerceType_BigDecimal() {
        String input = "1.2345678901234567890123456789";
        assertEquals(new BigDecimal(input), ClassUtils.coerceType(input, BigDecimal.class));
        assertThrows(NumberFormatException.class, () -> ClassUtils.coerceType("foo", BigDecimal.class));
    }

    @Test
    void coerceType_Unsupported() {
        assertThrows(IllegalArgumentException.class, () -> ClassUtils.coerceType("01-01-1970", LocalDate.class));
    }


    @Test
    void coerceType_nullTypeCastSameType() {
        Object result = ClassUtils.coerceType(null, BigInteger.class);
        Constructor<WithNullable> constructor = ClassUtils.getAllArgsConstructor(WithNullable.class);

        WithNullable obj = ClassUtils.instantiate(constructor, result);
        System.out.println(obj);
    }

    @Test
    void coerceType_nullTypeCastDiffType() {
        Object result = ClassUtils.coerceType(null, BigDecimal.class);
        Constructor<WithNullable> constructor = ClassUtils.getAllArgsConstructor(WithNullable.class);

        WithNullable obj = ClassUtils.instantiate(constructor, result);
        System.out.println(obj);
    }
}
