package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

// Use with run config
public class PojoPrimitiveAndWrapperTypes {
    @Env("primitive_byte")
    private byte primitiveByte;
    @Env("primitive_short")
    private short primitiveShort;
    @Env("primitive_int")
    private int primitiveInt;
    @Env("primitive_long")
    private long primitiveLong;
    @Env("primitive_char")
    private char primitiveChar;
    @Env("primitive_boolean")
    private boolean primitiveBoolean;
    @Env("primitive_float")
    private float primitiveFloat;
    @Env("primitive_double")
    private double primitiveDouble;

    @Env("reference_Byte")
    private Byte referenceByte;
    @Env("reference_Short")
    private Short referenceShort;
    @Env("reference_Integer")
    private Integer referenceInteger;
    @Env("reference_Long")
    private Long referenceLong;
    @Env("reference_Character")
    private Character referenceCharacter;
    @Env("reference_Boolean")
    private Boolean referenceBoolean;
    @Env("reference_Float")
    private Float referenceFloat;
    @Env("reference_Double")
    private Double referenceDouble;

    public byte getPrimitiveByte() {
        return primitiveByte;
    }

    public short getPrimitiveShort() {
        return primitiveShort;
    }

    public int getPrimitiveInt() {
        return primitiveInt;
    }

    public long getPrimitiveLong() {
        return primitiveLong;
    }

    public char getPrimitiveChar() {
        return primitiveChar;
    }

    public boolean getPrimitiveBoolean() {
        return primitiveBoolean;
    }

    public float getPrimitiveFloat() {
        return primitiveFloat;
    }

    public double getPrimitiveDouble() {
        return primitiveDouble;
    }

    public Byte getReferenceByte() {
        return referenceByte;
    }

    public Short getReferenceShort() {
        return referenceShort;
    }

    public Integer getReferenceInteger() {
        return referenceInteger;
    }

    public Long getReferenceLong() {
        return referenceLong;
    }

    public Character getReferenceCharacter() {
        return referenceCharacter;
    }

    public Boolean getReferenceBoolean() {
        return referenceBoolean;
    }

    public Float getReferenceFloat() {
        return referenceFloat;
    }

    public Double getReferenceDouble() {
        return referenceDouble;
    }
}
