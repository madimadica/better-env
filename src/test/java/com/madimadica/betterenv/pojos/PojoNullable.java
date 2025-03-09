package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoNullable {
    @Env("reference_String")
    private String referenceString;
    @Env(value="reference_NA", required = false)
    private String nullReference;

    public String getReferenceString() {
        return referenceString;
    }

    public String getNullReference() {
        return nullReference;
    }
}
