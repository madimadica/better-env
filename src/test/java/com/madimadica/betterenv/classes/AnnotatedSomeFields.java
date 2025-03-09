package com.madimadica.betterenv.classes;

public class AnnotatedSomeFields {
    @FieldAnnotation
    public int fPublic;
    @FieldAnnotation
    private int fPrivate;
    protected int fProtected;
    int fPackagePrivate;
}
