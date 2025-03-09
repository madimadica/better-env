package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoWithoutBlank {
    @Env("reference_Blank")
    private String blank;

    public String getBlank() {
        return blank;
    }
}
