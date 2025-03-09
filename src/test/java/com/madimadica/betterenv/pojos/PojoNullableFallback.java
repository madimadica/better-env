package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoNullableFallback {
    @Env("reference_String")
    private String referenceString;
    @Env(value="reference_NA", required = false)
    @Env.Fallback("hi")
    private String referenceFallback;

    public String getReferenceString() {
        return referenceString;
    }

    public String getReferenceFallback() {
        return referenceFallback;
    }
}
