package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoAnnotationsAllArgs {
    @Env("reference_String")
    private String s;
    @Env("reference_bar")
    private String bar;

    public PojoAnnotationsAllArgs(String s, String bar) {
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
