package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoSomeAnnotationsOverloadedFinal {
    @Env("reference_String")
    private final String referenceString;
    private final String x;
    private final String y;

    public PojoSomeAnnotationsOverloadedFinal(String referenceString) {
        this.referenceString = referenceString;
        this.x = "x";
        this.y = "y";
    }

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
