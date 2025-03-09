package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoSomeAnnotations {
    @Env("reference_String")
    private String referenceString;

    private String x;
    private String y;

    public String getReferenceString() {
        return referenceString;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}
