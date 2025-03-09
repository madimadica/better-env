package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

import java.math.BigDecimal;
import java.math.BigInteger;

public class PojoStringsAndMath {
    @Env("reference_String")
    private String referenceString;
    @Env("reference_BigInteger")
    private BigInteger referenceBigInteger;
    @Env("reference_BigDecimal")
    private BigDecimal referenceBigDecimal;

    public String getReferenceString() {
        return referenceString;
    }

    public BigInteger getReferenceBigInteger() {
        return referenceBigInteger;
    }

    public BigDecimal getReferenceBigDecimal() {
        return referenceBigDecimal;
    }
}
