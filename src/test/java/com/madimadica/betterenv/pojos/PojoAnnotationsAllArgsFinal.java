package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoAnnotationsAllArgsFinal {
    @Env("reference_String")
    private final String s;
    @Env("reference_bar")
    private final String bar;

    public PojoAnnotationsAllArgsFinal(String s, String bar) {
        this.s = s;
        this.bar = bar;
    }

    public String getS() {
        return s;
    }

    public String getBar() {
        return bar;
    }
}
