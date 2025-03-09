package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoMultipleTries {
    @Env({"reference_na1", "reference_na2", "reference_String"})
    private String s;

    public String getS() {
        return s;
    }
}
