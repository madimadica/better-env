package com.madimadica.betterenv.pojos;

import com.madimadica.betterenv.Env;

public class PojoWithBlank {
    @Env(value = "reference_Blank", allowBlank = true)
    String blank;

    public String getBlank() {
        return blank;
    }
}
