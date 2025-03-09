package com.madimadica.betterenv;

import com.madimadica.betterenv.pojos.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// run configuration env:
// primitive_boolean=true;primitive_byte=1;primitive_char=A;primitive_double=8;primitive_float=7;primitive_int=3;primitive_long=4;primitive_short=2;reference_BigDecimal=0.123;reference_BigInteger=123;reference_Boolean=false;reference_Byte=9;reference_Character=Z;reference_Double=16;reference_Float=15;reference_Integer=11;reference_Long=12;reference_Short=10;reference_String=foo;reference_Blank=;reference_bar=bar
class BetterEnvTest {

    @Test
    void load_primitivesAndWrappers() {
        PojoPrimitiveAndWrapperTypes pojo = BetterEnv.load(PojoPrimitiveAndWrapperTypes.class);
        assertEquals(1, pojo.getPrimitiveByte());
        assertEquals(2, pojo.getPrimitiveShort());
        assertEquals(3, pojo.getPrimitiveInt());
        assertEquals(4L, pojo.getPrimitiveLong());
        assertEquals('A', pojo.getPrimitiveChar());
        assertTrue(pojo.getPrimitiveBoolean());
        assertEquals(7.0f, pojo.getPrimitiveFloat());
        assertEquals(8.0, pojo.getPrimitiveDouble());

        assertEquals(Byte.valueOf((byte) 9), pojo.getReferenceByte());
        assertEquals(Short.valueOf((short) 10), pojo.getReferenceShort());
        assertEquals(Integer.valueOf(11), pojo.getReferenceInteger());
        assertEquals(Long.valueOf(12), pojo.getReferenceLong());
        assertEquals(Character.valueOf('Z'), pojo.getReferenceCharacter());
        assertFalse(pojo.getReferenceBoolean());
        assertEquals(Float.valueOf(15.0f), pojo.getReferenceFloat());
        assertEquals(Double.valueOf(16.0), pojo.getReferenceDouble());
    }

    @Test
    void load_stringsAndMath() {
        PojoStringsAndMath pojo = BetterEnv.load(PojoStringsAndMath.class);
        assertEquals("foo", pojo.getReferenceString());
        assertEquals(new BigInteger("123"), pojo.getReferenceBigInteger());
        assertEquals(new BigDecimal("0.123"), pojo.getReferenceBigDecimal());
    }

    @Test
    void load_withNull() {
        PojoNullable pojo = BetterEnv.load(PojoNullable.class);
        assertEquals("foo", pojo.getReferenceString());
        assertNull(pojo.getNullReference());
    }

    @Test
    void load_withNullWithFallback() {
        PojoNullableFallback pojo = BetterEnv.load(PojoNullableFallback.class);
        assertEquals("foo", pojo.getReferenceString());
        assertEquals("hi", pojo.getReferenceFallback());
    }

    @Test
    void load_multipleTries() {
        PojoMultipleTries pojo = BetterEnv.load(PojoMultipleTries.class);
        assertEquals("foo", pojo.getS());
    }

    @Test
    void load_withBlank() {
        PojoWithBlank pojo = BetterEnv.load(PojoWithBlank.class);
        assertEquals("", pojo.getBlank());
    }

    @Test
    void load_withoutBlank() {
        assertThrows(InvalidEnvironmentException.class, () -> BetterEnv.load(PojoWithoutBlank.class));
    }

    @Test
    void load_someAnnotations() {
        PojoSomeAnnotations pojo = BetterEnv.load(PojoSomeAnnotations.class);
        assertEquals("foo", pojo.getReferenceString());
        assertNull(pojo.getX());
        assertNull(pojo.getY());
    }

    @Test
    void load_someAnnotationsOverloaded() {
        PojoSomeAnnotationsOverloaded pojo = BetterEnv.load(PojoSomeAnnotationsOverloaded.class);
        assertEquals("foo", pojo.getReferenceString());
        assertEquals("x", pojo.getX());
        assertEquals("y", pojo.getY());
    }


    @Test
    void load_someAnnotationsOverloadedFinal() {
        PojoSomeAnnotationsOverloadedFinal pojo = BetterEnv.load(PojoSomeAnnotationsOverloadedFinal.class);
        assertEquals("foo", pojo.getReferenceString());
        assertEquals("x", pojo.getX());
        assertEquals("y", pojo.getY());
    }

    @Test
    void load_allArgs() {
        PojoAnnotationsAllArgs pojo = BetterEnv.load(PojoAnnotationsAllArgs.class);
        assertEquals("foo", pojo.getS());
        assertEquals("bar", pojo.getBar());
    }

    @Test
    void load_allArgsFinal() {
        PojoAnnotationsAllArgsFinal pojo = BetterEnv.load(PojoAnnotationsAllArgsFinal.class);
        assertEquals("foo", pojo.getS());
        assertEquals("bar", pojo.getBar());
    }

    @Test
    void get() {
        assertFalse(BetterEnv.get("ref_na").isPresent());
        Optional<String> s = BetterEnv.get("reference_String");
        assertTrue(s.isPresent());
        assertEquals("foo", s.get());
    }

    @Test
    void getInt() {
        assertFalse(BetterEnv.get("primitive_integer").isPresent());
        Optional<Integer> x = BetterEnv.getInt("primitive_int");
        assertTrue(x.isPresent());
        assertEquals(3, x.get());
    }

    @Test
    void getLong() {
        assertFalse(BetterEnv.get("primitive_integer").isPresent());
        Optional<Long> x = BetterEnv.getLong("primitive_int");
        assertTrue(x.isPresent());
        assertEquals(3L, x.get());
    }
}

